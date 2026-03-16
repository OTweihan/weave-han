package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogCategory;
import com.han.blog.domain.BlogPost;
import com.han.blog.domain.bo.BlogCategoryBo;
import com.han.blog.domain.vo.BlogCategoryVo;
import com.han.blog.mapper.BlogCategoryMapper;
import com.han.blog.mapper.BlogPostMapper;
import com.han.blog.service.IBlogCategoryService;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-16
 * @Description: 博客分类Service业务层处理 blog_category
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogCategoryServiceImpl implements IBlogCategoryService {

    private final BlogCategoryMapper blogCategoryMapper;
    private final BlogPostMapper blogPostMapper;

    /**
     * 根据条件分页查询博客分类列表
     *
     * @param category 查询条件
     * @param pageQuery 分页参数
     * @return 博客分类列表
     */
    @Override
    public TableDataInfo<BlogCategoryVo> selectPageCategoryList(BlogCategoryBo category, PageQuery pageQuery) {
        Page<BlogCategoryVo> resultPage = blogCategoryMapper.selectVoPage(pageQuery.build(), this.buildQueryWrapper(category));
        return TableDataInfo.build(resultPage);
    }

    /**
     * 构建博客分类查询条件包装器
     *
     * @param category 博客分类业务对象，包含查询条件
     * @return 查询条件包装器
     */
    private Wrapper<BlogCategory> buildQueryWrapper(BlogCategoryBo category) {
        if (ObjectUtil.isNull(category)) {
            category = new BlogCategoryBo();
        }
        Map<String, Object> params = category.getParams();
        Long parentId = normalizeParentId(category.getParentId());
        LambdaQueryWrapper<BlogCategory> wrapper = Wrappers.lambdaQuery();
        // 只查询未删除的数据（逻辑删除标志为0）
        wrapper.eq(BlogCategory::getDelFlag, Boolean.FALSE)
            // 按分类ID精确匹配（条件存在时）
            .eq(ObjectUtil.isNotNull(category.getCategoryId()), BlogCategory::getCategoryId, category.getCategoryId())
            // 按分类名称模糊匹配（条件存在时）
            .like(StringUtils.isNotBlank(category.getName()), BlogCategory::getName, category.getName())
            // 按分类别名精确匹配（条件存在时）
            .eq(StringUtils.isNotBlank(category.getSlug()), BlogCategory::getSlug, category.getSlug())
            // 按父分类ID精确匹配（条件存在时）
            .eq(ObjectUtil.isNotNull(parentId), BlogCategory::getParentId, parentId)
            // 按创建时间范围匹配（beginTime和endTime都存在时）
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogCategory::getCreateTime, params.get("beginTime"), params.get("endTime"))
            // 默认按创建时间降序排列
            .orderByDesc(BlogCategory::getCreateTime);
        return wrapper;
    }

    /**
     * 查询博客分类详细信息
     *
     * @param categoryId 分类ID
     * @return 博客分类详细信息
     */
    @Override
    public BlogCategoryVo queryCategoryDetail(Long categoryId) {
        return blogCategoryMapper.selectVoById(categoryId);
    }

    /**
     * 根据分类别名查询分类
     *
     * @param slug 分类别名
     * @return 博客分类
     */
    @Override
    public BlogCategoryVo queryCategoryBySlug(String slug) {
        if (StringUtils.isBlank(slug)) {
            return null;
        }
        return blogCategoryMapper.selectVoOne(new LambdaQueryWrapper<BlogCategory>()
            .eq(BlogCategory::getDelFlag, Boolean.FALSE)
            .eq(BlogCategory::getSlug, slug));
    }

    /**
     * 新增博客分类
     *
     * @param category 博客分类业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCategory(BlogCategoryBo category) {
        category.setParentId(normalizeParentId(category.getParentId()));
        validateBeforeSave(category, false);
        BlogCategory blogCategory = MapstructUtils.convert(category, BlogCategory.class);
        try {
            return blogCategoryMapper.insert(blogCategory);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("新增分类'{}'失败，分类别名'{}'已存在", category.getName(), category.getSlug());
        }
    }

    /**
     * 修改博客分类
     *
     * @param category 博客分类业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCategory(BlogCategoryBo category) {
        if (ObjectUtil.isNull(category.getCategoryId())) {
            throw new ServiceException("分类ID不能为空");
        }
        if (ObjectUtil.isNull(blogCategoryMapper.selectById(category.getCategoryId()))) {
            throw new ServiceException("分类不存在或已删除");
        }
        category.setParentId(normalizeParentId(category.getParentId()));
        validateBeforeSave(category, true);
        BlogCategory blogCategory = MapstructUtils.convert(category, BlogCategory.class);
        // 更新分类
        int flag;
        try {
            flag = blogCategoryMapper.updateById(blogCategory);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("修改分类'{}'失败，分类别名'{}'已存在", category.getName(), category.getSlug());
        }

        if (flag < 1) {
            throw new ServiceException("修改分类{}失败", category.getName());
        }

        return flag;
    }

    /**
     * 批量删除博客分类
     *
     * @param categoryIds 分类ID列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCategories(Long[] categoryIds) {
        List<Long> ids = List.of(categoryIds);
        boolean hasChildCategory = blogCategoryMapper.exists(new LambdaQueryWrapper<BlogCategory>()
            .in(BlogCategory::getParentId, ids)
            .notIn(BlogCategory::getCategoryId, ids));
        if (hasChildCategory) {
            throw new ServiceException("存在子分类，不允许删除");
        }
        boolean hasPost = blogPostMapper.exists(new LambdaQueryWrapper<BlogPost>()
            .in(BlogPost::getCategoryId, ids)
            .eq(BlogPost::getDelFlag, SystemConstants.NORMAL));
        if (hasPost) {
            throw new ServiceException("分类下存在文章，不允许删除");
        }
        int flag = blogCategoryMapper.deleteByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除分类失败!");
        }
        return flag;
    }

    /**
     * 查询所有博客分类列表（不分页）
     *
     * @return 博客分类列表
     */
    @Override
    public List<BlogCategoryVo> selectAllCategories() {
        return blogCategoryMapper.selectVoList(new LambdaQueryWrapper<BlogCategory>()
            .eq(BlogCategory::getDelFlag, Boolean.FALSE)
            .orderByDesc(BlogCategory::getCreateTime));
    }

    /**
     * 保存前校验（唯一性、父级合法性）
     *
     * @param category 分类业务对象
     * @param isUpdate 是否为更新操作
     */
    private void validateBeforeSave(BlogCategoryBo category, boolean isUpdate) {
        if (blogCategoryMapper.selectSlugCount(category.getSlug(), category.getCategoryId()) > 0) {
            throw new ServiceException("分类别名'{}'已存在", category.getSlug());
        }
        Long parentId = category.getParentId();
        if (ObjectUtil.isNull(parentId)) {
            return;
        }
        if (isUpdate && parentId.equals(category.getCategoryId())) {
            throw new ServiceException("上级分类不能选择自己");
        }
        BlogCategory parent = blogCategoryMapper.selectById(parentId);
        if (ObjectUtil.isNull(parent)) {
            throw new ServiceException("上级分类不存在或已删除");
        }
        if (isUpdate && hasCircularParent(category.getCategoryId(), parentId)) {
            throw new ServiceException("上级分类不能选择当前分类的下级");
        }
    }

    /**
     * 判断是否存在循环父子关系
     *
     * @param categoryId 当前分类ID
     * @param parentId 上级分类ID
     * @return true存在循环
     */
    private boolean hasCircularParent(Long categoryId, Long parentId) {
        Set<Long> visited = new HashSet<>();
        Long currentParentId = parentId;
        while (ObjectUtil.isNotNull(currentParentId) && visited.add(currentParentId)) {
            if (currentParentId.equals(categoryId)) {
                return true;
            }
            BlogCategory parent = blogCategoryMapper.selectById(currentParentId);
            if (ObjectUtil.isNull(parent)) {
                break;
            }
            currentParentId = normalizeParentId(parent.getParentId());
        }
        return false;
    }

    /**
     * 统一父ID：前端可能传0表示顶级，这里归一化为null
     *
     * @param parentId 父分类ID
     * @return 归一化后的父分类ID
     */
    private Long normalizeParentId(Long parentId) {
        if (ObjectUtil.isNull(parentId) || parentId <= 0) {
            return null;
        }
        return parentId;
    }
}

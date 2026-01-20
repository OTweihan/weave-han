package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogCategory;
import com.han.blog.domain.bo.BlogCategoryBo;
import com.han.blog.domain.vo.BlogCategoryVo;
import com.han.blog.mapper.BlogCategoryMapper;
import com.han.blog.service.IBlogCategoryService;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
        Map<String, Object> params = category.getParams();
        LambdaQueryWrapper<BlogCategory> wrapper = Wrappers.lambdaQuery();
        // 只查询未删除的数据（逻辑删除标志为0）
        wrapper.eq(BlogCategory::getDelFlag, SystemConstants.NORMAL)
            // 按分类ID精确匹配（条件存在时）
            .eq(ObjectUtil.isNotNull(category.getCategoryId()), BlogCategory::getCategoryId, category.getCategoryId())
            // 按分类名称模糊匹配（条件存在时）
            .like(ObjectUtil.isNotNull(category.getName()), BlogCategory::getName, category.getName())
            // 按分类别名精确匹配（条件存在时）
            .eq(ObjectUtil.isNotNull(category.getSlug()), BlogCategory::getSlug, category.getSlug())
            // 按父分类ID精确匹配（条件存在时）
            .eq(ObjectUtil.isNotNull(category.getParentId()), BlogCategory::getParentId, category.getParentId())
            // 按创建时间范围匹配（beginTime和endTime都存在时）
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogCategory::getCreateTime, params.get("beginTime"), params.get("endTime"))
            // 按排序字段升序排列，再按创建时间升序排列
            .orderByAsc(BlogCategory::getSortOrder)
            .orderByAsc(BlogCategory::getCreateTime);
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
        return blogCategoryMapper.selectVoOne(new LambdaQueryWrapper<BlogCategory>()
            .eq(BlogCategory::getDelFlag, SystemConstants.NORMAL)
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
        BlogCategory blogCategory = MapstructUtils.convert(category, BlogCategory.class);
        return blogCategoryMapper.insert(blogCategory);
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
        BlogCategory blogCategory = MapstructUtils.convert(category, BlogCategory.class);
        // 更新分类
        int flag = blogCategoryMapper.updateById(blogCategory);

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
            .eq(BlogCategory::getDelFlag, SystemConstants.NORMAL)
            .orderByAsc(BlogCategory::getSortOrder)
            .orderByAsc(BlogCategory::getCreateTime));
    }
}

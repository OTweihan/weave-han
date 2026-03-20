package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogPostTag;
import com.han.blog.domain.BlogTag;
import com.han.blog.domain.bo.BlogTagBo;
import com.han.blog.domain.vo.BlogTagVo;
import com.han.blog.mapper.BlogPostTagMapper;
import com.han.blog.mapper.BlogTagMapper;
import com.han.blog.service.IBlogTagService;
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

import java.util.List;
import java.util.Map;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客标签Service业务层处理 blog_tag
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogTagServiceImpl implements IBlogTagService {

    private static final String DEFAULT_TAG_COLOR = "#1890ff";

    private final BlogTagMapper blogTagMapper;
    private final BlogPostTagMapper blogPostTagMapper;

    /**
     * 根据条件分页查询博客标签列表
     *
     * @param tag      查询条件
     * @param pageQuery 分页参数
     * @return 博客标签列表
     */
    @Override
    public TableDataInfo<BlogTagVo> selectPageTagList(BlogTagBo tag, PageQuery pageQuery) {
        Page<BlogTagVo> resultPage = blogTagMapper.selectPageList(pageQuery.build(), this.buildQueryWrapper(tag));
        return TableDataInfo.build(resultPage);
    }

    /**
     * 构建博客标签查询条件包装器
     *
     * @param tag 博客标签业务对象，包含查询条件
     * @return 查询条件包装器
     */
    private Wrapper<BlogTag> buildQueryWrapper(BlogTagBo tag) {
        if (ObjectUtil.isNull(tag)) {
            tag = new BlogTagBo();
        }
        Map<String, Object> params = tag.getParams();
        LambdaQueryWrapper<BlogTag> wrapper = Wrappers.lambdaQuery();
        // 按标签ID精确匹配（条件存在时）
        wrapper.eq(ObjectUtil.isNotNull(tag.getTagId()), BlogTag::getTagId, tag.getTagId())
            // 按标签名称模糊匹配（条件存在时）
            .like(StringUtils.isNotBlank(tag.getName()), BlogTag::getName, tag.getName())
            // 按标签别名精确匹配（条件存在时）
            .eq(StringUtils.isNotBlank(tag.getSlug()), BlogTag::getSlug, tag.getSlug())
            // 按创建时间范围匹配（beginTime和endTime都存在时）
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogTag::getCreateTime, params.get("beginTime"), params.get("endTime"))
            // 按文章数量、创建时间降序排列
            .orderByDesc(BlogTag::getPostCount)
            .orderByDesc(BlogTag::getCreateTime);
        return wrapper;
    }

    /**
     * 查询博客标签详细信息
     *
     * @param tagId 标签ID
     * @return 博客标签详细信息
     */
    @Override
    public BlogTagVo queryTagDetail(Long tagId) {
        return blogTagMapper.selectVoById(tagId);
    }

    /**
     * 根据标签别名查询标签
     *
     * @param slug 标签别名
     * @return 博客标签
     */
    @Override
    public BlogTagVo queryTagBySlug(String slug) {
        if (StringUtils.isBlank(slug)) {
            return null;
        }
        return blogTagMapper.selectVoOne(new LambdaQueryWrapper<BlogTag>()
            .eq(BlogTag::getSlug, slug));
    }

    /**
     * 新增博客标签
     *
     * @param tag 博客标签业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTag(BlogTagBo tag) {
        normalizeBeforeSave(tag);
        validateBeforeSave(tag);
        BlogTag blogTag = MapstructUtils.convert(tag, BlogTag.class);
        try {
            return blogTagMapper.insert(blogTag);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("新增标签'{}'失败，标签名称或标签别名已存在", tag.getName());
        }
    }

    /**
     * 修改博客标签
     *
     * @param tag 博客标签业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTag(BlogTagBo tag) {
        if (ObjectUtil.isNull(tag.getTagId())) {
            throw new ServiceException("标签ID不能为空");
        }
        if (ObjectUtil.isNull(blogTagMapper.selectById(tag.getTagId()))) {
            throw new ServiceException("标签不存在");
        }
        normalizeBeforeSave(tag);
        validateBeforeSave(tag);
        BlogTag blogTag = MapstructUtils.convert(tag, BlogTag.class);
        // 更新标签
        int flag;
        try {
            flag = blogTagMapper.updateById(blogTag);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("修改标签'{}'失败，标签名称或标签别名已存在", tag.getName());
        }

        if (flag < 1) {
            throw new ServiceException("修改标签{}失败", tag.getName());
        }

        return flag;
    }

    /**
     * 批量删除博客标签
     *
     * @param tagIds 标签ID列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTags(Long[] tagIds) {
        List<Long> ids = List.of(tagIds);
        boolean tagInUse = blogPostTagMapper.exists(new LambdaQueryWrapper<BlogPostTag>()
            .in(BlogPostTag::getTagId, ids));
        if (tagInUse) {
            throw new ServiceException("标签已被文章或草稿使用，不能删除");
        }
        int flag = blogTagMapper.deleteByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除标签失败!");
        }
        return flag;
    }

    /**
     * 查询所有博客标签列表（不分页）
     *
     * @return 博客标签列表
     */
    @Override
    public List<BlogTagVo> selectAllTags() {
        return blogTagMapper.selectVoList(new LambdaQueryWrapper<BlogTag>()
            .orderByDesc(BlogTag::getPostCount)
            .orderByDesc(BlogTag::getCreateTime));
    }

    /**
     * 保存前统一处理字段值
     *
     * @param tag 标签业务对象
     */
    private void normalizeBeforeSave(BlogTagBo tag) {
        if (StringUtils.isBlank(tag.getColor())) {
            tag.setColor(DEFAULT_TAG_COLOR);
        }
    }

    /**
     * 保存前校验（名称、别名唯一）
     *
     * @param tag 标签业务对象
     */
    private void validateBeforeSave(BlogTagBo tag) {
        if (checkTagNameUnique(tag)) {
            throw new ServiceException("标签名称'{}'已存在", tag.getName());
        }
        if (checkTagSlugUnique(tag)) {
            throw new ServiceException("标签别名'{}'已存在", tag.getSlug());
        }
    }

    /**
     * 校验标签名称是否重复
     *
     * @param tag 标签业务对象
     * @return true重复
     */
    private boolean checkTagNameUnique(BlogTagBo tag) {
        return blogTagMapper.exists(new LambdaQueryWrapper<BlogTag>()
            .eq(BlogTag::getName, tag.getName())
            .ne(ObjectUtil.isNotNull(tag.getTagId()), BlogTag::getTagId, tag.getTagId()));
    }

    /**
     * 校验标签别名是否重复
     *
     * @param tag 标签业务对象
     * @return true重复
     */
    private boolean checkTagSlugUnique(BlogTagBo tag) {
        return blogTagMapper.exists(new LambdaQueryWrapper<BlogTag>()
            .eq(BlogTag::getSlug, tag.getSlug())
            .ne(ObjectUtil.isNotNull(tag.getTagId()), BlogTag::getTagId, tag.getTagId()));
    }
}

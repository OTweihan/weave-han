package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogTag;
import com.han.blog.domain.bo.BlogTagBo;
import com.han.blog.domain.vo.BlogTagVo;
import com.han.blog.mapper.BlogTagMapper;
import com.han.blog.service.IBlogTagService;
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
 * @CreateTime: 2026-01-14
 * @Description: 博客标签Service业务层处理 blog_tag
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogTagServiceImpl implements IBlogTagService {

    private final BlogTagMapper blogTagMapper;

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
        Map<String, Object> params = tag.getParams();
        LambdaQueryWrapper<BlogTag> wrapper = Wrappers.lambdaQuery();
        // 按标签ID精确匹配（条件存在时）
        wrapper.eq(ObjectUtil.isNotNull(tag.getTagId()), BlogTag::getTagId, tag.getTagId())
            // 按标签名称模糊匹配（条件存在时）
            .like(ObjectUtil.isNotNull(tag.getName()), BlogTag::getName, tag.getName())
            // 按标签别名精确匹配（条件存在时）
            .eq(ObjectUtil.isNotNull(tag.getSlug()), BlogTag::getSlug, tag.getSlug())
            // 按创建时间范围匹配（beginTime和endTime都存在时）
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogTag::getCreateTime, params.get("beginTime"), params.get("endTime"))
            // 按创建时间升序排列
            .orderByAsc(BlogTag::getCreateTime);
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
        BlogTag blogTag = MapstructUtils.convert(tag, BlogTag.class);
        return blogTagMapper.insert(blogTag);
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
        BlogTag blogTag = MapstructUtils.convert(tag, BlogTag.class);
        // 更新标签
        int flag = blogTagMapper.updateById(blogTag);

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
            .orderByAsc(BlogTag::getCreateTime));
    }
}

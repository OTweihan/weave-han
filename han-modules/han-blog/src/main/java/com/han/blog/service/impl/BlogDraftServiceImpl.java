package com.han.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogDraft;
import com.han.blog.domain.BlogDraftTag;
import com.han.blog.domain.BlogTag;
import com.han.blog.domain.bo.BlogDraftBo;
import com.han.blog.domain.vo.BlogDraftVo;
import com.han.blog.domain.vo.BlogTagVo;
import com.han.blog.mapper.BlogDraftMapper;
import com.han.blog.mapper.BlogDraftTagMapper;
import com.han.blog.mapper.BlogTagMapper;
import com.han.blog.service.IBlogDraftService;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客草稿Service业务层处理 blog_draft
 */
@RequiredArgsConstructor
@Service
public class BlogDraftServiceImpl implements IBlogDraftService {

    private final BlogDraftMapper blogDraftMapper;
    private final BlogDraftTagMapper blogDraftTagMapper;
    private final BlogTagMapper blogTagMapper;

    /**
     * 查询博客草稿列表（分页）
     */
    @Override
    public TableDataInfo<BlogDraftVo> selectPageDraftList(BlogDraftBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlogDraft> lqw = buildQueryWrapper(bo);
        Page<BlogDraftVo> result = blogDraftMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询博客草稿详细信息
     */
    @Override
    public BlogDraftVo queryDraftDetail(Long draftId) {
        BlogDraftVo vo = blogDraftMapper.selectVoById(draftId);
        if (vo != null) {
            // 查询标签列表
            List<BlogDraftTag> draftTags = blogDraftTagMapper.selectList(new LambdaQueryWrapper<BlogDraftTag>()
                .eq(BlogDraftTag::getDraftId, draftId));
            if (CollUtil.isNotEmpty(draftTags)) {
                List<Long> tagIds = draftTags.stream().map(BlogDraftTag::getTagId).toList();
                List<BlogTagVo> tags = blogTagMapper.selectVoList(new LambdaQueryWrapper<BlogTag>()
                    .in(BlogTag::getTagId, tagIds));
                vo.setTags(tags);
            }
        }
        return vo;
    }

    /**
     * 新增博客草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDraft(BlogDraftBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        BlogDraft add = MapstructUtils.convert(bo, BlogDraft.class);
        int rows = blogDraftMapper.insert(add);
        if (rows > 0) {
            // 保存标签关联
            saveBatchDraftTag(add.getDraftId(), bo.getTagIds());
        }
        return rows;
    }

    /**
     * 修改博客草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDraft(BlogDraftBo bo) {
        BlogDraft update = MapstructUtils.convert(bo, BlogDraft.class);
        int rows = blogDraftMapper.updateById(update);
        if (rows > 0) {
            Long draftId = bo.getDraftId();
            // 更新标签关联
            blogDraftTagMapper.delete(new LambdaQueryWrapper<BlogDraftTag>().eq(BlogDraftTag::getDraftId, draftId));
            saveBatchDraftTag(draftId, bo.getTagIds());
        }
        return rows;
    }

    /**
     * 批量删除博客草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDrafts(Long[] draftIds) {
        List<Long> ids = List.of(draftIds);
        int rows = blogDraftMapper.deleteByIds(ids);
        if (rows > 0) {
            blogDraftTagMapper.delete(new LambdaQueryWrapper<BlogDraftTag>().in(BlogDraftTag::getDraftId, ids));
        }
        return rows;
    }

    private LambdaQueryWrapper<BlogDraft> buildQueryWrapper(BlogDraftBo bo) {
        LambdaQueryWrapper<BlogDraft> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, BlogDraft::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getTitle()), BlogDraft::getTitle, bo.getTitle());
        lqw.eq(bo.getCategoryId() != null, BlogDraft::getCategoryId, bo.getCategoryId());
        return lqw;
    }

    /**
     * 批量保存草稿标签关联
     */
    private void saveBatchDraftTag(Long draftId, List<Long> tagIds) {
        if (CollUtil.isEmpty(tagIds)) {
            return;
        }
        List<BlogDraftTag> list = new ArrayList<>();
        Long userId = LoginHelper.getUserId();
        Date now = new Date();
        for (Long tagId : tagIds) {
            BlogDraftTag dt = new BlogDraftTag();
            dt.setDraftId(draftId);
            dt.setTagId(tagId);
            dt.setCreateBy(userId);
            dt.setCreateTime(now);
            list.add(dt);
        }
        blogDraftTagMapper.insertBatch(list);
    }
}

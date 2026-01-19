package com.han.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogDraft;
import com.han.blog.domain.bo.BlogDraftBo;
import com.han.blog.domain.vo.BlogDraftVo;
import com.han.blog.mapper.BlogDraftMapper;
import com.han.blog.service.IBlogDraftService;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        return blogDraftMapper.selectVoById(draftId);
    }

    /**
     * 新增博客草稿
     */
    @Override
    public int insertDraft(BlogDraftBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        BlogDraft add = MapstructUtils.convert(bo, BlogDraft.class);
        return blogDraftMapper.insert(add);
    }

    /**
     * 修改博客草稿
     */
    @Override
    public int updateDraft(BlogDraftBo bo) {
        BlogDraft update = MapstructUtils.convert(bo, BlogDraft.class);
        return blogDraftMapper.updateById(update);
    }

    /**
     * 批量删除博客草稿
     */
    @Override
    public int deleteDrafts(Long[] draftIds) {
        return blogDraftMapper.deleteBatchIds(List.of(draftIds));
    }

    private LambdaQueryWrapper<BlogDraft> buildQueryWrapper(BlogDraftBo bo) {
        LambdaQueryWrapper<BlogDraft> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, BlogDraft::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getTitle()), BlogDraft::getTitle, bo.getTitle());
        lqw.eq(bo.getCategoryId() != null, BlogDraft::getCategoryId, bo.getCategoryId());
        return lqw;
    }
}

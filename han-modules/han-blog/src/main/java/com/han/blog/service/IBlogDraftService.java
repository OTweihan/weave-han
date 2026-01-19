package com.han.blog.service;

import com.han.blog.domain.bo.BlogDraftBo;
import com.han.blog.domain.vo.BlogDraftVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客草稿Service接口 blog_draft
 */
public interface IBlogDraftService {

    /**
     * 查询博客草稿列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 博客草稿列表
     */
    TableDataInfo<BlogDraftVo> selectPageDraftList(BlogDraftBo bo, PageQuery pageQuery);

    /**
     * 查询博客草稿详细信息
     *
     * @param draftId 草稿ID
     * @return 博客草稿详细信息
     */
    BlogDraftVo queryDraftDetail(Long draftId);

    /**
     * 新增博客草稿
     *
     * @param draft 博客草稿业务对象
     * @return 结果
     */
    int insertDraft(BlogDraftBo draft);

    /**
     * 修改博客草稿
     *
     * @param draft 博客草稿业务对象
     * @return 结果
     */
    int updateDraft(BlogDraftBo draft);

    /**
     * 批量删除博客草稿
     *
     * @param draftIds 草稿ID列表
     * @return 结果
     */
    int deleteDrafts(Long[] draftIds);
}

package com.han.blog.service;

import com.han.blog.domain.bo.BlogCommentBo;
import com.han.blog.domain.vo.BlogCommentVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客评论Service接口 blog_comment
 */
public interface IBlogCommentService {

    /**
     * 查询博客评论列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 博客评论列表
     */
    TableDataInfo<BlogCommentVo> selectPageCommentList(BlogCommentBo bo, PageQuery pageQuery);

    /**
     * 查询单条博客评论详细信息
     *
     * @param commentId 评论ID
     * @return 博客评论详细信息
     */
    BlogCommentVo queryCommentDetail(Long commentId);

    /**
     * 新增博客评论
     *
     * @param comment 博客评论业务对象
     * @return 影响行数
     */
    int insertComment(BlogCommentBo comment);

    /**
     * 修改博客评论
     *
     * @param comment 博客评论业务对象
     * @return 影响行数
     */
    int updateComment(BlogCommentBo comment);

    /**
     * 批量删除博客评论
     *
     * @param commentIds 评论ID列表
     * @return 影响行数
     */
    int deleteComments(Long[] commentIds);
}

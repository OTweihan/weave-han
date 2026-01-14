package com.han.blog.service;

import com.han.blog.domain.bo.BlogPostBo;
import com.han.blog.domain.vo.BlogPostVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客文章Service接口 blog_post
 */
public interface IBlogPostService {

    /**
     * 查询博客文章列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 博客文章列表
     */
    TableDataInfo<BlogPostVo> selectPagePostList(BlogPostBo bo, PageQuery pageQuery);

    /**
     * 查询博客文章详细信息
     *
     * @param postId 文章ID
     * @return 博客文章详细信息
     */
    BlogPostVo queryPostDetail(Long postId);

    /**
     * 根据文章别名查询文章
     *
     * @param slug 文章别名
     * @return 博客文章
     */
    BlogPostVo queryPostBySlug(String slug);

    /**
     * 新增博客文章
     *
     * @param post 博客文章业务对象
     * @return 结果
     */
    int insertPost(BlogPostBo post);

    /**
     * 修改博客文章
     *
     * @param post 博客文章业务对象
     * @return 结果
     */
    int updatePost(BlogPostBo post);

    /**
     * 批量删除博客文章
     *
     * @param postIds 文章ID列表
     * @return 结果
     */
    int deletePosts(Long[] postIds);

    /**
     * 更新文章浏览量
     *
     * @param postId 文章ID
     * @return 结果
     */
    int incrementViewCount(Long postId);

    /**
     * 更新文章点赞数
     *
     * @param postId 文章ID
     * @param increment 增量
     * @return 结果
     */
    int incrementLikeCount(Long postId, Integer increment);
}

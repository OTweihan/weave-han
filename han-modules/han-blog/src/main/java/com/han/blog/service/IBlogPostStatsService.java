package com.han.blog.service;

import com.han.blog.domain.vo.BlogPostStatsVo;

import java.util.List;
import java.util.Map;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客文章统计Service接口
 */
public interface IBlogPostStatsService {

    /**
     * 根据文章ID查询统计信息
     *
     * @param postId 文章ID
     * @return 统计信息Vo
     */
    BlogPostStatsVo queryStatsByPostId(Long postId);

    /**
     * 批量查询文章统计信息
     *
     * @param postIds 文章ID列表
     * @return 统计信息Map，key为postId，value为StatsVo
     */
    Map<Long, BlogPostStatsVo> queryStatsByPostIds(List<Long> postIds);

    /**
     * 增加浏览量
     *
     * @param postId 文章ID
     * @return 结果
     */
    int incrementViewCount(Long postId);

    /**
     * 减少点赞数
     *
     * @param postId 文章ID
     * @return 结果
     */
    int decrementLikeCount(Long postId);

    /**
     * 增加评论数
     *
     * @param postId 文章ID
     * @return 结果
     */
    int incrementCommentCount(Long postId);

    /**
     * 减少评论数
     *
     * @param postId 文章ID
     * @return 结果
     */
    int decrementCommentCount(Long postId);

    /**
     * 初始化文章统计信息（当文章创建时调用）
     *
     * @param postId 文章ID
     */
    void initStats(Long postId);

    /**
     * 删除文章统计信息
     *
     * @param postIds 文章ID列表
     */
    void deleteStats(Long[] postIds);
}

package com.han.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.han.blog.domain.BlogPostStats;
import com.han.blog.domain.vo.BlogPostStatsVo;
import com.han.blog.mapper.BlogPostStatsMapper;
import com.han.blog.service.IBlogPostStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客文章统计Service业务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogPostStatsServiceImpl implements IBlogPostStatsService {

    private final BlogPostStatsMapper blogPostStatsMapper;

    /**
     * 根据文章ID查询统计信息
     *
     * @param postId 文章ID
     * @return 统计信息Vo
     */
    @Override
    public BlogPostStatsVo queryStatsByPostId(Long postId) {
        return blogPostStatsMapper.selectVoById(postId);
    }

    /**
     * 批量查询文章统计信息
     *
     * @param postIds 文章ID列表
     * @return 统计信息Map，key为postId，value为StatsVo
     */
    @Override
    public Map<Long, BlogPostStatsVo> queryStatsByPostIds(List<Long> postIds) {
        if (CollUtil.isEmpty(postIds)) {
            return Collections.emptyMap();
        }
        List<BlogPostStatsVo> statsList = blogPostStatsMapper.selectVoList(
            new LambdaQueryWrapper<BlogPostStats>().in(BlogPostStats::getPostId, postIds)
        );
        if (CollUtil.isEmpty(statsList)) {
            return Collections.emptyMap();
        }
        return statsList.stream().collect(Collectors.toMap(BlogPostStatsVo::getPostId, v -> v));
    }

    /**
     * 增加浏览量
     *
     * @param postId 文章ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long postId) {
        return blogPostStatsMapper.update(null,
            Wrappers.<BlogPostStats>lambdaUpdate()
                .eq(BlogPostStats::getPostId, postId)
                .setSql("view_count = view_count + 1"));
    }

    /**
     * 减少点赞数
     *
     * @param postId 文章ID
     * @return 结果
     */
    @Override
    public int decrementLikeCount(Long postId) {
        return blogPostStatsMapper.update(null,
            Wrappers.<BlogPostStats>lambdaUpdate()
                .eq(BlogPostStats::getPostId, postId)
                .setSql("like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END"));
    }

    /**
     * 增加评论数
     *
     * @param postId 文章ID
     * @return 结果
     */
    @Override
    public int incrementCommentCount(Long postId) {
        return blogPostStatsMapper.update(null,
            Wrappers.<BlogPostStats>lambdaUpdate()
                .eq(BlogPostStats::getPostId, postId)
                .setSql("comment_count = comment_count + 1"));
    }

    /**
     * 减少评论数
     *
     * @param postId 文章ID
     * @return 结果
     */
    @Override
    public int decrementCommentCount(Long postId) {
        return blogPostStatsMapper.update(null,
            Wrappers.<BlogPostStats>lambdaUpdate()
                .eq(BlogPostStats::getPostId, postId)
                .setSql("comment_count = CASE WHEN comment_count > 0 THEN comment_count - 1 ELSE 0 END"));
    }

    /**
     * 初始化文章统计信息（当文章创建时调用）
     *
     * @param postId 文章ID
     */
    @Override
    public void initStats(Long postId) {
        BlogPostStats stats = new BlogPostStats();
        stats.setPostId(postId);
        stats.setViewCount(0);
        stats.setLikeCount(0);
        stats.setCommentCount(0);
        blogPostStatsMapper.insert(stats);
    }

    /**
     * 删除文章统计信息
     *
     * @param postIds 文章ID列表
     */
    @Override
    public void deleteStats(Long[] postIds) {
        if (postIds == null || postIds.length == 0) {
            return;
        }
        blogPostStatsMapper.delete(Wrappers.<BlogPostStats>lambdaQuery().in(BlogPostStats::getPostId, List.of(postIds)));
    }
}

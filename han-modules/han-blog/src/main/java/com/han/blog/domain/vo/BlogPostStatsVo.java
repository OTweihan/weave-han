package com.han.blog.domain.vo;

import com.han.blog.domain.BlogPostStats;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客文章统计视图对象 blog_post_stats
 */
@Data
@AutoMapper(target = BlogPostStats.class)
public class BlogPostStatsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 更新时间
     */
    private Date updateTime;
}
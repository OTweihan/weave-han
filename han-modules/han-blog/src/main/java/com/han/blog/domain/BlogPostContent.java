package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客文章内容表
 */
@Data
@NoArgsConstructor
@TableName("blog_post_content")
public class BlogPostContent {

    /**
     * 文章ID
     */
    @TableId
    private Long postId;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 渲染后的HTML内容
     */
    private String contentHtml;

    /**
     * 字数统计
     */
    private Integer wordCount;

    /**
     * 预计阅读时间（分钟）
     */
    private Integer readingTime;
}

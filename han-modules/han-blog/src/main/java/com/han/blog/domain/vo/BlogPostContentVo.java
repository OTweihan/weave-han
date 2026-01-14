package com.han.blog.domain.vo;

import com.han.blog.domain.BlogPostContent;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客文章内容视图对象 blog_post_content
 */
@Data
@AutoMapper(target = BlogPostContent.class)
public class BlogPostContentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
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
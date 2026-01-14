package com.han.blog.domain.bo;

import com.han.blog.domain.BlogPostContent;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客文章内容业务对象 blog_post_content
 */
@Data
@NoArgsConstructor
@AutoMapper(target = BlogPostContent.class, reverseConvertGenerate = false)
public class BlogPostContentBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @NotBlank(message = "文章ID不能为空")
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
package com.han.blog.domain.bo;

import com.han.blog.domain.BlogPost;
import com.han.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客文章业务对象 blog_post
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlogPost.class, reverseConvertGenerate = false)
public class BlogPostBo extends BaseEntity {

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 300, message = "文章标题不能超过300个字符")
    private String title;

    /**
     * 文章别名（URL友好）
     */
    @Size(max = 200, message = "文章别名不能超过200个字符")
    private String slug;

    /**
     * 文章摘要
     */
    @Size(max = 800, message = "文章摘要不能超过800个字符")
    private String summary;

    /**
     * 封面图片ID
     */
    private Long coverImage;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 状态（0草稿 1发布 2下架 3回收站）
     */
    private String status;

    /**
     * 是否置顶（0否 1是）
     */
    private String isTop;

    /**
     * 是否推荐（0否 1是）
     */
    private String isFeatured;

    /**
     * 允许评论（0否 1是）
     */
    private String allowComment;

    /**
     * 文章访问密码
     */
    @Size(max = 100, message = "文章访问密码不能超过100个字符")
    private String password;

    /**
     * 来源类型（ORIGINAL原创 REPRINT转载 TRANSLATION翻译）
     */
    @Size(max = 20, message = "来源类型不能超过20个字符")
    private String sourceType;

    /**
     * 原文链接
     */
    @Size(max = 500, message = "原文链接不能超过500个字符")
    private String sourceUrl;

    /**
     * SEO关键词
     */
    @Size(max = 200, message = "SEO关键词不能超过200个字符")
    private String seoKeywords;

    /**
     * SEO描述
     */
    @Size(max = 300, message = "SEO描述不能超过300个字符")
    private String seoDescription;

    /**
     * 发布时间
     */
    private Date publishedTime;

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

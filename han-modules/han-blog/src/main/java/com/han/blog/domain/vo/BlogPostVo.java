package com.han.blog.domain.vo;

import com.han.blog.domain.BlogPost;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客文章视图对象 blog_post
 */
@Data
@AutoMapper(target = BlogPost.class)
public class BlogPostVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章别名（URL友好）
     */
    private String slug;

    /**
     * 文章摘要
     */
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
     * 作者昵称
     */
    private String authorName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类信息
     */
    private BlogCategoryVo category;

    /**
     * 标签列表
     */
    private List<BlogTagVo> tags;

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
     * 浏览量
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 文章访问密码
     */
    private String password;

    /**
     * 来源类型（ORIGINAL原创 REPRINT转载 TRANSLATION翻译）
     */
    private String sourceType;

    /**
     * 原文链接
     */
    private String sourceUrl;

    /**
     * SEO关键词
     */
    private String seoKeywords;

    /**
     * SEO描述
     */
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

package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.han.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客文章表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("blog_post")
public class BlogPost extends BaseEntity {

    /**
     * 文章ID
     */
    @TableId
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
     * 文章内容
     */
    private String content;

    /**
     * 渲染后的HTML内容
     */
    private String contentHtml;

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
     * 字数统计
     */
    private Integer wordCount;

    /**
     * 预计阅读时间（分钟）
     */
    private Integer readingTime;

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
     * 最后评论时间
     */
    private Date lastCommentTime;

    /**
     * 删除标志（0存在 1删除）
     */
    private String delFlag;
}

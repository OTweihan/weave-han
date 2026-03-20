package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.han.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
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
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String slug;

    /**
     * 文章摘要
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String summary;

    /**
     * 封面图片ID
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long coverImage;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 分类ID
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long categoryId;

    /**
     * 状态（0草稿 1发布 2下架 3回收站）
     */
    private Integer status;

    /**
     * 是否置顶（0否 1是）
     */
    private Integer isTop;

    /**
     * 是否推荐（0否 1是）
     */
    private Integer isFeatured;

    /**
     * 允许评论（0否 1是）
     */
    private Integer allowComment;

    /**
     * 文章访问密码
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String password;

    /**
     * 来源类型（ORIGINAL原创 REPRINT转载 TRANSLATION翻译）
     */
    private String sourceType;

    /**
     * 原文链接
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String sourceUrl;

    /**
     * SEO关键词
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String seoKeywords;

    /**
     * SEO描述
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String seoDescription;

    /**
     * 发布时间
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Date publishedTime;

    /**
     * 最后评论时间
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Date lastCommentTime;

    /**
     * 删除标志（0存在 1删除）
     */
    @TableLogic
    private Integer delFlag;
}

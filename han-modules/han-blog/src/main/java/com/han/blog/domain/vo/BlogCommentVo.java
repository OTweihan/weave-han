package com.han.blog.domain.vo;

import com.han.blog.domain.BlogComment;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客评论视图对象 blog_comment
 */
@Data
@AutoMapper(target = BlogComment.class)
public class BlogCommentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 用户ID（登录用户）
     */
    private Long userId;

    /**
     * 用户名（游客）
     */
    private String userName;

    /**
     * 邮箱（游客）
     */
    private String userEmail;

    /**
     * IP地址（支持IPv6）
     */
    private String userIp;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 状态（0待审核 1通过 2拒绝）
     */
    private String status;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 删除标志（0存在 1删除）
     */
    private Integer delFlag;
}


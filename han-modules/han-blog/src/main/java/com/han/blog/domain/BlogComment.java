package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.han.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客评论表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("blog_comment")
public class BlogComment extends BaseEntity {

    /**
     * 评论ID
     */
    @TableId
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
    private String delFlag;
}


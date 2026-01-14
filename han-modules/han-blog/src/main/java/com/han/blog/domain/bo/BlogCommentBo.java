package com.han.blog.domain.bo;

import com.han.blog.domain.BlogComment;
import com.han.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客评论业务对象 blog_comment
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlogComment.class, reverseConvertGenerate = false)
public class BlogCommentBo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 文章ID
     */
    @NotBlank(message = "文章ID不能为空")
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
    @Size(max = 30, message = "用户名不能超过30个字符")
    private String userName;

    /**
     * 邮箱（游客）
     */
    @Size(max = 100, message = "邮箱不能超过100个字符")
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
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过2000个字符")
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

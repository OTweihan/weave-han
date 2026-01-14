package com.han.blog.domain.vo;

import com.han.blog.domain.BlogLike;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 文章点赞视图对象 blog_like
 */
@Data
@AutoMapper(target = BlogLike.class)
public class BlogLikeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 点赞ID
     */
    private Long likeId;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 用户ID（登录用户）
     */
    private Long userId;

    /**
     * IP地址（游客，支持IPv6）
     */
    private String ipAddress;

    /**
     * 点赞时间
     */
    private Date createTime;
}


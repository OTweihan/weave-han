package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 文章点赞表
 */
@Data
@NoArgsConstructor
@TableName("blog_like")
public class BlogLike {

    /**
     * 点赞ID
     */
    @TableId
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


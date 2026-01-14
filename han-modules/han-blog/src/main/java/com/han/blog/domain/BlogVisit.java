package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客访问记录表
 */
@Data
@NoArgsConstructor
@TableName("blog_visit")
public class BlogVisit {

    /**
     * 访问记录ID
     */
    @TableId
    private Long visitId;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * IP地址（支持IPv6）
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 来源页
     */
    private String referer;

    /**
     * 访问时间
     */
    private Date visitTime;
}


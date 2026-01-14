package com.han.blog.domain.vo;

import com.han.blog.domain.BlogVisit;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客访问记录视图对象 blog_visit
 */
@Data
@AutoMapper(target = BlogVisit.class)
public class BlogVisitVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问记录ID
     */
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


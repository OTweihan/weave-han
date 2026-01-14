package com.han.blog.domain.vo;

import com.han.blog.domain.BlogLink;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 友情链接视图对象 blog_link
 */
@Data
@AutoMapper(target = BlogLink.class)
public class BlogLinkVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 链接ID
     */
    private Long linkId;

    /**
     * 链接名称
     */
    private String name;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 链接描述
     */
    private String description;

    /**
     * 链接Logo图片ID
     */
    private Long logo;

    /**
     * 打开方式
     */
    private String target;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}


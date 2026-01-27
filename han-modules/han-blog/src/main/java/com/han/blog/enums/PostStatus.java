package com.han.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客文章状态枚举
 */
@Getter
@AllArgsConstructor
public enum PostStatus {

    /**
     * 草稿
     */
    DRAFT(0, "草稿"),

    /**
     * 发布
     */
    PUBLISHED(1, "发布"),

    /**
     * 下架
     */
    OFFLINE(2, "下架"),

    /**
     * 回收站
     */
    RECYCLE(3, "回收站");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态信息
     */
    private final String info;
}

package com.han.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 博客文章状态枚举
 *
 * @author WeiHan
 * @date 2026-01-19
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

    private final Integer code;
    private final String info;
}

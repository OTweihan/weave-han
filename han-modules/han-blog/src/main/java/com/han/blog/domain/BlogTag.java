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
 * @Description: 博客标签表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("blog_tag")
public class BlogTag extends BaseEntity {

    /**
     * 标签ID
     */
    @TableId
    private Long tagId;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签别名
     */
    private String slug;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 文章数量
     */
    private Integer postCount;
}


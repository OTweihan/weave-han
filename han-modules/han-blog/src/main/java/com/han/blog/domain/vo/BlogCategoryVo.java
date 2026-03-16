package com.han.blog.domain.vo;

import com.han.blog.domain.BlogCategory;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客分类视图对象 blog_category
 */
@Data
@AutoMapper(target = BlogCategory.class)
public class BlogCategoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类别名
     */
    private String slug;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类封面图片ID
     */
    private Long coverImage;

    /**
     * 文章数量
     */
    private Integer postCount;

    /**
     * 删除标志（0存在 1删除）
     */
    private Boolean delFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}


package com.han.blog.domain.vo;

import com.han.blog.domain.BlogImage;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客图片库视图对象 blog_image
 */
@Data
@AutoMapper(target = BlogImage.class)
public class BlogImageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    private Long imageId;

    /**
     * OSS文件ID
     */
    private Long ossId;

    /**
     * 图片名称
     */
    private String imageName;

    /**
     * 图片类型（关联字典表blog_image_type）
     */
    private String imageType;

    /**
     * 关联文章ID
     */
    private Long postId;

    /**
     * 关联分类ID
     */
    private Long categoryId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 图片描述（alt属性）
     */
    private String alt;

    /**
     * 图片标题/说明
     */
    private String caption;

    /**
     * 图片宽度
     */
    private Integer width;

    /**
     * 图片高度
     */
    private Integer height;

    /**
     * 文件大小（字节）
     */
    private Integer fileSize;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否公开（0私有 1公开）
     */
    private String isPublic;

    /**
     * 访问次数
     */
    private Integer visitCount;

    /**
     * 删除标志（0存在 1删除）
     */
    private String delFlag;
}


package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.han.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客图片库表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("blog_image")
public class BlogImage extends BaseEntity {

    /**
     * 图片ID
     */
    @TableId
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
    @TableLogic
    private String delFlag;
}


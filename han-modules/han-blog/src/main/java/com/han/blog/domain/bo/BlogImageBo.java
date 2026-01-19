package com.han.blog.domain.bo;

import com.han.blog.domain.BlogImage;
import com.han.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客图片库业务对象 blog_image
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlogImage.class, reverseConvertGenerate = false)
public class BlogImageBo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    private Long imageId;

    /**
     * OSS文件ID
     */
    @NotBlank(message = "OSS文件ID不能为空")
    private Long ossId;

    /**
     * 图片名称
     */
    @NotBlank(message = "图片名称不能为空")
    @Size(max = 100, message = "图片名称不能超过100个字符")
    private String imageName;

    /**
     * 图片类型（关联字典表blog_image_type）
     */
    @NotBlank(message = "图片类型不能为空")
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
    @Size(max = 200, message = "原始文件名不能超过200个字符")
    private String originalName;

    /**
     * 图片描述（alt属性）
     */
    @Size(max = 200, message = "图片描述不能超过200个字符")
    private String alt;

    /**
     * 图片标题/说明
     */
    @Size(max = 500, message = "图片标题不能超过500个字符")
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
    private Integer delFlag;
}

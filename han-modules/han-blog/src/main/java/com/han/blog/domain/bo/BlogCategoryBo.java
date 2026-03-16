package com.han.blog.domain.bo;

import com.han.blog.domain.BlogCategory;
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
 * @Description: 博客分类业务对象 blog_category
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlogCategory.class, reverseConvertGenerate = false)
public class BlogCategoryBo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String name;

    /**
     * 分类别名
     */
    @NotBlank(message = "分类别名不能为空")
    @Size(max = 50, message = "分类别名不能超过50个字符")
    private String slug;

    /**
     * 分类描述
     */
    @Size(max = 500, message = "分类描述不能超过500个字符")
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
}

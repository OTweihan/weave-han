package com.han.blog.domain.bo;

import com.han.blog.domain.BlogTag;
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
 * @Description: 博客标签业务对象 blog_tag
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlogTag.class, reverseConvertGenerate = false)
public class BlogTagBo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 30, message = "标签名称不能超过30个字符")
    private String name;

    /**
     * 标签别名
     */
    @NotBlank(message = "标签别名不能为空")
    @Size(max = 30, message = "标签别名不能超过30个字符")
    private String slug;

    /**
     * 标签描述
     */
    @Size(max = 200, message = "标签描述不能超过200个字符")
    private String description;

    /**
     * 标签颜色
     */
    @Size(max = 7, message = "标签颜色不能超过7个字符")
    private String color;

    /**
     * 文章数量
     */
    private Integer postCount;
}

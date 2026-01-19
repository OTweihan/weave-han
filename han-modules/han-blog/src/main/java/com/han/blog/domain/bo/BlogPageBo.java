package com.han.blog.domain.bo;

import com.han.blog.domain.BlogPage;
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
 * @Description: 页面管理业务对象 blog_page
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlogPage.class, reverseConvertGenerate = false)
public class BlogPageBo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 页面标题
     */
    @NotBlank(message = "页面标题不能为空")
    @Size(max = 100, message = "页面标题不能超过100个字符")
    private String title;

    /**
     * 页面别名
     */
    @NotBlank(message = "页面别名不能为空")
    @Size(max = 100, message = "页面别名不能超过100个字符")
    private String slug;

    /**
     * 页面内容
     */
    private String content;

    /**
     * 渲染后的HTML内容
     */
    private String contentHtml;

    /**
     * 页面模板
     */
    private String template;

    /**
     * 状态（0草稿 1发布 2下架）
     */
    @NotBlank(message = "状态不能为空")
    private String status;

    /**
     * 允许评论（0否 1是）
     */
    private String commentEnabled;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 删除标志（0存在 1删除）
     */
    private Integer delFlag;
}

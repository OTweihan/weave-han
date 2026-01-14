package com.han.blog.domain.bo;

import com.han.blog.domain.BlogDraft;
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
 * @Description: 草稿箱业务对象 blog_draft
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlogDraft.class, reverseConvertGenerate = false)
public class BlogDraftBo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 草稿ID
     */
    private Long draftId;

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private Long userId;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 300, message = "标题不能超过300个字符")
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 分类ID
     */
    @NotBlank(message = "分类ID不能为空")
    private Long categoryId;
}

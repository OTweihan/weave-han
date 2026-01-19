package com.han.blog.domain.vo;

import com.han.blog.domain.BlogDraft;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 草稿箱视图对象 blog_draft
 */
@Data
@AutoMapper(target = BlogDraft.class)
public class BlogDraftVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 草稿ID
     */
    private Long draftId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签列表
     */
    private List<BlogTagVo> tags;
}


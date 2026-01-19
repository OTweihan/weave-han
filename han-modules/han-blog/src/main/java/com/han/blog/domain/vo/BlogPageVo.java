package com.han.blog.domain.vo;

import com.han.blog.domain.BlogPage;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 页面管理视图对象 blog_page
 */
@Data
@AutoMapper(target = BlogPage.class)
public class BlogPageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 页面标题
     */
    private String title;

    /**
     * 页面别名
     */
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


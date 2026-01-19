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
 * @Description: 页面管理表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("blog_page")
public class BlogPage extends BaseEntity {

    /**
     * 页面ID
     */
    @TableId
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
    @TableLogic
    private String delFlag;
}


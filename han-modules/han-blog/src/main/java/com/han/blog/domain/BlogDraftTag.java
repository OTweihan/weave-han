package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 草稿标签关联表
 */
@Data
@NoArgsConstructor
@TableName("blog_draft_tag")
public class BlogDraftTag {

    /**
     * 草稿ID
     */
    @TableId
    private Long draftId;

    /**
     * 标签ID
     */
    @TableId
    private Long tagId;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 删除标志（0存在 1删除）
     */
    private Integer delFlag;
}
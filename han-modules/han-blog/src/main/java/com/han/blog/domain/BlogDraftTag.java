package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 草稿标签关联表（联合主键：draft_id + tag_id）
 */
@Data
@NoArgsConstructor
@TableName("blog_draft_tag")
public class BlogDraftTag {

    /**
     * 草稿ID（联合主键之一）
     */
    private Long draftId;

    /**
     * 标签ID（联合主键之一）
     */
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
    @TableLogic
    private Integer delFlag;
}

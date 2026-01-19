package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 文章标签关联表（联合主键：post_id + tag_id）
 */
@Data
@NoArgsConstructor
@TableName("blog_post_tag")
public class BlogPostTag {

    /**
     * 文章ID（联合主键之一）
     */
    private Long postId;

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
}


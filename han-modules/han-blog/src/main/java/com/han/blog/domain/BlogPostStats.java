package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客文章统计表
 */
@Data
@NoArgsConstructor
@TableName("blog_post_stats")
public class BlogPostStats {

    /**
     * 文章ID
     */
    @TableId
    private Long postId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 更新时间
     */
    private Date updateTime;
}

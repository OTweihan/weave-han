package com.han.blog.domain.bo;

import com.han.blog.domain.BlogPostStats;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客文章统计业务对象 blog_post_stats
 */
@Data
@NoArgsConstructor
@AutoMapper(target = BlogPostStats.class, reverseConvertGenerate = false)
public class BlogPostStatsBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @NotBlank(message = "文章ID不能为空")
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
    @NotBlank(message = "更新时间不能为空")
    private Date updateTime;
}

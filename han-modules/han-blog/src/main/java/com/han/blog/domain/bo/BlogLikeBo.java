package com.han.blog.domain.bo;

import com.han.blog.domain.BlogLike;
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
 * @Description: 文章点赞业务对象 blog_like
 */
@Data
@NoArgsConstructor
@AutoMapper(target = BlogLike.class, reverseConvertGenerate = false)
public class BlogLikeBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 点赞ID
     */
    private Long likeId;

    /**
     * 文章ID
     */
    @NotBlank(message = "文章ID不能为空")
    private Long postId;

    /**
     * 用户ID（登录用户）
     */
    private Long userId;

    /**
     * IP地址（游客，支持IPv6）
     */
    private String ipAddress;

    /**
     * 点赞时间
     */
    @NotBlank(message = "点赞时间不能为空")
    private Date createTime;
}

package com.han.blog.domain.bo;

import com.han.blog.domain.BlogVisit;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客访问记录业务对象 blog_visit
 */
@Data
@NoArgsConstructor
@AutoMapper(target = BlogVisit.class, reverseConvertGenerate = false)
public class BlogVisitBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问记录ID
     */
    private Long visitId;

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * IP地址（支持IPv6）
     */
    @Size(max = 45, message = "IP地址不能超过45个字符")
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 来源页
     */
    @Size(max = 200, message = "来源页不能超过200个字符")
    private String referer;

    /**
     * 访问时间
     */
    @NotBlank(message = "访问时间不能为空")
    private Date visitTime;
}

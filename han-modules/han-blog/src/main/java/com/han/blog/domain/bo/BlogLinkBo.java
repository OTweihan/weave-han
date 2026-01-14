package com.han.blog.domain.bo;

import com.han.blog.domain.BlogLink;
import com.han.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 友情链接业务对象 blog_link
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlogLink.class, reverseConvertGenerate = false)
public class BlogLinkBo extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 链接ID
     */
    private Long linkId;

    /**
     * 链接名称
     */
    @NotBlank(message = "链接名称不能为空")
    @Size(max = 50, message = "链接名称不能超过50个字符")
    private String name;

    /**
     * 链接地址
     */
    @NotBlank(message = "链接地址不能为空")
    @Size(max = 200, message = "链接地址不能超过200个字符")
    private String url;

    /**
     * 链接描述
     */
    @Size(max = 200, message = "链接描述不能超过200个字符")
    private String description;

    /**
     * 链接Logo图片ID
     */
    private Long logo;

    /**
     * 打开方式
     */
    private String target;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}

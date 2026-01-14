package com.han.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.han.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 友情链接表
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("blog_link")
public class BlogLink extends BaseEntity {

    /**
     * 链接ID
     */
    @TableId
    private Long linkId;

    /**
     * 链接名称
     */
    private String name;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 链接描述
     */
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


package com.han.common.core.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: OSS DTO
 */
@Data
@NoArgsConstructor
public class OssDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对象存储主键
     */
    private Long ossId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;
}

package com.han.common.storage.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 上传返回体
 */
@Data
@Builder
public class UploadResult {

    /**
     * 文件路径
     */
    private String url;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 已上传对象的实体标记（用来校验文件）
     */
    private String eTag;
}

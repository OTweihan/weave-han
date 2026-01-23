package com.han.system.domain.vo;

import lombok.Data;

/**
 * @Author Michelle.Chung
 * @CreateTime: 2026-01-23
 * @Description: 上传对象信息
 */
@Data
public class SysOssUploadVo {

    /**
     * URL地址
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 对象存储主键
     */
    private String ossId;
}

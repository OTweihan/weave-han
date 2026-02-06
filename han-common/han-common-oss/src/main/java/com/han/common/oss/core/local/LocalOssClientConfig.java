package com.han.common.oss.core.local;

import com.han.common.oss.core.OssClientConfig;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-02-04
 * @Description: Local 存储的文件客户端配置类
 */
public class LocalOssClientConfig implements OssClientConfig {

    /**
     * 基础路径
     */
    @NotEmpty(message = "基础路径不能为空")
    private String basePath;

    /**
     * 自定义域名
     */
    @NotEmpty(message = "domain 不能为空")
    @URL(message = "domain 必须是 URL 格式")
    private String domain;
}

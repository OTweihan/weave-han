package com.han.common.storage.core.db;

import com.han.common.storage.core.StorageClientConfig;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-02-04
 * @Description: DB 存储的文件客户端配置类
 */
@Data
public class DbStorageClientConfig implements StorageClientConfig {

    /**
     * 自定义域名
     */
    @NotEmpty(message = "domain 不能为空")
    @URL(message = "domain 必须是 URL 格式")
    private String domain;
}

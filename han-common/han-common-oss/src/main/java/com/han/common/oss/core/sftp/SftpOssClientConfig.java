package com.han.common.oss.core.sftp;

import com.han.common.oss.core.OssClientConfig;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-02-04
 * @Description: 基于 SFTP 存储的对象存储客户端的配置类
 */
@Data
public class SftpOssClientConfig implements OssClientConfig {

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

    /**
     * 主机地址
     */
    @NotEmpty(message = "host 不能为空")
    private String host;

    /**
     * 主机端口
     */
    @NotNull(message = "port 不能为空")
    private Integer port;

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    private String password;
}

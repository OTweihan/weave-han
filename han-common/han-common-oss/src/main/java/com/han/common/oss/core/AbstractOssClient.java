package com.han.common.oss.core;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: OSS 抽象客户端
 */
@Slf4j
public abstract class AbstractOssClient<ConfigData extends OssClientConfig> implements OssClient {

    /**
     * 配置编号
     */
    private final Long ossConfigId;

    /**
     * 配置信息
     */
    protected ConfigData configData;

    /**
     * 原始配置信息
     */
    private ConfigData originalConfigData;

    public AbstractOssClient(Long ossConfigId, ConfigData configData) {
        this.ossConfigId = ossConfigId;
        this.configData = configData;
        this.originalConfigData = configData;
    }

    /**
     * 初始化
     */
    public final void init() {
        doInit();
        log.debug("配置 {} 初始化完成", configData);
    }

    protected abstract void doInit();

    public final void refresh(ConfigData configData) {
        // 判断是否更新
        if (configData.equals(this.originalConfigData)) {
            return;
        }
        log.info("配置 {} 发生变化，重新初始化", configData);
        this.configData = configData;
        this.originalConfigData = configData;
        // 初始化
        this.init();
    }

    @Override
    public Long getOssConfigId() {
        return ossConfigId;
    }

    /**
     * 格式化文件的 URL 访问地址
     * 使用场景：local、ftp、db，通过 FileController 的 getFile 来获取文件内容
     *
     * @param domain 自定义域名
     * @param path   文件路径
     * @return URL 访问地址
     */
    protected String formatFileUrl(String domain, String path) {
        return StrUtil.format("{}/admin-api/infra/file/{}/get/{}", domain, getOssConfigId(), path);
    }
}

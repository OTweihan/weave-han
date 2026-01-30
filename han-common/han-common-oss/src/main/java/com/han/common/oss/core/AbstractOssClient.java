package com.han.common.oss.core;

import cn.hutool.core.util.IdUtil;
import com.han.common.core.utils.DateUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.oss.properties.OssProperties;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: OSS 抽象客户端
 */
public abstract class AbstractOssClient implements OssClient {

    protected String configKey;
    protected OssProperties properties;

    @Override
    public String getConfigKey() {
        return configKey;
    }

    @Override
    public boolean checkPropertiesSame(OssProperties properties) {
        return this.properties.equals(properties);
    }

    /**
     * 生成一个符合特定规则的、唯一的文件路径。
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 文件路径
     */
    public String getPath(String prefix, String suffix) {
        // 生成uuid
        String uuid = IdUtil.fastSimpleUUID();
        // 生成日期路径
        String datePath = DateUtils.datePath();
        // 拼接路径
        String path = StringUtils.isNotEmpty(prefix) ?
            prefix + StringUtils.SLASH + datePath + StringUtils.SLASH + uuid : datePath + StringUtils.SLASH + uuid;
        return path + suffix;
    }
}

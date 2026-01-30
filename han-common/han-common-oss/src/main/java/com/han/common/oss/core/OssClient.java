package com.han.common.oss.core;

import com.han.common.oss.entity.UploadResult;
import com.han.common.oss.enums.AccessPolicyType;
import com.han.common.oss.properties.OssProperties;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * OSS对象存储客户端接口
 *
 * @Author: AprilWind
 */
/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: OSS对象存储客户端接口
 */
public interface OssClient {

    /**
     * 上传 byte[] 数据
     */
    UploadResult uploadSuffix(byte[] data, String suffix, String contentType);

    /**
     * 上传 InputStream
     */
    UploadResult uploadSuffix(InputStream inputStream, String suffix, Long length, String contentType);

    /**
     * 上传 File
     */
    UploadResult uploadSuffix(File file, String suffix);

    /**
     * 下载文件到输出流
     */
    void download(String key, OutputStream out, Consumer<Long> consumer);

    /**
     * 删除文件
     */
    void delete(String path);

    /**
     * 获取私有URL链接
     */
    String getPrivateUrl(String objectKey, Duration expiredTime);

    /**
     * 获取配置Key
     */
    String getConfigKey();

    /**
     * 获取桶权限类型
     */
    AccessPolicyType getAccessPolicy();

    /**
     * 检查配置是否相同
     */
    boolean checkPropertiesSame(OssProperties properties);

    /**
     * 关闭客户端
     */
    void close();
}

package com.han.common.storage.core.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.han.common.storage.core.AbstractStorageClient;

import java.io.*;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: 本地存储客户端
 */
public class LocalStorageClient extends AbstractStorageClient<LocalStorageClientConfig> {

    public LocalStorageClient(Long id, LocalStorageClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        FileUtil.writeBytes(content, filePath);
        // 拼接返回路径
        return super.formatFileUrl(configData.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        FileUtil.del(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        try {
            return FileUtil.readBytes(filePath);
        } catch (IORuntimeException ex) {
            if (ex.getMessage().startsWith("File not exist:")) {
                return null;
            }
            throw ex;
        }
    }

    private String getFilePath(String path) {
        return configData.getBasePath() + File.separator + path;
    }
}

package com.han.common.storage.core.db;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.han.common.storage.core.AbstractStorageClient;
import com.han.common.storage.domain.StorageFile;
import com.han.common.storage.domain.StorageFileContent;
import com.han.common.storage.mapper.StorageFileMapper;
import com.han.common.storage.mapper.StorageFileContentMapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: DB 存储客户端
 */
public class DbStorageClient extends AbstractStorageClient<DbStorageClientConfig> {

    private StorageFileMapper fileMapper;
    private StorageFileContentMapper ossContentMapper;

    public DbStorageClient(Long id, DbStorageClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        fileMapper = SpringUtil.getBean(StorageFileMapper.class);
        ossContentMapper = SpringUtil.getBean(StorageFileContentMapper.class);
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        return upload(content, path, type, null);
    }

    @Override
    public String upload(byte[] content, String path, String type, Long fileId) {
        Assert.notNull(fileId, "数据库存储上传时 fileId 不能为空");
        StorageFileContent contentDO = new StorageFileContent().setFileId(fileId).setContent(content);
        ossContentMapper.insert(contentDO);
        // 拼接返回路径
        return super.formatFileUrl(configData.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        StorageFile file = fileMapper.selectOneByConfigIdAndPath(getOssConfigId(), path);
        if (file == null) {
            return;
        }
        ossContentMapper.deleteByFileId(file.getId());
    }

    @Override
    public byte[] getContent(String path) {
        StorageFile file = fileMapper.selectOneByConfigIdAndPath(getOssConfigId(), path);
        if (file == null) {
            return null;
        }
        StorageFileContent storageFileContent = ossContentMapper.selectOneByFileId(file.getId());
        return storageFileContent != null ? storageFileContent.getContent() : null;
    }
}

package com.han.common.oss.core.db;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.han.common.oss.core.AbstractOssClient;
import com.han.common.oss.domain.SysFile;
import com.han.common.oss.domain.SysOssContent;
import com.han.common.oss.mapper.SysFileMapper;
import com.han.common.oss.mapper.SysOssContentMapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: DB 存储客户端
 */
public class DbOssClient extends AbstractOssClient<DbOssClientConfig> {

    private SysFileMapper fileMapper;
    private SysOssContentMapper ossContentMapper;

    public DbOssClient(Long id, DbOssClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        fileMapper = SpringUtil.getBean(SysFileMapper.class);
        ossContentMapper = SpringUtil.getBean(SysOssContentMapper.class);
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        return upload(content, path, type, null);
    }

    @Override
    public String upload(byte[] content, String path, String type, Long fileId) {
        Assert.notNull(fileId, "数据库存储上传时 fileId 不能为空");
        SysOssContent contentDO = new SysOssContent().setFileId(fileId).setContent(content);
        ossContentMapper.insert(contentDO);
        // 拼接返回路径
        return super.formatFileUrl(configData.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        SysFile file = fileMapper.selectOneByConfigIdAndPath(getOssConfigId(), path);
        if (file == null) {
            return;
        }
        ossContentMapper.deleteByFileId(file.getId());
    }

    @Override
    public byte[] getContent(String path) {
        SysFile file = fileMapper.selectOneByConfigIdAndPath(getOssConfigId(), path);
        if (file == null) {
            return null;
        }
        SysOssContent sysOssContent = ossContentMapper.selectOneByFileId(file.getId());
        return sysOssContent != null ? sysOssContent.getContent() : null;
    }
}

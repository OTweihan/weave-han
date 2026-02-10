package com.han.common.oss.core.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.han.common.oss.core.AbstractOssClient;
import com.han.common.oss.domain.SysOssContent;
import com.han.common.oss.mapper.SysOssContentMapper;

import java.util.Comparator;
import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: DB 存储客户端
 */
public class DbOssClient extends AbstractOssClient<DbOssClientConfig> {

    private SysOssContentMapper ossContentMapper;

    public DbOssClient(Long id, DbOssClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        ossContentMapper = SpringUtil.getBean(SysOssContentMapper.class);
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        SysOssContent contentDO = new SysOssContent().setOssConfigId(getOssConfigId())
            .setPath(path).setContent(content);
        ossContentMapper.insert(contentDO);
        // 拼接返回路径
        return super.formatFileUrl(configData.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        ossContentMapper.deleteByConfigIdAndPath(getOssConfigId(), path);
    }

    @Override
    public byte[] getContent(String path) {
        List<SysOssContent> list = ossContentMapper.selectListByConfigIdAndPath(getOssConfigId(), path);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        // 排序后，拿 id 最大的，即最后上传的
        list.sort(Comparator.comparing(SysOssContent::getContentId));
        return CollUtil.getLast(list).getContent();
    }
}

package com.han.common.oss.core.ftp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpException;
import cn.hutool.extra.ftp.FtpMode;
import com.han.common.oss.core.AbstractOssClient;

import java.io.*;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-22
 * @Description: FTP 存储客户端
 */
public class FtpOssClient extends AbstractOssClient<FtpOssClientConfig> {

    /**
     * 连接超时时间，单位：毫秒
     */
    private static final Long CONNECTION_TIMEOUT = 3000L;

    /**
     * 读写超时时间，单位：毫秒
     */
    private static final Long SO_TIMEOUT = 10000L;

    private Ftp ftp;

    public FtpOssClient(Long id, FtpOssClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        FtpConfig ftpConfig = new FtpConfig(configData.getHost(), configData.getPort(), configData.getUsername(), configData.getPassword(),
            CharsetUtil.CHARSET_UTF_8, null, null);
        ftpConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
        ftpConfig.setSoTimeout(SO_TIMEOUT);
        this.ftp = new Ftp(ftpConfig, FtpMode.valueOf(configData.getMode()));
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        String fileName = FileUtil.getName(filePath);
        String dir = StrUtil.removeSuffix(filePath, fileName);
        reconnectIfTimeout();
        // 不需要主动创建目录，ftp 内部已经处理（见源码）
        boolean success = ftp.upload(dir, fileName, new ByteArrayInputStream(content));
        if (!success) {
            throw new FtpException(StrUtil.format("上传文件到目标目录 ({}) 失败", filePath));
        }
        // 拼接返回路径
        return super.formatFileUrl(configData.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        reconnectIfTimeout();
        ftp.delFile(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        String fileName = FileUtil.getName(filePath);
        String dir = StrUtil.removeSuffix(filePath, fileName);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        reconnectIfTimeout();
        ftp.download(dir, fileName, out);
        return out.toByteArray();
    }

    private String getFilePath(String path) {
        return configData.getBasePath() + StrUtil.SLASH + path;
    }

    private synchronized void reconnectIfTimeout() {
        ftp.reconnectIfTimeout();
    }
}

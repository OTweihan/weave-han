package com.han.common.core.service;

import com.han.common.core.domain.dto.FileDTO;

import java.util.List;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-21
 * @Description: 通用文件服务
 */
public interface FileService {

    /**
     * 通过文件ID查询对应的url
     *
     * @param fileIds 文件ID串逗号分隔
     * @return url串逗号分隔
     */
    String selectUrlByIds(String fileIds);

    /**
     * 通过文件ID查询列表
     *
     * @param fileIds 文件ID串逗号分隔
     * @return 列表
     */
    List<FileDTO> selectByIds(String fileIds);
}

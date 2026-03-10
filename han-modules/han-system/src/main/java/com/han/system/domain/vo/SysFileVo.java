package com.han.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.han.system.domain.SysFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 文件存储视图对象 sys_file
 */
@Data
@AutoMapper(target = SysFile.class)
public class SysFileVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 存储配置ID
     */
    private Long configId;

    /**
     * 存储文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 访问URL
     */
    private String url;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 创建时间
     */
    private Date createTime;
}

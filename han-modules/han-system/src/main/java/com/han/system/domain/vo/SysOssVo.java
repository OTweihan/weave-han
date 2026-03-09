package com.han.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import com.han.system.domain.SysOss;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: OSS对象存储视图对象 sys_oss
 */
@Data
@AutoMapper(target = SysOss.class)
public class SysOssVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对象存储主键
     */
    private Long ossId;

    /**
     * 配置编号
     */
    private Long ossConfigId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * URL地址
     */
    private String url;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 创建时间
     */
    private Date createTime;
}

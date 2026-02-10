package com.han.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.han.common.mybatis.core.domain.BaseEntity;
import lombok.experimental.Accessors;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: OSS对象存储对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oss")
public class SysOss extends BaseEntity {

    /**
     * 对象存储主键
     */
    @TableId(value = "oss_id")
    private Long ossId;

    /**
     * 配置编号
     * 关联 {@link SysOssConfig#getOssConfigId()}
     */
    private Long configId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * URL地址
     */
    private String url;

    /**
     * 文件的 MIME 类型
     */
    private String type;

    /**
     * 文件大小
     */
    private Long size;
}

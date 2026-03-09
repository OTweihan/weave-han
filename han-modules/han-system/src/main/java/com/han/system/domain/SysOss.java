package com.han.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("sys_file")
public class SysOss extends BaseEntity {

    /**
     * 对象存储主键
     */
    @TableId(value = "id")
    private Long ossId;

    /**
     * 配置编号
     * 关联 {@link SysOssConfig#getOssConfigId()}
     */
    @TableField("config_id")
    private Long configId;

    /**
     * 存储类型
     */
    @TableField("storage_type")
    private String storageType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * URL地址
     */
    private String url;

    /**
     * 文件的 MIME 类型
     */
    @TableField("mime_type")
    private String type;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Long size;
}

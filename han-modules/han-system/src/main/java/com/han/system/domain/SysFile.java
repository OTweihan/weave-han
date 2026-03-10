package com.han.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.han.common.mybatis.core.domain.BaseEntity;
import lombok.experimental.Accessors;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 文件存储对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {

    /**
     * 对象存储主键
     */
    @TableId(value = "id")
    private Long ossId;

    /**
     * 配置编号
     * 关联 {@link SysStorageConfig#getStorageConfigId()}
     */
    @TableField("config_id")
    private Long configId;

    /**
     * 存储类型
     */
    private String storageType;

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
    private String mimeType;

    /**
     * 文件大小
     */
    private Long fileSize;
}

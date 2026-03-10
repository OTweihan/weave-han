package com.han.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.han.common.storage.core.StorageClientConfig;
import com.han.common.storage.enums.StorageTypeEnum;
import com.han.common.storage.handler.StorageClientConfigTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.han.common.mybatis.core.domain.BaseEntity;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 存储配置对象 sys_storage_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_storage_config", autoResultMap = true)
public class SysStorageConfig extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long storageConfigId;

    /**
     * 配置名
     */
    private String configName;

    /**
     * 存储类型
     * @see StorageTypeEnum
     */
    private Integer storageType;

    /**
     * 配置数据
     */
    @TableField(typeHandler = StorageClientConfigTypeHandler.class)
    private StorageClientConfig configData;

    /**
     * 是否主配置
     */
    @TableField("is_master")
    private boolean master;

    /**
     * 备注
     */
    private String remark;
}

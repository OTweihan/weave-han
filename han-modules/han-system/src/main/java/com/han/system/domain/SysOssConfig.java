package com.han.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.han.common.oss.core.OssClientConfig;
import com.han.common.oss.enums.OssStorageTypeEnum;
import com.han.common.oss.handler.OssClientConfigTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.han.common.mybatis.core.domain.BaseEntity;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 对象存储配置对象 sys_oss_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_storage_config", autoResultMap = true)
public class SysOssConfig extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long ossConfigId;

    /**
     * 配置名
     */
    private String configName;

    /**
     * 存储类型
     * @see OssStorageTypeEnum
     */
    private Integer storageType;

    /**
     * 配置数据
     */
    @TableField(typeHandler = OssClientConfigTypeHandler.class)
    private OssClientConfig configData;

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

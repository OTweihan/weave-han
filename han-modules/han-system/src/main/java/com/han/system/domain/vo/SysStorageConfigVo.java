package com.han.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import com.han.common.storage.core.StorageClientConfig;
import com.han.common.storage.enums.StorageTypeEnum;
import com.han.system.domain.SysStorageConfig;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 存储配置视图对象 sys_storage_config
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysStorageConfig.class)
public class SysStorageConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
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
    private StorageClientConfig configData;

    /**
     * 是否主配置
     */
    private Boolean master;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;
}

package com.han.system.domain.bo;

import java.util.Map;

import com.han.common.storage.enums.StorageTypeEnum;
import com.han.system.domain.SysStorageConfig;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.han.common.core.validate.AddGroup;
import com.han.common.core.validate.EditGroup;
import com.han.common.mybatis.core.domain.BaseEntity;

/**
 * @Author WeiHan
 * @CreateTime: 2021-08-13
 * @Description: 通知公告业务对象 sys_notice
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysStorageConfig.class, reverseConvertGenerate = false)
public class SysStorageConfigBo extends BaseEntity {

    /**
     * 主键
     */
    private Long storageConfigId;

    /**
     * 配置key
     */
    @NotBlank(message = "配置名不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(min = 2, max = 30, message = "configName长度必须介于{min}和{max} 之间")
    private String configName;

    /**
     * 存储类型
     * @see StorageTypeEnum
     */
    @NotNull(message = "存储类型不能为空")
    private Integer storageType;

    /**
     * 动态配置数据
     */
    @NotNull(message = "存储配置不能为空")
    @AutoMapping(ignore = true)
    private Map<String, Object> configData;

    /**
     * 是否主配置
     */
    private Boolean master;

    /**
     * 备注
     */
    private String remark;
}

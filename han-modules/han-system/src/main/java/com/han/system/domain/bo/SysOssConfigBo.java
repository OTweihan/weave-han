package com.han.system.domain.bo;

import java.util.Map;

import com.han.common.oss.enums.OssStorageTypeEnum;
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
import com.han.system.domain.SysOssConfig;

/**
 * @Author WeiHan
 * @CreateTime: 2021-08-13
 * @Description: 通知公告业务对象 sys_notice
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysOssConfig.class, reverseConvertGenerate = false)
public class SysOssConfigBo extends BaseEntity {

    /**
     * 主键
     */
    private Long ossConfigId;

    /**
     * 配置key
     */
    @NotBlank(message = "配置key不能为空", groups = {AddGroup.class, EditGroup.class})
    @Size(min = 2, max = 100, message = "configKey长度必须介于{min}和{max} 之间")
    private String configKey;

    /**
     * 存储类型
     * @see OssStorageTypeEnum
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

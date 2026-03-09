package com.han.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import com.han.common.oss.core.OssClientConfig;
import com.han.common.oss.enums.OssStorageTypeEnum;
import com.han.system.domain.SysOssConfig;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author Lion Li, 孤舟烟雨
 * @CreateTime: 2021-08-13
 * @Description: 对象存储配置视图对象 sys_oss_config
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysOssConfig.class)
public class SysOssConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
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
    private OssClientConfig configData;

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

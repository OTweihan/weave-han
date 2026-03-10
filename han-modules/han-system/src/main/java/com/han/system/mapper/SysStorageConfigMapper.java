package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysStorageConfig;
import com.han.system.domain.vo.SysStorageConfigVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Lion Li, 孤舟烟雨
 * @CreateTime: 2021-08-13
 * @Description: 对象存储配置Mapper接口
 */
@Mapper
public interface SysStorageConfigMapper extends BaseMapperPlus<SysStorageConfig, SysStorageConfigVo> {
}

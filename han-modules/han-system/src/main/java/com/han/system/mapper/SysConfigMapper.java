package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysConfig;
import com.han.system.domain.vo.SysConfigVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 参数配置 数据层
 */
@Mapper
public interface SysConfigMapper extends BaseMapperPlus<SysConfig, SysConfigVo> {
}

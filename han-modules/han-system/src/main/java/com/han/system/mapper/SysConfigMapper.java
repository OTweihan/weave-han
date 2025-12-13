package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysConfig;
import com.han.system.domain.vo.SysConfigVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 参数配置 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysConfigMapper extends BaseMapperPlus<SysConfig, SysConfigVo> {

}

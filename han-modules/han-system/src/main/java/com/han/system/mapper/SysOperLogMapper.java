package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysOperLog;
import com.han.system.domain.vo.SysOperLogVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysOperLogMapper extends BaseMapperPlus<SysOperLog, SysOperLogVo> {

}

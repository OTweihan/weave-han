package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysLogininfor;
import com.han.system.domain.vo.SysLogininforVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 系统访问日志情况信息 数据层
 */
@Mapper
public interface SysLogininforMapper extends BaseMapperPlus<SysLogininfor, SysLogininforVo> {
}

package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysLogininfor;
import com.han.system.domain.vo.SysLogininforVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统访问日志情况信息 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysLogininforMapper extends BaseMapperPlus<SysLogininfor, SysLogininforVo> {

}

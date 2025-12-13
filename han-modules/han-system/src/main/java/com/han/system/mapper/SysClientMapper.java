package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysClient;
import com.han.system.domain.vo.SysClientVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 授权管理Mapper接口
 *
 * @author Michelle.Chung
 * @date 2023-05-15
 */
@Mapper
public interface SysClientMapper extends BaseMapperPlus<SysClient, SysClientVo> {

}

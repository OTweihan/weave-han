package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysClient;
import com.han.system.domain.vo.SysClientVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Michelle.Chung
 * @CreateTime: 2023-05-15
 * @Description: 授权管理Mapper接口
 */
@Mapper
public interface SysClientMapper extends BaseMapperPlus<SysClient, SysClientVo> {
}

package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysOssConfig;
import com.han.system.domain.vo.SysOssConfigVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对象存储配置Mapper接口
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Mapper
public interface SysOssConfigMapper extends BaseMapperPlus<SysOssConfig, SysOssConfigVo> {

}

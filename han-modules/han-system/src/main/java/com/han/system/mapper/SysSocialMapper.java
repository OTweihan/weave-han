package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysSocial;
import com.han.system.domain.vo.SysSocialVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社会化关系Mapper接口
 *
 * @author thiszhc
 */
@Mapper
public interface SysSocialMapper extends BaseMapperPlus<SysSocial, SysSocialVo> {

}

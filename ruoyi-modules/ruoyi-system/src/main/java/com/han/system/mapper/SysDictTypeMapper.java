package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysDictType;
import com.han.system.domain.vo.SysDictTypeVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapperPlus<SysDictType, SysDictTypeVo> {

}

package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysDictType;
import com.han.system.domain.vo.SysDictTypeVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 字典表 数据层
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapperPlus<SysDictType, SysDictTypeVo> {
}

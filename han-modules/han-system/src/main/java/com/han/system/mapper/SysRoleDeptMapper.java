package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysRoleDept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色与部门关联表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysRoleDeptMapper extends BaseMapperPlus<SysRoleDept, SysRoleDept> {

}

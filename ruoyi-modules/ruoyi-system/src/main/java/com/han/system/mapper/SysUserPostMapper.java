package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysUserPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户与岗位关联表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysUserPostMapper extends BaseMapperPlus<SysUserPost, SysUserPost> {

}

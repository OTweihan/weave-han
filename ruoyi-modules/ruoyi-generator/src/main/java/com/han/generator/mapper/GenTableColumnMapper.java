package com.han.generator.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.generator.domain.GenTableColumn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 业务字段 数据层
 *
 * @author Lion Li
 */
@Mapper
@InterceptorIgnore(dataPermission = "true")
public interface GenTableColumnMapper extends BaseMapperPlus<GenTableColumn, GenTableColumn> {

}

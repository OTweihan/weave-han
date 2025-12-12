package com.han.workflow.mapper;

import com.han.workflow.domain.FlowSpel;
import com.han.workflow.domain.vo.FlowSpelVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程spel达式定义Mapper接口
 *
 * @author Michelle.Chung
 * @date 2025-07-04
 */
@Mapper
public interface FlwSpelMapper extends BaseMapperPlus<FlowSpel, FlowSpelVo> {

}

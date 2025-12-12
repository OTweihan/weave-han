package com.han.workflow.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.workflow.domain.TestLeave;
import com.han.workflow.domain.vo.TestLeaveVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请假Mapper接口
 *
 * @author may
 * @date 2023-07-21
 */
@Mapper
public interface TestLeaveMapper extends BaseMapperPlus<TestLeave, TestLeaveVo> {

}

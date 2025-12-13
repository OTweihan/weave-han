package com.han.demo.mapper;

import com.han.common.mybatis.annotation.DataColumn;
import com.han.common.mybatis.annotation.DataPermission;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.demo.domain.TestTree;
import com.han.demo.domain.vo.TestTreeVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试树表Mapper接口
 *
 * @author Lion Li
 * @date 2021-07-26
 */
@Mapper
@DataPermission({
    @DataColumn(key = "deptName", value = "dept_id"),
    @DataColumn(key = "userName", value = "user_id")
})
public interface TestTreeMapper extends BaseMapperPlus<TestTree, TestTreeVo> {

}

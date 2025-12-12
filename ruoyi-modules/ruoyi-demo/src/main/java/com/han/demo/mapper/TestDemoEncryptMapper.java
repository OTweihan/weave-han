package com.han.demo.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.demo.domain.TestDemoEncrypt;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试加密功能
 *
 * @author Lion Li
 */
@Mapper
public interface TestDemoEncryptMapper extends BaseMapperPlus<TestDemoEncrypt, TestDemoEncrypt> {

}

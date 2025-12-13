package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysOss;
import com.han.system.domain.vo.SysOssVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件上传 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysOssMapper extends BaseMapperPlus<SysOss, SysOssVo> {
}

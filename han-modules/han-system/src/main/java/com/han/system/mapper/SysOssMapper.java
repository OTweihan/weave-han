package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysOss;
import com.han.system.domain.vo.SysOssVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 文件上传 数据层
 */
@Mapper
public interface SysOssMapper extends BaseMapperPlus<SysOss, SysOssVo> {
}

package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysNotice;
import com.han.system.domain.vo.SysNoticeVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 通知公告表 数据层
 */
@Mapper
public interface SysNoticeMapper extends BaseMapperPlus<SysNotice, SysNoticeVo> {
}

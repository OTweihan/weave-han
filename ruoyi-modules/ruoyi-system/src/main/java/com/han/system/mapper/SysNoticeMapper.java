package com.han.system.mapper;

import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysNotice;
import com.han.system.domain.vo.SysNoticeVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告表 数据层
 *
 * @author Lion Li
 */
@Mapper
public interface SysNoticeMapper extends BaseMapperPlus<SysNotice, SysNoticeVo> {

}

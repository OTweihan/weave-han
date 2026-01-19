package com.han.blog.mapper;

import com.han.blog.domain.BlogVisit;
import com.han.blog.domain.vo.BlogVisitVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客访问记录Mapper接口
 */
@Mapper
public interface BlogVisitMapper extends BaseMapperPlus<BlogVisit, BlogVisitVo> {

}

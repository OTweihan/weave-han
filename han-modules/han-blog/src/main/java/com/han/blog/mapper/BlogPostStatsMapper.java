package com.han.blog.mapper;

import com.han.blog.domain.BlogPostStats;
import com.han.blog.domain.vo.BlogPostStatsVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客文章统计Mapper接口
 */
@Mapper
public interface BlogPostStatsMapper extends BaseMapperPlus<BlogPostStats, BlogPostStatsVo> {

}

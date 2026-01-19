package com.han.blog.mapper;

import com.han.blog.domain.BlogDraftTag;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客草稿标签关联Mapper接口
 */
@Mapper
public interface BlogDraftTagMapper extends BaseMapperPlus<BlogDraftTag, BlogDraftTag> {

}

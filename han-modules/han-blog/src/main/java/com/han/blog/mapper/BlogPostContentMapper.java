package com.han.blog.mapper;

import com.han.blog.domain.BlogPostContent;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-16
 * @Description: 文章内容Mapper接口
 */
@Mapper
public interface BlogPostContentMapper extends BaseMapperPlus<BlogPostContent, BlogPostContent> {

}

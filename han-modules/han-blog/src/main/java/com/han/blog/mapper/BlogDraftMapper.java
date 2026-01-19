package com.han.blog.mapper;

import com.han.blog.domain.BlogDraft;
import com.han.blog.domain.vo.BlogDraftVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客草稿Mapper接口
 */
@Mapper
public interface BlogDraftMapper extends BaseMapperPlus<BlogDraft, BlogDraftVo> {

}

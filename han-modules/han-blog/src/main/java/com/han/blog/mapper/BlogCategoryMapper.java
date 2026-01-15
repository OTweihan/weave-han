package com.han.blog.mapper;

import com.han.blog.domain.BlogCategory;
import com.han.blog.domain.vo.BlogCategoryVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客分类Mapper接口
 */
@Mapper
public interface BlogCategoryMapper extends BaseMapperPlus<BlogCategory, BlogCategoryVo> {

}

package com.han.blog.mapper;

import com.han.blog.domain.BlogPostTag;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-16
 * @Description: 文章标签关联Mapper接口
 */
@Mapper
public interface BlogPostTagMapper extends BaseMapperPlus<BlogPostTag, BlogPostTag> {

    /**
     * 根据文章ID查询标签ID列表
     *
     * @param postId 文章ID
     * @return 标签ID列表
     */
    List<Long> selectTagIdsByPostId(@Param("postId") Long postId);

    /**
     * 根据文章ID删除关联关系
     *
     * @param postId 文章ID
     * @return 结果
     */
    int deleteByPostId(@Param("postId") Long postId);

    /**
     * 批量根据文章ID删除关联关系
     *
     * @param postIds 文章ID列表
     * @return 结果
     */
    int deleteByPostIds(@Param("postIds") List<Long> postIds);
}
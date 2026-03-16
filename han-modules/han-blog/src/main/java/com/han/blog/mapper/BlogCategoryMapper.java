package com.han.blog.mapper;

import com.han.blog.domain.BlogCategory;
import com.han.blog.domain.vo.BlogCategoryVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客分类Mapper接口
 */
@Mapper
public interface BlogCategoryMapper extends BaseMapperPlus<BlogCategory, BlogCategoryVo> {

    /**
     * 统计别名数量（包含逻辑删除数据）
     *
     * @param slug 分类别名
     * @param excludeCategoryId 排除的分类ID
     * @return 数量
     */
    long selectSlugCount(@Param("slug") String slug, @Param("excludeCategoryId") Long excludeCategoryId);
}

package com.han.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 查询博客分类列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 博客分类列表
     */
    default Page<BlogCategoryVo> selectPageList(Page<BlogCategory> page, Wrapper<BlogCategory> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

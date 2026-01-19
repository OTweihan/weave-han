package com.han.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.blog.domain.BlogPost;
import com.han.blog.domain.vo.BlogPostVo;
import org.apache.ibatis.annotations.Mapper;


/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客文章Mapper接口
 */
@Mapper
public interface BlogPostMapper extends BaseMapperPlus<BlogPost, BlogPostVo> {

    /**
     * 查询博客文章列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 博客文章列表
     */
    default Page<BlogPostVo> selectPageList(Page<BlogPost> page, Wrapper<BlogPost> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

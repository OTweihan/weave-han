package com.han.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogPage;
import com.han.blog.domain.vo.BlogPageVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客页面Mapper接口
 */
@Mapper
public interface BlogPageMapper extends BaseMapperPlus<BlogPage, BlogPageVo> {

    /**
     * 查询博客页面列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 博客页面列表
     */
    default Page<BlogPageVo> selectPageList(Page<BlogPage> page, Wrapper<BlogPage> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

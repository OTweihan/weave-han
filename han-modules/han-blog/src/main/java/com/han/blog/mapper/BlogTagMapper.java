package com.han.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogTag;
import com.han.blog.domain.vo.BlogTagVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客标签Mapper接口
 */
@Mapper
public interface BlogTagMapper extends BaseMapperPlus<BlogTag, BlogTagVo> {

    /**
     * 查询博客标签列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 博客标签列表
     */
    default Page<BlogTagVo> selectPageList(Page<BlogTag> page, Wrapper<BlogTag> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

package com.han.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogLike;
import com.han.blog.domain.vo.BlogLikeVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客点赞记录Mapper接口
 */
@Mapper
public interface BlogLikeMapper extends BaseMapperPlus<BlogLike, BlogLikeVo> {

    /**
     * 查询点赞记录列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 点赞记录列表
     */
    default Page<BlogLikeVo> selectPageList(Page<BlogLike> page, Wrapper<BlogLike> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

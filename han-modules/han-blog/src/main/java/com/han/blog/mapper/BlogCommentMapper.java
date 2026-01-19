package com.han.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogComment;
import com.han.blog.domain.vo.BlogCommentVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客评论Mapper接口
 */
@Mapper
public interface BlogCommentMapper extends BaseMapperPlus<BlogComment, BlogCommentVo> {

    /**
     * 查询博客评论列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 博客评论列表
     */
    default Page<BlogCommentVo> selectPageList(Page<BlogComment> page, Wrapper<BlogComment> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

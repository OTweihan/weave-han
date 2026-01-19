package com.han.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogLink;
import com.han.blog.domain.vo.BlogLinkVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客友情链接Mapper接口
 */
@Mapper
public interface BlogLinkMapper extends BaseMapperPlus<BlogLink, BlogLinkVo> {

    /**
     * 查询友情链接列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 友情链接列表
     */
    default Page<BlogLinkVo> selectPageList(Page<BlogLink> page, Wrapper<BlogLink> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

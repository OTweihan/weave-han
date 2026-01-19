package com.han.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogImage;
import com.han.blog.domain.vo.BlogImageVo;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客图片Mapper接口
 */
@Mapper
public interface BlogImageMapper extends BaseMapperPlus<BlogImage, BlogImageVo> {

    /**
     * 查询博客图片库列表
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return 博客图片库列表
     */
    default Page<BlogImageVo> selectPageList(Page<BlogImage> page, Wrapper<BlogImage> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

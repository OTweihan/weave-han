package com.han.blog.service;

import com.han.blog.domain.bo.BlogTagBo;
import com.han.blog.domain.vo.BlogTagVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-14
 * @Description: 博客标签Service接口 blog_tag
 */
public interface IBlogTagService {

    /**
     * 查询博客标签列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 博客标签列表
     */
    TableDataInfo<BlogTagVo> selectPageTagList(BlogTagBo bo, PageQuery pageQuery);

    /**
     * 查询博客标签详细信息
     *
     * @param tagId 标签ID
     * @return 博客标签详细信息
     */
    BlogTagVo queryTagDetail(Long tagId);

    /**
     * 根据标签别名查询标签
     *
     * @param slug 标签别名
     * @return 博客标签
     */
    BlogTagVo queryTagBySlug(String slug);

    /**
     * 新增博客标签
     *
     * @param tag 博客标签业务对象
     * @return 结果
     */
    int insertTag(BlogTagBo tag);

    /**
     * 修改博客标签
     *
     * @param tag 博客标签业务对象
     * @return 结果
     */
    int updateTag(BlogTagBo tag);

    /**
     * 批量删除博客标签
     *
     * @param tagIds 标签ID列表
     * @return 结果
     */
    int deleteTags(Long[] tagIds);

    /**
     * 查询所有博客标签列表（不分页）
     *
     * @return 博客标签列表
     */
    List<BlogTagVo> selectAllTags();
}
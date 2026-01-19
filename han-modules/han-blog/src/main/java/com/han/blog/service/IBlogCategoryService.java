package com.han.blog.service;

import com.han.blog.domain.bo.BlogCategoryBo;
import com.han.blog.domain.vo.BlogCategoryVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-16
 * @Description: 博客分类Service接口 blog_category
 */
public interface IBlogCategoryService {

    /**
     * 查询博客分类列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 博客分类列表
     */
    TableDataInfo<BlogCategoryVo> selectPageCategoryList(BlogCategoryBo bo, PageQuery pageQuery);

    /**
     * 查询博客分类详细信息
     *
     * @param categoryId 分类ID
     * @return 博客分类详细信息
     */
    BlogCategoryVo queryCategoryDetail(Long categoryId);

    /**
     * 根据分类别名查询分类
     *
     * @param slug 分类别名
     * @return 博客分类
     */
    BlogCategoryVo queryCategoryBySlug(String slug);

    /**
     * 新增博客分类
     *
     * @param category 博客分类业务对象
     * @return 结果
     */
    int insertCategory(BlogCategoryBo category);

    /**
     * 修改博客分类
     *
     * @param category 博客分类业务对象
     * @return 结果
     */
    int updateCategory(BlogCategoryBo category);

    /**
     * 批量删除博客分类
     *
     * @param categoryIds 分类ID列表
     * @return 结果
     */
    int deleteCategories(Long[] categoryIds);

    /**
     * 查询所有博客分类列表（不分页）
     *
     * @return 博客分类列表
     */
    List<BlogCategoryVo> selectAllCategories();
}

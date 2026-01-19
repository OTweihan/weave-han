package com.han.blog.service;

import com.han.blog.domain.bo.BlogPageBo;
import com.han.blog.domain.vo.BlogPageVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客页面Service接口 blog_page
 */
public interface IBlogPageService {

    /**
     * 查询博客页面列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 博客页面列表
     */
    TableDataInfo<BlogPageVo> selectPagePageList(BlogPageBo bo, PageQuery pageQuery);

    /**
     * 查询单条博客页面详细信息
     *
     * @param pageId 页面ID
     * @return 博客页面详细信息
     */
    BlogPageVo queryPageDetail(Long pageId);

    /**
     * 根据别名查询页面
     *
     * @param slug 页面别名
     * @return 博客页面详细信息
     */
    BlogPageVo queryPageBySlug(String slug);

    /**
     * 新增博客页面
     *
     * @param page 博客页面业务对象
     * @return 结果
     */
    int insertPage(BlogPageBo page);

    /**
     * 修改博客页面
     *
     * @param page 博客页面业务对象
     * @return 结果
     */
    int updatePage(BlogPageBo page);

    /**
     * 批量删除博客页面
     *
     * @param pageIds 页面ID列表
     * @return 结果
     */
    int deletePages(Long[] pageIds);

    /**
     * 查询所有公开页面列表（用于菜单配置等）
     *
     * @return 博客页面列表
     */
    List<BlogPageVo> selectAllPublicPages();
}

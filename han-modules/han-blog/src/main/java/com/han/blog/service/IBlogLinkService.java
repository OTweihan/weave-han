package com.han.blog.service;

import com.han.blog.domain.bo.BlogLinkBo;
import com.han.blog.domain.vo.BlogLinkVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客友情链接Service接口 blog_link
 */
public interface IBlogLinkService {

    /**
     * 查询友情链接列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 友情链接列表
     */
    TableDataInfo<BlogLinkVo> selectPageLinkList(BlogLinkBo bo, PageQuery pageQuery);

    /**
     * 查询单条友情链接详细信息
     *
     * @param linkId 链接ID
     * @return 友情链接详细信息
     */
    BlogLinkVo queryLinkDetail(Long linkId);

    /**
     * 新增友情链接
     *
     * @param link 友情链接业务对象
     * @return 结果
     */
    int insertLink(BlogLinkBo link);

    /**
     * 修改友情链接
     *
     * @param link 友情链接业务对象
     * @return 结果
     */
    int updateLink(BlogLinkBo link);

    /**
     * 批量删除友情链接
     *
     * @param linkIds 链接ID列表
     * @return 结果
     */
    int deleteLinks(Long[] linkIds);

    /**
     * 查询所有公开友情链接列表（不分页）
     *
     * @return 友情链接列表
     */
    List<BlogLinkVo> selectAllPublicLinks();
}

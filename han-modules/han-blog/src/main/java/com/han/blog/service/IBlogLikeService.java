package com.han.blog.service;

import com.han.blog.domain.bo.BlogLikeBo;
import com.han.blog.domain.vo.BlogLikeVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客点赞记录Service接口 blog_like
 */
public interface IBlogLikeService {

    /**
     * 查询点赞记录列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 点赞记录列表
     */
    TableDataInfo<BlogLikeVo> selectPageLikeList(BlogLikeBo bo, PageQuery pageQuery);

    /**
     * 查询单条点赞记录详细信息
     *
     * @param likeId 点赞ID
     * @return 点赞记录详细信息
     */
    BlogLikeVo queryLikeDetail(Long likeId);

    /**
     * 查询指定文章的点赞记录列表
     *
     * @param postId 文章ID
     * @return 点赞记录列表
     */
    List<BlogLikeVo> selectLikesByPostId(Long postId);

    /**
     * 新增点赞记录
     *
     * @param like 点赞业务对象
     * @return 影响行数
     */
    int insertLike(BlogLikeBo like);

    /**
     * 取消点赞（删除点赞记录）
     *
     * @param likeId 点赞ID
     * @return 影响行数
     */
    int deleteLike(Long likeId);

    /**
     * 批量删除点赞记录
     *
     * @param likeIds 点赞ID列表
     * @return 影响行数
     */
    int deleteLikes(Long[] likeIds);
}


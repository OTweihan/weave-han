package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogPost;
import com.han.blog.domain.bo.BlogPostBo;
import com.han.blog.domain.vo.BlogPostVo;
import com.han.blog.mapper.BlogPostMapper;
import com.han.blog.service.IBlogPostService;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客文章Service业务层处理 blog_post
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogPostServiceImpl implements IBlogPostService {

    private final BlogPostMapper blogPostMapper;

    /**
     * 根据条件分页查询博客文章列表
     *
     * @param post      查询条件
     * @param pageQuery 分页参数
     * @return 博客文章列表
     */
    @Override
    public TableDataInfo<BlogPostVo> selectPagePostList(BlogPostBo post, PageQuery pageQuery) {
        Page<BlogPostVo> resultPage = blogPostMapper.selectPageList(pageQuery.build(), this.buildQueryWrapper(post));
        return TableDataInfo.build(resultPage);
    }

    /**
     * 构建博客文章查询条件包装器
     *
     * @param post 博客文章业务对象，包含查询条件
     * @return 查询条件包装器
     */
    private Wrapper<BlogPost> buildQueryWrapper(BlogPostBo post) {
        Map<String, Object> params = post.getParams();
        LambdaQueryWrapper<BlogPost> wrapper = Wrappers.lambdaQuery();
        // 筛选未删除的文章
        wrapper.eq(BlogPost::getDelFlag, SystemConstants.NORMAL)
            // 按文章ID精确匹配（条件存在时）
            .eq(ObjectUtil.isNotNull(post.getPostId()), BlogPost::getPostId, post.getPostId())
            // 按创建时间范围匹配（beginTime和endTime都存在时）
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogPost::getCreateTime, params.get("beginTime"), params.get("endTime"))
            // 按发布时间升序排列
            .orderByAsc(BlogPost::getPublishedTime);
        return wrapper;
    }

    /**
     * 查询博客文章详细信息
     *
     * @param postId 文章ID
     * @return 博客文章详细信息
     */
    @Override
    public BlogPostVo queryPostDetail(Long postId) {
        return blogPostMapper.selectVoById(postId);
    }

    /**
     * 根据文章别名查询文章
     *
     * @param slug 文章别名
     * @return 博客文章
     */
    @Override
    public BlogPostVo queryPostBySlug(String slug) {
        return blogPostMapper.selectVoOne(new LambdaQueryWrapper<BlogPost>().eq(BlogPost::getSlug, slug));
    }

    /**
     * 新增博客文章
     *
     * @param post 博客文章业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPost(BlogPostBo post) {
        BlogPost blogPost = MapstructUtils.convert(post, BlogPost.class);

        int rows = blogPostMapper.insert(blogPost);

        return rows;
    }

    /**
     * 修改博客文章
     *
     * @param post 博客文章业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePost(BlogPostBo post) {
        BlogPost blogPost = MapstructUtils.convert(post, BlogPost.class);
        // 更新文章
        int flag = blogPostMapper.updateById(blogPost);

        if (flag < 1) {
            throw new ServiceException("修改文章{}失败", post.getTitle());
        }

        return flag;
    }

    /**
     * 批量删除博客文章
     *
     * @param postIds 文章ID列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePosts(Long[] postIds) {
        List<Long> ids = List.of(postIds);
        int flag = blogPostMapper.deleteByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除文章失败!");
        }
        return flag;
    }

    /**
     * 更新文章浏览量
     *
     * @param postId 文章ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long postId) {
        return blogPostMapper.update(
            null,
            Wrappers.<BlogPost>lambdaUpdate()
                .eq(BlogPost::getPostId, postId)
                .setSql("view_count = view_count + 1")
        );
    }

    /**
     * 更新文章点赞数
     *
     * @param postId    文章ID
     * @param increment 增量
     * @return 结果
     */
    @Override
    public int incrementLikeCount(Long postId, Integer increment) {
        return blogPostMapper.update(
            null,
            Wrappers.<BlogPost>lambdaUpdate()
                .eq(BlogPost::getPostId, postId)
                .setSql("view_count = view_count + 1")
        );
    }
}

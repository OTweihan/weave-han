package com.han.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogPost;
import com.han.blog.domain.BlogPostContent;
import com.han.blog.domain.BlogPostTag;
import com.han.blog.domain.bo.BlogPostBo;
import com.han.blog.domain.vo.BlogCategoryVo;
import com.han.blog.domain.vo.BlogPostStatsVo;
import com.han.blog.domain.vo.BlogPostVo;
import com.han.blog.domain.vo.BlogTagVo;
import com.han.blog.mapper.BlogCategoryMapper;
import com.han.blog.mapper.BlogPostContentMapper;
import com.han.blog.mapper.BlogPostMapper;
import com.han.blog.mapper.BlogPostTagMapper;
import com.han.blog.mapper.BlogTagMapper;
import com.han.blog.service.IBlogPostService;
import com.han.blog.service.IBlogPostStatsService;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
    private final BlogPostContentMapper blogPostContentMapper;
    private final BlogPostTagMapper blogPostTagMapper;
    private final BlogCategoryMapper blogCategoryMapper;
    private final BlogTagMapper blogTagMapper;
    private final IBlogPostStatsService blogPostStatsService;

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
        
        // 批量填充统计信息
        List<BlogPostVo> rows = resultPage.getRecords();
        if (CollUtil.isNotEmpty(rows)) {
            List<Long> postIds = rows.stream().map(BlogPostVo::getPostId).toList();
            Map<Long, BlogPostStatsVo> statsMap = blogPostStatsService.queryStatsByPostIds(postIds);
            
            for (BlogPostVo row : rows) {
                BlogPostStatsVo stats = statsMap.get(row.getPostId());
                if (stats != null) {
                    row.setViewCount(Long.valueOf(stats.getViewCount()));
                    row.setLikeCount(Long.valueOf(stats.getLikeCount()));
                    row.setCommentCount(Long.valueOf(stats.getCommentCount()));
                }
            }
        }
        
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
        BlogPostVo postVo = blogPostMapper.selectVoById(postId);
        if (postVo != null) {
            // 查询文章内容
            BlogPostContent content = blogPostContentMapper.selectById(postId);
            if (content != null) {
                postVo.setContent(content.getContent());
                postVo.setContentHtml(content.getContentHtml());
                postVo.setWordCount(content.getWordCount());
                postVo.setReadingTime(content.getReadingTime());
            }

            // 查询分类信息
            if (postVo.getCategoryId() != null) {
                BlogCategoryVo category = blogCategoryMapper.selectVoById(postVo.getCategoryId());
                postVo.setCategory(category);
            }

            // 查询标签列表
            List<Long> tagIds = blogPostTagMapper.selectTagIdsByPostId(postId);
            if (CollUtil.isNotEmpty(tagIds)) {
                List<BlogTagVo> tags = blogTagMapper.selectVoList(new LambdaQueryWrapper<com.han.blog.domain.BlogTag>()
                    .in(com.han.blog.domain.BlogTag::getTagId, tagIds));
                postVo.setTags(tags);
            }
            
            // 填充统计信息
            BlogPostStatsVo stats = blogPostStatsService.queryStatsByPostId(postId);
            if (stats != null) {
                postVo.setViewCount(Long.valueOf(stats.getViewCount()));
                postVo.setLikeCount(Long.valueOf(stats.getLikeCount()));
                postVo.setCommentCount(Long.valueOf(stats.getCommentCount()));
            }
        }
        return postVo;
    }

    /**
     * 根据文章别名查询文章
     *
     * @param slug 文章别名
     * @return 博客文章
     */
    @Override
    public BlogPostVo queryPostBySlug(String slug) {
        BlogPostVo postVo = blogPostMapper.selectVoOne(new LambdaQueryWrapper<BlogPost>().eq(BlogPost::getSlug, slug));
        if (postVo != null) {
            // 查询文章内容
            BlogPostContent content = blogPostContentMapper.selectById(postVo.getPostId());
            if (content != null) {
                postVo.setContent(content.getContent());
                postVo.setContentHtml(content.getContentHtml());
                postVo.setWordCount(content.getWordCount());
                postVo.setReadingTime(content.getReadingTime());
            }

            // 查询分类信息
            if (postVo.getCategoryId() != null) {
                BlogCategoryVo category = blogCategoryMapper.selectVoById(postVo.getCategoryId());
                postVo.setCategory(category);
            }

            // 查询标签列表
            List<Long> tagIds = blogPostTagMapper.selectTagIdsByPostId(postVo.getPostId());
            if (CollUtil.isNotEmpty(tagIds)) {
                List<BlogTagVo> tags = blogTagMapper.selectVoList(new LambdaQueryWrapper<com.han.blog.domain.BlogTag>()
                    .in(com.han.blog.domain.BlogTag::getTagId, tagIds));
                postVo.setTags(tags);
            }
            
            // 填充统计信息
            BlogPostStatsVo stats = blogPostStatsService.queryStatsByPostId(postVo.getPostId());
            if (stats != null) {
                postVo.setViewCount(Long.valueOf(stats.getViewCount()));
                postVo.setLikeCount(Long.valueOf(stats.getLikeCount()));
                postVo.setCommentCount(Long.valueOf(stats.getCommentCount()));
            }
        }
        return postVo;
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
        post.setAuthorId(LoginHelper.getUserId());
        BlogPost blogPost = MapstructUtils.convert(post, BlogPost.class);
        int rows = blogPostMapper.insert(blogPost);

        if (rows > 0) {
            Long postId = blogPost.getPostId();

            // 保存文章内容
            if (post.getContent() != null) {
                BlogPostContent content = new BlogPostContent();
                content.setPostId(postId);
                content.setContent(post.getContent());
                content.setContentHtml(post.getContentHtml());
                content.setWordCount(post.getWordCount());
                content.setReadingTime(post.getReadingTime());
                blogPostContentMapper.insert(content);
            }

            // 保存文章标签关联
            if (CollUtil.isNotEmpty(post.getTagIds())) {
                saveBatchPostTag(postId, post.getTagIds());
            }
        }

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

        Long postId = post.getPostId();

        // 更新或新增文章内容
        if (post.getContent() != null) {
            BlogPostContent existContent = blogPostContentMapper.selectById(postId);
            BlogPostContent content = new BlogPostContent();
            content.setPostId(postId);
            content.setContent(post.getContent());
            content.setContentHtml(post.getContentHtml());
            content.setWordCount(post.getWordCount());
            content.setReadingTime(post.getReadingTime());

            if (existContent != null) {
                // 更新
                blogPostContentMapper.updateById(content);
            } else {
                // 新增
                blogPostContentMapper.insert(content);
            }
        }

        // 更新文章标签关联（先删除再新增）
        blogPostTagMapper.deleteByPostId(postId);
        if (CollUtil.isNotEmpty(post.getTagIds())) {
            saveBatchPostTag(postId, post.getTagIds());
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
        // 删除文章
        int flag = blogPostMapper.deleteByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除文章失败!");
        }
        // 级联删除文章内容
        blogPostContentMapper.deleteByIds(ids);
        // 级联删除文章标签关联
        blogPostTagMapper.deleteByPostIds(ids);
        return flag;
    }

    /**
     * 批量保存文章标签关联
     *
     * @param postId 文章ID
     * @param tagIds 标签ID列表
     */
    private void saveBatchPostTag(Long postId, List<Long> tagIds) {
        List<BlogPostTag> postTags = new ArrayList<>();
        Long userId = LoginHelper.getUserId();
        Date now = new Date();

        for (Long tagId : tagIds) {
            BlogPostTag postTag = new BlogPostTag();
            postTag.setPostId(postId);
            postTag.setTagId(tagId);
            postTag.setCreateBy(userId);
            postTag.setCreateTime(now);
            postTags.add(postTag);
        }

        if (CollUtil.isNotEmpty(postTags)) {
            blogPostTagMapper.insertBatch(postTags);
        }
    }

    /**
     * 更新文章浏览量
     *
     * @param postId 文章ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long postId) {
        return blogPostStatsService.incrementViewCount(postId);
    }
}

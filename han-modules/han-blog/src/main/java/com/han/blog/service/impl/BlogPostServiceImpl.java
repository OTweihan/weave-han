package com.han.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogCategory;
import com.han.blog.domain.BlogPost;
import com.han.blog.domain.BlogPostContent;
import com.han.blog.domain.BlogPostTag;
import com.han.blog.domain.BlogTag;
import com.han.blog.domain.bo.BlogPostBo;
import com.han.blog.domain.vo.BlogCategoryVo;
import com.han.blog.domain.vo.BlogPostStatsVo;
import com.han.blog.domain.vo.BlogPostVo;
import com.han.blog.domain.vo.BlogTagVo;
import com.han.blog.enums.PostStatus;
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
import com.han.common.core.utils.StringUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客文章Service业务层处理 blog_post
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogPostServiceImpl implements IBlogPostService {

    private static final String SOURCE_TYPE_ORIGINAL = "ORIGINAL";
    private static final String SOURCE_TYPE_REPRINT = "REPRINT";
    private static final String SOURCE_TYPE_TRANSLATION = "TRANSLATION";

    private final BlogPostMapper blogPostMapper;
    private final BlogPostContentMapper blogPostContentMapper;
    private final BlogPostTagMapper blogPostTagMapper;
    private final BlogCategoryMapper blogCategoryMapper;
    private final BlogTagMapper blogTagMapper;
    private final IBlogPostStatsService blogPostStatsService;

    /**
     * 查询公开博客文章列表（分页）
     *
     * @param post      查询条件
     * @param pageQuery 分页参数
     * @return 公开博客文章列表
     */
    @Override
    public TableDataInfo<BlogPostVo> selectPublicPagePostList(BlogPostBo post, PageQuery pageQuery) {
        BlogPostBo queryBo = ObjectUtil.defaultIfNull(post, new BlogPostBo());
        Page<BlogPostVo> resultPage = blogPostMapper.selectPagePostList(pageQuery.build(), buildPublicQueryWrapper(queryBo));
        fillListExtraInfo(resultPage.getRecords());
        hidePublicSensitiveFields(resultPage.getRecords());
        return TableDataInfo.build(resultPage);
    }

    /**
     * 根据条件分页查询博客文章列表
     *
     * @param post      查询条件
     * @param pageQuery 分页参数
     * @return 博客文章列表
     */
    @Override
    public TableDataInfo<BlogPostVo> selectPagePostList(BlogPostBo post, PageQuery pageQuery) {
        BlogPostBo queryBo = ObjectUtil.defaultIfNull(post, new BlogPostBo());
        Page<BlogPostVo> resultPage = blogPostMapper.selectPagePostList(pageQuery.build(), buildQueryWrapper(queryBo));
        fillListExtraInfo(resultPage.getRecords());
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
        Long categoryId = normalizeNullableId(post.getCategoryId());
        LambdaQueryWrapper<BlogPost> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BlogPost::getDelFlag, SystemConstants.NORMAL)
            .eq(ObjectUtil.isNotNull(post.getPostId()), BlogPost::getPostId, post.getPostId())
            .like(StringUtils.isNotBlank(post.getTitle()), BlogPost::getTitle, post.getTitle())
            .eq(StringUtils.isNotBlank(post.getSlug()), BlogPost::getSlug, StringUtils.trim(post.getSlug()))
            .eq(ObjectUtil.isNotNull(post.getAuthorId()), BlogPost::getAuthorId, post.getAuthorId())
            .eq(ObjectUtil.isNotNull(categoryId), BlogPost::getCategoryId, categoryId)
            .eq(StringUtils.isNotBlank(post.getStatus()), BlogPost::getStatus, post.getStatus())
            .eq(StringUtils.isNotBlank(post.getSourceType()), BlogPost::getSourceType, StringUtils.upperCase(StringUtils.trim(post.getSourceType())))
            .eq(StringUtils.isNotBlank(post.getIsTop()), BlogPost::getIsTop, post.getIsTop())
            .eq(StringUtils.isNotBlank(post.getIsFeatured()), BlogPost::getIsFeatured, post.getIsFeatured())
            .eq(StringUtils.isNotBlank(post.getAllowComment()), BlogPost::getAllowComment, post.getAllowComment())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogPost::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByDesc(BlogPost::getIsTop)
            .orderByDesc(BlogPost::getPublishedTime)
            .orderByDesc(BlogPost::getUpdateTime);
        return wrapper;
    }

    /**
     * 构建公开文章列表查询条件
     *
     * @param post 文章查询参数
     * @return 查询条件包装器
     */
    private Wrapper<BlogPost> buildPublicQueryWrapper(BlogPostBo post) {
        Long categoryId = normalizeNullableId(post.getCategoryId());
        LambdaQueryWrapper<BlogPost> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BlogPost::getDelFlag, SystemConstants.NORMAL)
            .eq(BlogPost::getStatus, PostStatus.PUBLISHED.getCode())
            .like(StringUtils.isNotBlank(post.getTitle()), BlogPost::getTitle, StringUtils.trim(post.getTitle()))
            .eq(ObjectUtil.isNotNull(post.getAuthorId()), BlogPost::getAuthorId, post.getAuthorId())
            .eq(ObjectUtil.isNotNull(categoryId), BlogPost::getCategoryId, categoryId)
            .eq(StringUtils.isNotBlank(post.getSourceType()), BlogPost::getSourceType, StringUtils.upperCase(StringUtils.trim(post.getSourceType())))
            .eq(StringUtils.isNotBlank(post.getIsTop()), BlogPost::getIsTop, post.getIsTop())
            .eq(StringUtils.isNotBlank(post.getIsFeatured()), BlogPost::getIsFeatured, post.getIsFeatured())
            .isNotNull(BlogPost::getPublishedTime)
            .orderByDesc(BlogPost::getIsTop)
            .orderByDesc(BlogPost::getIsFeatured)
            .orderByDesc(BlogPost::getPublishedTime)
            .orderByDesc(BlogPost::getUpdateTime);
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
        fillPostExtraInfo(postVo);
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
        if (StringUtils.isBlank(slug)) {
            return null;
        }
        BlogPostVo postVo = blogPostMapper.selectVoOne(new LambdaQueryWrapper<BlogPost>()
            .eq(BlogPost::getDelFlag, SystemConstants.NORMAL)
            .eq(BlogPost::getStatus, PostStatus.PUBLISHED.getCode())
            .isNotNull(BlogPost::getPublishedTime)
            .eq(BlogPost::getSlug, StringUtils.trim(slug)));
        fillPostExtraInfo(postVo);
        hidePublicSensitiveField(postVo);
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
        prepareBeforeSave(post, false);
        BlogPost blogPost = MapstructUtils.convert(post, BlogPost.class);
        int rows;
        try {
            rows = blogPostMapper.insert(blogPost);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("新增文章'{}'失败，文章别名'{}'已存在", post.getTitle(), post.getSlug());
        }

        if (rows > 0) {
            Long postId = null;
            if (blogPost != null) {
                postId = blogPost.getPostId();
            } else {
                throw new ServiceException("新增文章成功，但未获取到文章ID");
            }
            saveOrUpdatePostContent(postId, post);
            saveBatchPostTag(postId, post.getTagIds());
            blogPostStatsService.initStats(postId);
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
        if (ObjectUtil.isNull(post.getPostId())) {
            throw new ServiceException("文章ID不能为空");
        }
        prepareBeforeSave(post, true);
        BlogPost blogPost = MapstructUtils.convert(post, BlogPost.class);
        int rows;
        try {
            rows = blogPostMapper.updateById(blogPost);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("修改文章'{}'失败，文章别名'{}'已存在", post.getTitle(), post.getSlug());
        }
        if (rows < 1) {
            throw new ServiceException("修改文章{}失败", post.getTitle());
        }

        Long postId = post.getPostId();
        saveOrUpdatePostContent(postId, post);
        blogPostTagMapper.deleteByPostId(postId);
        saveBatchPostTag(postId, post.getTagIds());
        return rows;
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
        int rows = blogPostMapper.deleteByIds(ids);
        if (rows < 1) {
            throw new ServiceException("删除文章失败!");
        }
        blogPostContentMapper.deleteByIds(ids);
        blogPostTagMapper.deleteByPostIds(ids);
        blogPostStatsService.deleteStats(postIds);
        return rows;
    }

    /**
     * 更新文章浏览量
     *
     * @param postId 文章ID
     * @return 结果
     */
    @Override
    public int incrementViewCount(Long postId) {
        boolean exists = blogPostMapper.exists(new LambdaQueryWrapper<BlogPost>()
            .eq(BlogPost::getPostId, postId)
            .eq(BlogPost::getDelFlag, SystemConstants.NORMAL)
            .eq(BlogPost::getStatus, PostStatus.PUBLISHED.getCode())
            .isNotNull(BlogPost::getPublishedTime));
        if (!exists) {
            return 0;
        }
        int rows = blogPostStatsService.incrementViewCount(postId);
        if (rows > 0) {
            return rows;
        }
        blogPostStatsService.initStats(postId);
        return blogPostStatsService.incrementViewCount(postId);
    }

    /**
     * 填充文章扩展信息
     *
     * @param postVo 文章视图对象
     */
    private void fillPostExtraInfo(BlogPostVo postVo) {
        if (postVo == null) {
            return;
        }
        BlogPostContent content = blogPostContentMapper.selectById(postVo.getPostId());
        if (content != null) {
            postVo.setContent(content.getContent());
            postVo.setContentHtml(content.getContentHtml());
            postVo.setWordCount(content.getWordCount());
            postVo.setReadingTime(content.getReadingTime());
        }

        if (postVo.getCategoryId() != null) {
            BlogCategoryVo category = blogCategoryMapper.selectVoById(postVo.getCategoryId());
            postVo.setCategory(category);
        }

        List<Long> tagIds = blogPostTagMapper.selectTagIdsByPostId(postVo.getPostId());
        if (CollUtil.isNotEmpty(tagIds)) {
            List<BlogTagVo> tags = blogTagMapper.selectVoList(new LambdaQueryWrapper<BlogTag>()
                .in(BlogTag::getTagId, tagIds));
            postVo.setTags(tags);
        }

        BlogPostStatsVo stats = blogPostStatsService.queryStatsByPostId(postVo.getPostId());
        applyStats(postVo, stats);
    }

    /**
     * 批量填充统计信息
     *
     * @param rows 文章列表
     */
    private void fillStats(List<BlogPostVo> rows) {
        if (CollUtil.isEmpty(rows)) {
            return;
        }
        List<Long> postIds = rows.stream().map(BlogPostVo::getPostId).toList();
        Map<Long, BlogPostStatsVo> statsMap = blogPostStatsService.queryStatsByPostIds(postIds);
        for (BlogPostVo row : rows) {
            applyStats(row, statsMap.get(row.getPostId()));
        }
    }

    /**
     * 批量填充列表页所需的扩展信息
     *
     * @param rows 文章列表
     */
    private void fillListExtraInfo(List<BlogPostVo> rows) {
        if (CollUtil.isEmpty(rows)) {
            return;
        }
        fillStats(rows);
        fillCategories(rows);
    }

    /**
     * 批量补充分类型信息
     *
     * @param rows 文章列表
     */
    private void fillCategories(List<BlogPostVo> rows) {
        List<Long> categoryIds = rows.stream()
            .map(BlogPostVo::getCategoryId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        if (CollUtil.isEmpty(categoryIds)) {
            return;
        }
        Map<Long, BlogCategoryVo> categoryMap = blogCategoryMapper.selectVoList(
            new LambdaQueryWrapper<BlogCategory>().in(BlogCategory::getCategoryId, categoryIds)
        ).stream().collect(Collectors.toMap(BlogCategoryVo::getCategoryId, category -> category));
        for (BlogPostVo row : rows) {
            row.setCategory(categoryMap.get(row.getCategoryId()));
        }
    }

    /**
     * 应用统计信息
     *
     * @param postVo 文章视图对象
     * @param stats  统计信息
     */
    private void applyStats(BlogPostVo postVo, BlogPostStatsVo stats) {
        postVo.setViewCount(safeLong(stats == null ? null : stats.getViewCount()));
        postVo.setLikeCount(safeLong(stats == null ? null : stats.getLikeCount()));
        postVo.setCommentCount(safeLong(stats == null ? null : stats.getCommentCount()));
    }

    /**
     * 保存或更新文章内容
     *
     * @param postId 文章ID
     * @param post   文章业务对象
     */
    private void saveOrUpdatePostContent(Long postId, BlogPostBo post) {
        BlogPostContent content = new BlogPostContent();
        content.setPostId(postId);
        content.setContent(StringUtils.defaultString(post.getContent()));
        content.setContentHtml(post.getContentHtml());
        content.setWordCount(post.getWordCount());
        content.setReadingTime(post.getReadingTime());

        BlogPostContent existContent = blogPostContentMapper.selectById(postId);
        if (existContent == null) {
            blogPostContentMapper.insert(content);
        } else {
            blogPostContentMapper.updateById(content);
        }
    }

    /**
     * 批量保存文章标签关联
     *
     * @param postId 文章ID
     * @param tagIds 标签ID列表
     */
    private void saveBatchPostTag(Long postId, List<Long> tagIds) {
        if (CollUtil.isEmpty(tagIds)) {
            return;
        }
        List<BlogPostTag> postTags = new ArrayList<>(tagIds.size());
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

        blogPostTagMapper.insertBatch(postTags);
    }

    /**
     * 保存前统一处理默认值与校验
     *
     * @param post     文章业务对象
     * @param isUpdate 是否为更新操作
     */
    private void prepareBeforeSave(BlogPostBo post, boolean isUpdate) {
        if (post == null) {
            throw new ServiceException("文章参数不能为空");
        }
        post.setSlug(normalizeOptionalText(post.getSlug()));
        post.setSummary(normalizeOptionalText(post.getSummary()));
        post.setPassword(normalizeOptionalText(post.getPassword()));
        post.setSourceType(StringUtils.upperCase(StringUtils.blankToDefault(StringUtils.trim(post.getSourceType()), SOURCE_TYPE_ORIGINAL)));
        post.setSourceUrl(normalizeOptionalText(post.getSourceUrl()));
        post.setSeoKeywords(normalizeOptionalText(post.getSeoKeywords()));
        post.setSeoDescription(normalizeOptionalText(post.getSeoDescription()));
        post.setContent(StringUtils.defaultString(post.getContent()));
        post.setContentHtml(normalizeOptionalText(post.getContentHtml()));
        post.setCoverImage(normalizeNullableId(post.getCoverImage()));
        post.setCategoryId(normalizeNullableId(post.getCategoryId()));
        post.setStatus(StringUtils.blankToDefault(StringUtils.trim(post.getStatus()), String.valueOf(PostStatus.DRAFT.getCode())));
        post.setIsTop(StringUtils.blankToDefault(StringUtils.trim(post.getIsTop()), "0"));
        post.setIsFeatured(StringUtils.blankToDefault(StringUtils.trim(post.getIsFeatured()), "0"));
        post.setAllowComment(StringUtils.blankToDefault(StringUtils.trim(post.getAllowComment()), "1"));
        post.setTagIds(normalizeTagIds(post.getTagIds()));
        populateContentStats(post);
        validateBeforeSave(post, isUpdate);
        if (isPublishedStatus(post.getStatus()) && post.getPublishedTime() == null) {
            post.setPublishedTime(new Date());
        }
    }

    /**
     * 保存前校验
     *
     * @param post     文章业务对象
     * @param isUpdate 是否为更新操作
     */
    private void validateBeforeSave(BlogPostBo post, boolean isUpdate) {
        if (isUpdate) {
            if (ObjectUtil.isNull(post.getPostId())) {
                throw new ServiceException("文章ID不能为空");
            }
            if (ObjectUtil.isNull(blogPostMapper.selectById(post.getPostId()))) {
                throw new ServiceException("文章不存在或已删除");
            }
        }

        if (StringUtils.isNotBlank(post.getSlug()) && blogPostMapper.selectSlugCount(post.getSlug(), post.getPostId()) > 0) {
            throw new ServiceException("文章别名'{}'已存在", post.getSlug());
        }

        if (post.getCategoryId() != null) {
            BlogCategory category = blogCategoryMapper.selectById(post.getCategoryId());
            if (ObjectUtil.isNull(category) || Boolean.TRUE.equals(category.getDelFlag())) {
                throw new ServiceException("所选分类不存在或已删除");
            }
        }

        if (!StringUtils.inStringIgnoreCase(post.getSourceType(), SOURCE_TYPE_ORIGINAL, SOURCE_TYPE_REPRINT, SOURCE_TYPE_TRANSLATION)) {
            throw new ServiceException("文章来源类型不合法");
        }
        if (!StringUtils.inStringIgnoreCase(post.getStatus(),
            String.valueOf(PostStatus.DRAFT.getCode()),
            String.valueOf(PostStatus.PUBLISHED.getCode()),
            String.valueOf(PostStatus.OFFLINE.getCode()),
            String.valueOf(PostStatus.RECYCLE.getCode()))) {
            throw new ServiceException("文章状态不合法");
        }
        if (!StringUtils.inStringIgnoreCase(post.getIsTop(), "0", "1")) {
            throw new ServiceException("置顶标识不合法");
        }
        if (!StringUtils.inStringIgnoreCase(post.getIsFeatured(), "0", "1")) {
            throw new ServiceException("推荐标识不合法");
        }
        if (!StringUtils.inStringIgnoreCase(post.getAllowComment(), "0", "1")) {
            throw new ServiceException("评论开关标识不合法");
        }
        if (SOURCE_TYPE_ORIGINAL.equals(post.getSourceType())) {
            post.setSourceUrl(null);
        } else if (StringUtils.isBlank(post.getSourceUrl())) {
            throw new ServiceException("转载或翻译文章必须填写原文链接");
        }

        if (CollUtil.isNotEmpty(post.getTagIds())) {
            Long validTagCount = blogTagMapper.selectCount(new LambdaQueryWrapper<BlogTag>()
                .in(BlogTag::getTagId, post.getTagIds()));
            if (validTagCount == null || validTagCount.intValue() != post.getTagIds().size()) {
                throw new ServiceException("存在无效的标签数据");
            }
        }
    }

    /**
     * 归一化标签ID列表
     *
     * @param tagIds 标签ID列表
     * @return 去重后的标签ID列表
     */
    private List<Long> normalizeTagIds(List<Long> tagIds) {
        if (CollUtil.isEmpty(tagIds)) {
            return List.of();
        }
        Set<Long> orderedIds = new LinkedHashSet<>();
        for (Long tagId : tagIds) {
            Long value = normalizeNullableId(tagId);
            if (value != null) {
                orderedIds.add(value);
            }
        }
        return new ArrayList<>(orderedIds);
    }

    /**
     * 归一化可空文本
     *
     * @param value 文本
     * @return 归一化后的文本
     */
    private String normalizeOptionalText(String value) {
        String trimValue = StringUtils.trim(value);
        return StringUtils.isBlank(trimValue) ? null : trimValue;
    }

    /**
     * 归一化可空ID
     *
     * @param value 主键值
     * @return 归一化后的主键值
     */
    private Long normalizeNullableId(Long value) {
        if (value == null || value <= 0) {
            return null;
        }
        return value;
    }

    /**
     * 补齐内容统计
     *
     * @param post 文章业务对象
     */
    private void populateContentStats(BlogPostBo post) {
        int wordCount = ObjectUtil.defaultIfNull(post.getWordCount(), countWord(post.getContent()));
        if (wordCount < 0) {
            wordCount = 0;
        }
        post.setWordCount(wordCount);

        Integer readingTime = post.getReadingTime();
        if (readingTime == null || readingTime < 0) {
            readingTime = wordCount == 0 ? 0 : Math.max(1, (int) Math.ceil(wordCount / 500D));
        }
        post.setReadingTime(readingTime);
    }

    /**
     * 简单估算字数
     *
     * @param content 文章内容
     * @return 字数
     */
    private int countWord(String content) {
        String plainText = StringUtils.defaultString(content)
            .replaceAll("<[^>]+>", StringUtils.EMPTY)
            .replaceAll("\\s+", StringUtils.EMPTY);
        return plainText.length();
    }

    /**
     * 判断是否为已发布状态
     *
     * @param status 状态值
     * @return true 已发布
     */
    private boolean isPublishedStatus(String status) {
        return String.valueOf(PostStatus.PUBLISHED.getCode()).equals(status);
    }

    /**
     * 安全转换为Long
     *
     * @param value 数值
     * @return Long值
     */
    private Long safeLong(Integer value) {
        return value == null ? 0L : value.longValue();
    }

    /**
     * 隐藏公开接口不应返回的敏感字段
     *
     * @param rows 文章列表
     */
    private void hidePublicSensitiveFields(List<BlogPostVo> rows) {
        if (CollUtil.isEmpty(rows)) {
            return;
        }
        rows.forEach(this::hidePublicSensitiveField);
    }

    /**
     * 隐藏公开接口不应返回的敏感字段
     *
     * @param postVo 文章视图对象
     */
    private void hidePublicSensitiveField(BlogPostVo postVo) {
        if (postVo == null) {
            return;
        }
        postVo.setPassword(null);
    }
}

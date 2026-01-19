package com.han.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogLike;
import com.han.blog.domain.bo.BlogLikeBo;
import com.han.blog.domain.vo.BlogLikeVo;
import com.han.blog.mapper.BlogLikeMapper;
import com.han.blog.service.IBlogLikeService;
import com.han.blog.service.IBlogPostStatsService;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客点赞记录Service业务层处理 blog_like
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogLikeServiceImpl implements IBlogLikeService {

    private final BlogLikeMapper blogLikeMapper;

    private final IBlogPostStatsService blogPostStatsService;

    /**
     * 根据条件分页查询点赞记录列表
     *
     * @param like 查询条件
     * @param pageQuery 分页参数
     * @return 点赞记录列表
     */
    @Override
    public TableDataInfo<BlogLikeVo> selectPageLikeList(BlogLikeBo like, PageQuery pageQuery) {
        Page<BlogLikeVo> resultPage = blogLikeMapper.selectPageList(pageQuery.build(), this.buildQueryWrapper(like));
        return TableDataInfo.build(resultPage);
    }

    /**
     * 构建点赞记录查询条件包装器
     *
     * @param like 点赞业务对象，包含查询条件
     * @return 查询条件包装器
     */
    private Wrapper<BlogLike> buildQueryWrapper(BlogLikeBo like) {
        LambdaQueryWrapper<BlogLike> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ObjectUtil.isNotNull(like.getLikeId()), BlogLike::getLikeId, like.getLikeId())
            .eq(ObjectUtil.isNotNull(like.getPostId()), BlogLike::getPostId, like.getPostId())
            .eq(ObjectUtil.isNotNull(like.getUserId()), BlogLike::getUserId, like.getUserId())
            .eq(ObjectUtil.isNotNull(like.getIpAddress()), BlogLike::getIpAddress, like.getIpAddress())
            .orderByDesc(BlogLike::getCreateTime);
        return wrapper;
    }

    /**
     * 查询点赞记录详细信息
     *
     * @param likeId 点赞ID
     * @return 点赞记录详细信息
     */
    @Override
    public BlogLikeVo queryLikeDetail(Long likeId) {
        return blogLikeMapper.selectVoById(likeId);
    }

    /**
     * 查询指定文章的点赞记录列表
     *
     * @param postId 文章ID
     * @return 点赞记录列表
     */
    @Override
    public List<BlogLikeVo> selectLikesByPostId(Long postId) {
        return blogLikeMapper.selectVoList(new LambdaQueryWrapper<BlogLike>()
            .eq(BlogLike::getPostId, postId)
            .orderByDesc(BlogLike::getCreateTime));
    }

    /**
     * 新增点赞记录
     *
     * @param like 点赞业务对象
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertLike(BlogLikeBo like) {
        BlogLike blogLike = MapstructUtils.convert(like, BlogLike.class);
        if (blogLike.getCreateTime() == null) {
            blogLike.setCreateTime(new Date());
        }
        // 不做复杂防重策略，防重由上层或后续扩展处理
        return blogLikeMapper.insert(blogLike);
    }

    /**
     * 取消点赞（删除点赞记录）
     *
     * @param likeId 点赞ID
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLike(Long likeId) {
        BlogLikeVo like = queryLikeDetail(likeId);
        if (like != null) {
            int rows = blogLikeMapper.deleteById(likeId);
            if (rows > 0) {
                // 减少文章点赞数
                blogPostStatsService.decrementLikeCount(like.getPostId());
            }
            return rows;
        }
        return 0;
    }

    /**
     * 批量删除点赞记录
     *
     * @param likeIds 点赞ID列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLikes(Long[] likeIds) {
        List<Long> ids = List.of(likeIds);
        // 先查询出受影响的 postId
        List<BlogLikeVo> likes = blogLikeMapper.selectVoList(new LambdaQueryWrapper<BlogLike>().in(BlogLike::getLikeId, ids));

        int rows = blogLikeMapper.delete(new LambdaQueryWrapper<BlogLike>().in(BlogLike::getLikeId, ids));

        if (rows > 0 && CollUtil.isNotEmpty(likes)) {
            // 批量更新统计数据（这里简化为循环处理，也可优化为批量更新）
            for (BlogLikeVo like : likes) {
                blogPostStatsService.decrementLikeCount(like.getPostId());
            }
        }
        return rows;
    }
}


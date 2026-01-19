package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogComment;
import com.han.blog.domain.bo.BlogCommentBo;
import com.han.blog.domain.vo.BlogCommentVo;
import com.han.blog.mapper.BlogCommentMapper;
import com.han.blog.service.IBlogCommentService;
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
 * @CreateTime: 2026-01-19
 * @Description: 博客评论Service业务层处理 blog_comment
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogCommentServiceImpl implements IBlogCommentService {

    private final BlogCommentMapper blogCommentMapper;

    /**
     * 根据条件分页查询博客评论列表
     *
     * @param comment 查询条件
     * @param pageQuery 分页参数
     * @return 博客评论列表
     */
    @Override
    public TableDataInfo<BlogCommentVo> selectPageCommentList(BlogCommentBo comment, PageQuery pageQuery) {
        Page<BlogCommentVo> resultPage = blogCommentMapper.selectPageList(pageQuery.build(), this.buildQueryWrapper(comment));
        return TableDataInfo.build(resultPage);
    }

    /**
     * 构建博客评论查询条件包装器
     *
     * @param comment 博客评论业务对象，包含查询条件
     * @return 查询条件包装器
     */
    private Wrapper<BlogComment> buildQueryWrapper(BlogCommentBo comment) {
        Map<String, Object> params = comment.getParams();
        LambdaQueryWrapper<BlogComment> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BlogComment::getDelFlag, SystemConstants.NORMAL)
            .eq(ObjectUtil.isNotNull(comment.getCommentId()), BlogComment::getCommentId, comment.getCommentId())
            .eq(ObjectUtil.isNotNull(comment.getPostId()), BlogComment::getPostId, comment.getPostId())
            .eq(ObjectUtil.isNotNull(comment.getParentId()), BlogComment::getParentId, comment.getParentId())
            .eq(ObjectUtil.isNotNull(comment.getUserId()), BlogComment::getUserId, comment.getUserId())
            .like(ObjectUtil.isNotNull(comment.getUserName()), BlogComment::getUserName, comment.getUserName())
            .eq(ObjectUtil.isNotNull(comment.getStatus()), BlogComment::getStatus, comment.getStatus())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogComment::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByAsc(BlogComment::getCreateTime);
        return wrapper;
    }

    /**
     * 查询博客评论详细信息
     *
     * @param commentId 评论ID
     * @return 博客评论详细信息
     */
    @Override
    public BlogCommentVo queryCommentDetail(Long commentId) {
        return blogCommentMapper.selectVoById(commentId);
    }

    /**
     * 新增博客评论
     *
     * @param comment 博客评论业务对象
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertComment(BlogCommentBo comment) {
        BlogComment blogComment = MapstructUtils.convert(comment, BlogComment.class);
        return blogCommentMapper.insert(blogComment);
    }

    /**
     * 修改博客评论
     *
     * @param comment 博客评论业务对象
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateComment(BlogCommentBo comment) {
        BlogComment blogComment = MapstructUtils.convert(comment, BlogComment.class);
        int flag = blogCommentMapper.updateById(blogComment);
        if (flag < 1) {
            throw new ServiceException("修改评论失败");
        }
        return flag;
    }

    /**
     * 批量删除博客评论
     *
     * @param commentIds 评论ID列表
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteComments(Long[] commentIds) {
        List<Long> ids = List.of(commentIds);
        int flag = blogCommentMapper.deleteByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除评论失败");
        }
        return flag;
    }
}

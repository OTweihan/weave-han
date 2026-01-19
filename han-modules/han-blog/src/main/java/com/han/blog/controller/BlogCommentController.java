package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogCommentBo;
import com.han.blog.domain.vo.BlogCommentVo;
import com.han.blog.service.IBlogCommentService;
import com.han.common.core.domain.R;
import com.han.common.idempotent.annotation.RepeatSubmit;
import com.han.common.log.annotation.Log;
import com.han.common.log.enums.BusinessType;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.web.core.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客评论 Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/comment")
public class BlogCommentController extends BaseController {

    private final IBlogCommentService blogCommentService;

    /**
     * 查询博客评论列表
     *
     * @param comment 查询条件
     * @param pageQuery 分页参数
     * @return 博客评论列表
     */
    @SaCheckPermission("blog:comment:list")
    @GetMapping("/list")
    public TableDataInfo<BlogCommentVo> list(BlogCommentBo comment, PageQuery pageQuery) {
        return blogCommentService.selectPageCommentList(comment, pageQuery);
    }

    /**
     * 获取博客评论详细信息
     *
     * @param commentId 评论ID
     * @return 博客评论详细信息
     */
    @SaCheckPermission("blog:comment:query")
    @GetMapping(value = "{commentId}")
    public R<BlogCommentVo> getInfo(@PathVariable Long commentId) {
        return R.ok(blogCommentService.queryCommentDetail(commentId));
    }

    /**
     * 新增博客评论
     *
     * @param comment 博客评论业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:comment:add")
    @Log(title = "博客评论", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogCommentBo comment) {
        return toAjax(blogCommentService.insertComment(comment));
    }

    /**
     * 修改博客评论
     *
     * @param comment 博客评论业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:comment:edit")
    @Log(title = "博客评论", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody BlogCommentBo comment) {
        return toAjax(blogCommentService.updateComment(comment));
    }

    /**
     * 批量删除博客评论
     *
     * @param commentIds 评论ID列表
     * @return 结果
     */
    @SaCheckPermission("blog:comment:remove")
    @Log(title = "博客评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commentIds}")
    public R<Void> remove(@PathVariable Long[] commentIds) {
        return toAjax(blogCommentService.deleteComments(commentIds));
    }
}



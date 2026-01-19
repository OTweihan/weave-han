package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogPostBo;
import com.han.blog.domain.bo.BlogVisitBo;
import com.han.blog.domain.vo.BlogPostVo;
import com.han.blog.service.IBlogPostService;
import com.han.blog.service.IBlogVisitService;
import com.han.common.core.domain.R;
import com.han.common.core.utils.ServletUtils;
import com.han.common.idempotent.annotation.RepeatSubmit;
import com.han.common.log.annotation.Log;
import com.han.common.log.enums.BusinessType;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.web.core.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-05
 * @Description: 博客文章 Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/post")
public class BlogPostController extends BaseController {

    private final IBlogPostService blogPostService;
    private final IBlogVisitService blogVisitService;

    /**
     * 查询博客文章列表
     */
    @SaCheckPermission("blog:post:list")
    @GetMapping("/list")
    public TableDataInfo<BlogPostVo> list(BlogPostBo post, PageQuery pageQuery) {
        return blogPostService.selectPagePostList(post, pageQuery);
    }

    /**
     * 获取博客文章详细信息
     *
     * @param postId 文章ID
     * @return 博客文章详细信息
     */
    @SaCheckPermission("blog:post:query")
    @GetMapping(value = "{postId}")
    public R<BlogPostVo> getInfo(@PathVariable Long postId) {
        return R.ok(blogPostService.queryPostDetail(postId));
    }

    /**
     * 根据文章别名获取文章
     *
     * @param slug 文章别名
     * @return 博客文章
     */
    @GetMapping(value = "/slug/{slug}")
    public R<BlogPostVo> getPostBySlug(@PathVariable String slug) {
        return R.ok(blogPostService.queryPostBySlug(slug));
    }

    /**
     * 新增博客文章
     *
     * @param post 博客文章业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:post:add")
    @Log(title = "博客文章", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogPostBo post) {
        return toAjax(blogPostService.insertPost(post));
    }

    /**
     * 修改博客文章
     *
     * @param post 博客文章业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:post:edit")
    @Log(title = "博客文章", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody BlogPostBo post) {
        return toAjax(blogPostService.updatePost(post));
    }

    /**
     * 批量删除博客文章
     *
     * @param postIds 文章ID列表
     * @return 结果
     */
    @SaCheckPermission("blog:post:remove")
    @Log(title = "博客文章", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public R<Void> remove(@PathVariable Long[] postIds) {
        return toAjax(blogPostService.deletePosts(postIds));
    }

    /**
     * 更新文章浏览量
     *
     * @param postId 文章ID
     * @return 结果
     */
    @PostMapping("/view/{postId}")
    public R<Void> incrementView(@PathVariable Long postId) {
        // 记录访问日志
        BlogVisitBo visitBo = new BlogVisitBo();
        visitBo.setPostId(postId);
        visitBo.setVisitTime(new Date());
        visitBo.setIpAddress(ServletUtils.getClientIP());
        jakarta.servlet.http.HttpServletRequest request = ServletUtils.getRequest();
        if (request != null) {
            visitBo.setUserAgent(request.getHeader("User-Agent"));
            visitBo.setReferer(request.getHeader("Referer"));
        }
        blogVisitService.insertByBo(visitBo);

        return toAjax(blogPostService.incrementViewCount(postId));
    }
}

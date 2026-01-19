package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogLikeBo;
import com.han.blog.domain.vo.BlogLikeVo;
import com.han.blog.service.IBlogLikeService;
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

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客点赞记录 Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/like")
public class BlogLikeController extends BaseController {

    private final IBlogLikeService blogLikeService;

    /**
     * 查询点赞记录列表
     *
     * @param like 查询条件
     * @param pageQuery 分页参数
     * @return 点赞记录列表
     */
    @SaCheckPermission("blog:like:list")
    @GetMapping("/list")
    public TableDataInfo<BlogLikeVo> list(BlogLikeBo like, PageQuery pageQuery) {
        return blogLikeService.selectPageLikeList(like, pageQuery);
    }

    /**
     * 获取点赞记录详细信息
     *
     * @param likeId 点赞ID
     * @return 点赞记录详细信息
     */
    @SaCheckPermission("blog:like:query")
    @GetMapping(value = "{likeId}")
    public R<BlogLikeVo> getInfo(@PathVariable Long likeId) {
        return R.ok(blogLikeService.queryLikeDetail(likeId));
    }

    /**
     * 查询指定文章的点赞记录列表
     *
     * @param postId 文章ID
     * @return 点赞记录列表
     */
    @SaCheckPermission("blog:like:list")
    @GetMapping("/post/{postId}")
    public R<List<BlogLikeVo>> listByPost(@PathVariable Long postId) {
        return R.ok(blogLikeService.selectLikesByPostId(postId));
    }

    /**
     * 新增点赞记录
     *
     * @param like 点赞业务对象
     * @return 结果
     */
    @Log(title = "文章点赞", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogLikeBo like) {
        return toAjax(blogLikeService.insertLike(like));
    }

    /**
     * 取消点赞（删除单条点赞记录）
     *
     * @param likeId 点赞ID
     * @return 结果
     */
    @Log(title = "文章点赞", businessType = BusinessType.DELETE)
    @RepeatSubmit()
    @DeleteMapping("/{likeId}")
    public R<Void> remove(@PathVariable Long likeId) {
        return toAjax(blogLikeService.deleteLike(likeId));
    }

    /**
     * 批量删除点赞记录
     *
     * @param likeIds 点赞ID列表
     * @return 结果
     */
    @SaCheckPermission("blog:like:remove")
    @Log(title = "文章点赞", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{likeIds}")
    public R<Void> batchRemove(@PathVariable Long[] likeIds) {
        return toAjax(blogLikeService.deleteLikes(likeIds));
    }
}



package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogTagBo;
import com.han.blog.domain.vo.BlogTagVo;
import com.han.blog.service.IBlogTagService;
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
 * @CreateTime: 2026-01-14
 * @Description: 博客标签 Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/tag")
public class BlogTagController extends BaseController {

    private final IBlogTagService blogTagService;

    /**
     * 查询博客标签列表
     */
    @SaCheckPermission("blog:tag:list")
    @GetMapping("/list")
    public TableDataInfo<BlogTagVo> list(BlogTagBo tag, PageQuery pageQuery) {
        return blogTagService.selectPageTagList(tag, pageQuery);
    }

    /**
     * 获取博客标签详细信息
     *
     * @param tagId 标签ID
     * @return 博客标签详细信息
     */
    @SaCheckPermission("blog:tag:query")
    @GetMapping(value = "{tagId}")
    public R<BlogTagVo> getInfo(@PathVariable Long tagId) {
        return R.ok(blogTagService.queryTagDetail(tagId));
    }

    /**
     * 根据标签别名获取标签
     *
     * @param slug 标签别名
     * @return 博客标签
     */
    @GetMapping(value = "/slug/{slug}")
    public R<BlogTagVo> getTagBySlug(@PathVariable String slug) {
        return R.ok(blogTagService.queryTagBySlug(slug));
    }

    /**
     * 新增博客标签
     *
     * @param tag 博客标签业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:tag:add")
    @Log(title = "博客标签", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogTagBo tag) {
        return toAjax(blogTagService.insertTag(tag));
    }

    /**
     * 修改博客标签
     *
     * @param tag 博客标签业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:tag:edit")
    @Log(title = "博客标签", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody BlogTagBo tag) {
        return toAjax(blogTagService.updateTag(tag));
    }

    /**
     * 批量删除博客标签
     *
     * @param tagIds 标签ID列表
     * @return 结果
     */
    @SaCheckPermission("blog:tag:remove")
    @Log(title = "博客标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tagIds}")
    public R<Void> remove(@PathVariable Long[] tagIds) {
        return toAjax(blogTagService.deleteTags(tagIds));
    }

    /**
     * 获取所有标签列表（用于多选标签）
     *
     * @return 标签列表
     */
    @GetMapping("/all")
    public R<List<BlogTagVo>> getAllTags() {
        return R.ok(blogTagService.selectAllTags());
    }
}
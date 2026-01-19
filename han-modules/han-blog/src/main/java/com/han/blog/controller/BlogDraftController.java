package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogDraftBo;
import com.han.blog.domain.vo.BlogDraftVo;
import com.han.blog.service.IBlogDraftService;
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
 * @Description: 博客草稿 Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/draft")
public class BlogDraftController extends BaseController {

    private final IBlogDraftService blogDraftService;

    /**
     * 查询博客草稿列表
     */
    @SaCheckPermission("blog:draft:list")
    @GetMapping("/list")
    public TableDataInfo<BlogDraftVo> list(BlogDraftBo bo, PageQuery pageQuery) {
        return blogDraftService.selectPageDraftList(bo, pageQuery);
    }

    /**
     * 获取博客草稿详细信息
     *
     * @param draftId 草稿ID
     * @return 博客草稿详细信息
     */
    @SaCheckPermission("blog:draft:query")
    @GetMapping(value = "/{draftId}")
    public R<BlogDraftVo> getInfo(@PathVariable Long draftId) {
        return R.ok(blogDraftService.queryDraftDetail(draftId));
    }

    /**
     * 新增博客草稿
     *
     * @param bo 博客草稿业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:draft:add")
    @Log(title = "博客草稿", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogDraftBo bo) {
        return toAjax(blogDraftService.insertDraft(bo));
    }

    /**
     * 修改博客草稿
     *
     * @param bo 博客草稿业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:draft:edit")
    @Log(title = "博客草稿", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody BlogDraftBo bo) {
        return toAjax(blogDraftService.updateDraft(bo));
    }

    /**
     * 批量删除博客草稿
     *
     * @param draftIds 草稿ID列表
     * @return 结果
     */
    @SaCheckPermission("blog:draft:remove")
    @Log(title = "博客草稿", businessType = BusinessType.DELETE)
    @DeleteMapping("/{draftIds}")
    public R<Void> remove(@PathVariable Long[] draftIds) {
        return toAjax(blogDraftService.deleteDrafts(draftIds));
    }
}

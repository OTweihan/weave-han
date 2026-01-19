package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogPageBo;
import com.han.blog.domain.vo.BlogPageVo;
import com.han.blog.service.IBlogPageService;
import com.han.common.core.domain.R;
import com.han.common.core.validate.AddGroup;
import com.han.common.core.validate.EditGroup;
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
 * @Description: 博客页面控制器
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/page")
public class BlogPageController extends BaseController {

    private final IBlogPageService blogPageService;

    /**
     * 查询博客页面列表
     */
    @SaCheckPermission("blog:page:list")
    @GetMapping("/list")
    public TableDataInfo<BlogPageVo> list(BlogPageBo bo, PageQuery pageQuery) {
        return blogPageService.selectPagePageList(bo, pageQuery);
    }

    /**
     * 获取博客页面详细信息
     *
     * @param pageId 页面ID
     */
    @SaCheckPermission("blog:page:query")
    @GetMapping("/{pageId}")
    public R<BlogPageVo> getInfo(@PathVariable Long pageId) {
        return R.ok(blogPageService.queryPageDetail(pageId));
    }

    /**
     * 新增博客页面
     */
    @SaCheckPermission("blog:page:add")
    @Log(title = "博客页面", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlogPageBo bo) {
        return toAjax(blogPageService.insertPage(bo));
    }

    /**
     * 修改博客页面
     */
    @SaCheckPermission("blog:page:edit")
    @Log(title = "博客页面", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlogPageBo bo) {
        return toAjax(blogPageService.updatePage(bo));
    }

    /**
     * 删除博客页面
     *
     * @param pageIds 页面ID串
     */
    @SaCheckPermission("blog:page:remove")
    @Log(title = "博客页面", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pageIds}")
    public R<Void> remove(@PathVariable Long[] pageIds) {
        return toAjax(blogPageService.deletePages(pageIds));
    }

    /**
     * 获取所有公开页面列表（用于前端菜单等）
     */
    @GetMapping("/public")
    public R<List<BlogPageVo>> getAllPublicPages() {
        return R.ok(blogPageService.selectAllPublicPages());
    }

    /**
     * 根据别名获取公开页面详细信息（用于前端展示）
     *
     * @param slug 页面别名
     */
    @GetMapping("/public/slug/{slug}")
    public R<BlogPageVo> getPublicPageBySlug(@PathVariable String slug) {
        return R.ok(blogPageService.queryPageBySlug(slug));
    }
}

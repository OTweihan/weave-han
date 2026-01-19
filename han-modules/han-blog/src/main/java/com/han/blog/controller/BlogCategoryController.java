package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogCategoryBo;
import com.han.blog.domain.vo.BlogCategoryVo;
import com.han.blog.service.IBlogCategoryService;
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
 * @CreateTime: 2026-01-16
 * @Description: 博客分类 Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/category")
public class BlogCategoryController extends BaseController {

    private final IBlogCategoryService blogCategoryService;

    /**
     * 查询博客分类列表
     */
    @SaCheckPermission("blog:category:list")
    @GetMapping("/list")
    public TableDataInfo<BlogCategoryVo> list(BlogCategoryBo category, PageQuery pageQuery) {
        return blogCategoryService.selectPageCategoryList(category, pageQuery);
    }

    /**
     * 获取博客分类详细信息
     *
     * @param categoryId 分类ID
     * @return 博客分类详细信息
     */
    @SaCheckPermission("blog:category:query")
    @GetMapping(value = "{categoryId}")
    public R<BlogCategoryVo> getInfo(@PathVariable Long categoryId) {
        return R.ok(blogCategoryService.queryCategoryDetail(categoryId));
    }

    /**
     * 根据分类别名获取分类
     *
     * @param slug 分类别名
     * @return 博客分类
     */
    @GetMapping(value = "/slug/{slug}")
    public R<BlogCategoryVo> getCategoryBySlug(@PathVariable String slug) {
        return R.ok(blogCategoryService.queryCategoryBySlug(slug));
    }

    /**
     * 新增博客分类
     *
     * @param category 博客分类业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:category:add")
    @Log(title = "博客分类", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogCategoryBo category) {
        return toAjax(blogCategoryService.insertCategory(category));
    }

    /**
     * 修改博客分类
     *
     * @param category 博客分类业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:category:edit")
    @Log(title = "博客分类", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody BlogCategoryBo category) {
        return toAjax(blogCategoryService.updateCategory(category));
    }

    /**
     * 批量删除博客分类
     *
     * @param categoryIds 分类ID列表
     * @return 结果
     */
    @SaCheckPermission("blog:category:remove")
    @Log(title = "博客分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public R<Void> remove(@PathVariable Long[] categoryIds) {
        return toAjax(blogCategoryService.deleteCategories(categoryIds));
    }

    /**
     * 获取所有分类列表（用于下拉选择）
     *
     * @return 分类列表
     */
    @GetMapping("/all")
    public R<List<BlogCategoryVo>> getAllCategories() {
        return R.ok(blogCategoryService.selectAllCategories());
    }
}

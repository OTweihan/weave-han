package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogImageBo;
import com.han.blog.domain.vo.BlogImageVo;
import com.han.blog.service.IBlogImageService;
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
 * @Description: 博客图片库 Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/image")
public class BlogImageController extends BaseController {

    private final IBlogImageService blogImageService;

    /**
     * 查询博客图片列表
     *
     * @param image 查询条件
     * @param pageQuery 分页参数
     * @return 博客图片列表
     */
    @SaCheckPermission("blog:image:list")
    @GetMapping("/list")
    public TableDataInfo<BlogImageVo> list(BlogImageBo image, PageQuery pageQuery) {
        return blogImageService.selectPageImageList(image, pageQuery);
    }

    /**
     * 获取博客图片详细信息
     *
     * @param imageId 图片ID
     * @return 博客图片详细信息
     */
    @SaCheckPermission("blog:image:query")
    @GetMapping(value = "{imageId}")
    public R<BlogImageVo> getInfo(@PathVariable Long imageId) {
        return R.ok(blogImageService.queryImageDetail(imageId));
    }

    /**
     * 新增博客图片
     *
     * @param image 博客图片业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:image:add")
    @Log(title = "博客图片", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogImageBo image) {
        return toAjax(blogImageService.insertImage(image));
    }

    /**
     * 修改博客图片
     *
     * @param image 博客图片业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:image:edit")
    @Log(title = "博客图片", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody BlogImageBo image) {
        return toAjax(blogImageService.updateImage(image));
    }

    /**
     * 批量删除博客图片
     *
     * @param imageIds 图片ID列表
     * @return 结果
     */
    @SaCheckPermission("blog:image:remove")
    @Log(title = "博客图片", businessType = BusinessType.DELETE)
    @DeleteMapping("/{imageIds}")
    public R<Void> remove(@PathVariable Long[] imageIds) {
        return toAjax(blogImageService.deleteImages(imageIds));
    }

    /**
     * 获取所有公开图片列表（用于选择器）
     *
     * @return 博客图片列表
     */
    @GetMapping("/public")
    public R<List<BlogImageVo>> getAllPublicImages() {
        return R.ok(blogImageService.selectAllPublicImages());
    }
}



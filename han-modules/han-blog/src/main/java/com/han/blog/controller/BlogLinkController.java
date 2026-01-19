package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogLinkBo;
import com.han.blog.domain.vo.BlogLinkVo;
import com.han.blog.service.IBlogLinkService;
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
 * @Description: 博客友情链接 Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/link")
public class BlogLinkController extends BaseController {

    private final IBlogLinkService blogLinkService;

    /**
     * 查询友情链接列表
     *
     * @param link 查询条件
     * @param pageQuery 分页参数
     * @return 友情链接列表
     */
    @SaCheckPermission("blog:link:list")
    @GetMapping("/list")
    public TableDataInfo<BlogLinkVo> list(BlogLinkBo link, PageQuery pageQuery) {
        return blogLinkService.selectPageLinkList(link, pageQuery);
    }

    /**
     * 获取友情链接详细信息
     *
     * @param linkId 链接ID
     * @return 友情链接详细信息
     */
    @SaCheckPermission("blog:link:query")
    @GetMapping(value = "{linkId}")
    public R<BlogLinkVo> getInfo(@PathVariable Long linkId) {
        return R.ok(blogLinkService.queryLinkDetail(linkId));
    }

    /**
     * 新增友情链接
     *
     * @param link 友情链接业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:link:add")
    @Log(title = "友情链接", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogLinkBo link) {
        return toAjax(blogLinkService.insertLink(link));
    }

    /**
     * 修改友情链接
     *
     * @param link 友情链接业务对象
     * @return 结果
     */
    @SaCheckPermission("blog:link:edit")
    @Log(title = "友情链接", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody BlogLinkBo link) {
        return toAjax(blogLinkService.updateLink(link));
    }

    /**
     * 批量删除友情链接
     *
     * @param linkIds 链接ID列表
     * @return 结果
     */
    @SaCheckPermission("blog:link:remove")
    @Log(title = "友情链接", businessType = BusinessType.DELETE)
    @DeleteMapping("/{linkIds}")
    public R<Void> remove(@PathVariable Long[] linkIds) {
        return toAjax(blogLinkService.deleteLinks(linkIds));
    }

    /**
     * 获取所有公开友情链接列表（用于前端展示）
     *
     * @return 友情链接列表
     */
    @GetMapping("/public")
    public R<List<BlogLinkVo>> getAllPublicLinks() {
        return R.ok(blogLinkService.selectAllPublicLinks());
    }
}

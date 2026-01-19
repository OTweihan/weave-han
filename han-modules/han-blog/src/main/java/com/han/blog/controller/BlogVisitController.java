package com.han.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.blog.domain.bo.BlogVisitBo;
import com.han.blog.domain.vo.BlogVisitVo;
import com.han.blog.service.IBlogVisitService;
import com.han.common.core.domain.R;
import com.han.common.core.validate.AddGroup;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.web.core.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客访问记录控制器
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/blog/visit")
public class BlogVisitController extends BaseController {

    private final IBlogVisitService blogVisitService;

    /**
     * 查询访问记录列表
     */
    @SaCheckPermission("blog:visit:list")
    @GetMapping("/list")
    public TableDataInfo<BlogVisitVo> list(BlogVisitBo bo, PageQuery pageQuery) {
        return blogVisitService.selectPageList(bo, pageQuery);
    }

    /**
     * 新增访问记录
     */
    @SaCheckPermission("blog:visit:add")
    @PostMapping
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlogVisitBo bo) {
        return toAjax(blogVisitService.insertByBo(bo));
    }
}

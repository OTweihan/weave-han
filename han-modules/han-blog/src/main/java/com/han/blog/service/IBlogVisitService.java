package com.han.blog.service;

import com.han.blog.domain.bo.BlogVisitBo;
import com.han.blog.domain.vo.BlogVisitVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客访问记录Service接口
 */
public interface IBlogVisitService {

    /**
     * 查询访问记录列表
     *
     * @param bo        访问记录业务对象
     * @param pageQuery 分页查询对象
     * @return 访问记录分页列表
     */
    TableDataInfo<BlogVisitVo> selectPageList(BlogVisitBo bo, PageQuery pageQuery);

    /**
     * 新增访问记录
     *
     * @param bo 访问记录业务对象
     * @return 是否成功
     */
    Boolean insertByBo(BlogVisitBo bo);
}

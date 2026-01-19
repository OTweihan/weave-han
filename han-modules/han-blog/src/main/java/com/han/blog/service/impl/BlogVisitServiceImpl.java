package com.han.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogVisit;
import com.han.blog.domain.bo.BlogVisitBo;
import com.han.blog.domain.vo.BlogVisitVo;
import com.han.blog.mapper.BlogVisitMapper;
import com.han.blog.service.IBlogVisitService;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客访问记录Service业务层处理
 */
@RequiredArgsConstructor
@Service
public class BlogVisitServiceImpl implements IBlogVisitService {

    private final BlogVisitMapper baseMapper;

    /**
     * 查询访问记录列表
     */
    @Override
    public TableDataInfo<BlogVisitVo> selectPageList(BlogVisitBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlogVisit> lqw = buildQueryWrapper(bo);
        Page<BlogVisitVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<BlogVisit> buildQueryWrapper(BlogVisitBo bo) {
        LambdaQueryWrapper<BlogVisit> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPostId() != null, BlogVisit::getPostId, bo.getPostId());
        lqw.like(StringUtils.isNotBlank(bo.getIpAddress()), BlogVisit::getIpAddress, bo.getIpAddress());
        lqw.like(StringUtils.isNotBlank(bo.getUserAgent()), BlogVisit::getUserAgent, bo.getUserAgent());
        lqw.like(StringUtils.isNotBlank(bo.getReferer()), BlogVisit::getReferer, bo.getReferer());
        lqw.orderByDesc(BlogVisit::getVisitTime);
        return lqw;
    }

    /**
     * 新增访问记录
     */
    @Override
    public Boolean insertByBo(BlogVisitBo bo) {
        BlogVisit add = MapstructUtils.convert(bo, BlogVisit.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setVisitId(add.getVisitId());
        }
        return flag;
    }
}

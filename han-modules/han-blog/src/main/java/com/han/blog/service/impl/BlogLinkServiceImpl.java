package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogLink;
import com.han.blog.domain.bo.BlogLinkBo;
import com.han.blog.domain.vo.BlogLinkVo;
import com.han.blog.mapper.BlogLinkMapper;
import com.han.blog.service.IBlogLinkService;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客友情链接Service业务层处理 blog_link
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogLinkServiceImpl implements IBlogLinkService {

    private final BlogLinkMapper blogLinkMapper;

    /**
     * 根据条件分页查询友情链接列表
     *
     * @param link 查询条件
     * @param pageQuery 分页参数
     * @return 友情链接列表
     */
    @Override
    public TableDataInfo<BlogLinkVo> selectPageLinkList(BlogLinkBo link, PageQuery pageQuery) {
        Page<BlogLinkVo> resultPage = blogLinkMapper.selectPageList(pageQuery.build(), this.buildQueryWrapper(link));
        return TableDataInfo.build(resultPage);
    }

    /**
     * 构建友情链接查询条件包装器
     *
     * @param link 友情链接业务对象，包含查询条件
     * @return 查询条件包装器
     */
    private Wrapper<BlogLink> buildQueryWrapper(BlogLinkBo link) {
        Map<String, Object> params = link.getParams();
        LambdaQueryWrapper<BlogLink> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ObjectUtil.isNotNull(link.getLinkId()), BlogLink::getLinkId, link.getLinkId())
            .like(ObjectUtil.isNotNull(link.getName()), BlogLink::getName, link.getName())
            .like(ObjectUtil.isNotNull(link.getUrl()), BlogLink::getUrl, link.getUrl())
            .like(ObjectUtil.isNotNull(link.getDescription()), BlogLink::getDescription, link.getDescription())
            .eq(ObjectUtil.isNotNull(link.getStatus()), BlogLink::getStatus, link.getStatus())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogLink::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByAsc(BlogLink::getSortOrder)
            .orderByDesc(BlogLink::getCreateTime);
        return wrapper;
    }

    /**
     * 查询友情链接详细信息
     *
     * @param linkId 链接ID
     * @return 友情链接详细信息
     */
    @Override
    public BlogLinkVo queryLinkDetail(Long linkId) {
        return blogLinkMapper.selectVoById(linkId);
    }

    /**
     * 新增友情链接
     *
     * @param link 友情链接业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertLink(BlogLinkBo link) {
        BlogLink blogLink = MapstructUtils.convert(link, BlogLink.class);
        return blogLinkMapper.insert(blogLink);
    }

    /**
     * 修改友情链接
     *
     * @param link 友情链接业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLink(BlogLinkBo link) {
        BlogLink blogLink = MapstructUtils.convert(link, BlogLink.class);
        int flag = blogLinkMapper.updateById(blogLink);
        if (flag < 1) {
            throw new ServiceException("修改友情链接失败");
        }
        return flag;
    }

    /**
     * 批量删除友情链接
     *
     * @param linkIds 链接ID列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLinks(Long[] linkIds) {
        List<Long> ids = List.of(linkIds);
        int flag = blogLinkMapper.deleteByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除友情链接失败");
        }
        return flag;
    }

    /**
     * 查询所有公开友情链接列表（不分页）
     *
     * @return 友情链接列表
     */
    @Override
    public List<BlogLinkVo> selectAllPublicLinks() {
        return blogLinkMapper.selectVoList(new LambdaQueryWrapper<BlogLink>()
            .eq(BlogLink::getStatus, SystemConstants.NORMAL)
            .orderByAsc(BlogLink::getSortOrder)
            .orderByDesc(BlogLink::getCreateTime));
    }
}

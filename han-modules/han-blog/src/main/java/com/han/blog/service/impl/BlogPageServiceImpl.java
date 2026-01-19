package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogPage;
import com.han.blog.domain.bo.BlogPageBo;
import com.han.blog.domain.vo.BlogPageVo;
import com.han.blog.mapper.BlogPageMapper;
import com.han.blog.service.IBlogPageService;
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
 * @Description: 博客页面Service业务层处理 blog_page
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogPageServiceImpl implements IBlogPageService {

    private final BlogPageMapper blogPageMapper;

    /**
     * 根据条件分页查询博客页面列表
     *
     * @param page 查询条件
     * @param pageQuery 分页参数
     * @return 博客页面列表
     */
    @Override
    public TableDataInfo<BlogPageVo> selectPagePageList(BlogPageBo page, PageQuery pageQuery) {
        Page<BlogPageVo> resultPage = blogPageMapper.selectPageList(pageQuery.build(), this.buildQueryWrapper(page));
        return TableDataInfo.build(resultPage);
    }

    /**
     * 构建博客页面查询条件包装器
     *
     * @param page 博客页面业务对象，包含查询条件
     * @return 查询条件包装器
     */
    private Wrapper<BlogPage> buildQueryWrapper(BlogPageBo page) {
        Map<String, Object> params = page.getParams();
        LambdaQueryWrapper<BlogPage> wrapper = Wrappers.lambdaQuery();
        // 筛选未删除的页面（逻辑删除，依赖MyBatis-Plus @TableLogic）
        wrapper.eq(ObjectUtil.isNotNull(page.getPageId()), BlogPage::getPageId, page.getPageId())
            .like(ObjectUtil.isNotNull(page.getTitle()), BlogPage::getTitle, page.getTitle())
            .like(ObjectUtil.isNotNull(page.getSlug()), BlogPage::getSlug, page.getSlug())
            .eq(ObjectUtil.isNotNull(page.getStatus()), BlogPage::getStatus, page.getStatus())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogPage::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByAsc(BlogPage::getSortOrder)
            .orderByDesc(BlogPage::getCreateTime);
        return wrapper;
    }

    /**
     * 查询博客页面详细信息
     *
     * @param pageId 页面ID
     * @return 博客页面详细信息
     */
    @Override
    public BlogPageVo queryPageDetail(Long pageId) {
        return blogPageMapper.selectVoById(pageId);
    }

    /**
     * 根据别名查询页面
     *
     * @param slug 页面别名
     * @return 博客页面详细信息
     */
    @Override
    public BlogPageVo queryPageBySlug(String slug) {
        return blogPageMapper.selectVoOne(new LambdaQueryWrapper<BlogPage>()
            .eq(BlogPage::getSlug, slug));
    }

    /**
     * 新增博客页面
     *
     * @param page 博客页面业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPage(BlogPageBo page) {
        BlogPage blogPage = MapstructUtils.convert(page, BlogPage.class);
        return blogPageMapper.insert(blogPage);
    }

    /**
     * 修改博客页面
     *
     * @param page 博客页面业务对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePage(BlogPageBo page) {
        BlogPage blogPage = MapstructUtils.convert(page, BlogPage.class);
        int flag = blogPageMapper.updateById(blogPage);
        if (flag < 1) {
            throw new ServiceException("修改页面失败");
        }
        return flag;
    }

    /**
     * 批量删除博客页面
     *
     * @param pageIds 页面ID列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePages(Long[] pageIds) {
        List<Long> ids = List.of(pageIds);
        int flag = blogPageMapper.deleteByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除页面失败");
        }
        return flag;
    }

    /**
     * 查询所有公开页面列表（用于菜单配置等）
     *
     * @return 博客页面列表
     */
    @Override
    public List<BlogPageVo> selectAllPublicPages() {
        return blogPageMapper.selectVoList(new LambdaQueryWrapper<BlogPage>()
            // 假设 "1" 为发布状态，需确认 SystemConstants 定义，这里暂用 NORMAL=0 的反义词或显式值
            // 通常 Status: 0草稿 1发布 2下架。这里假设发布为 "1"
            .eq(BlogPage::getStatus, "1")
            .orderByAsc(BlogPage::getSortOrder));
    }
}

package com.han.blog.service;

import com.han.blog.domain.bo.BlogImageBo;
import com.han.blog.domain.vo.BlogImageVo;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-19
 * @Description: 博客图片库Service接口 blog_image
 */
public interface IBlogImageService {

    /**
     * 查询博客图片列表（分页）
     *
     * @param bo 查询条件
     * @param pageQuery 分页参数
     * @return 博客图片列表
     */
    TableDataInfo<BlogImageVo> selectPageImageList(BlogImageBo bo, PageQuery pageQuery);

    /**
     * 查询单条博客图片详细信息
     *
     * @param imageId 图片ID
     * @return 博客图片详细信息
     */
    BlogImageVo queryImageDetail(Long imageId);

    /**
     * 新增博客图片
     *
     * @param image 博客图片业务对象
     * @return 影响行数
     */
    int insertImage(BlogImageBo image);

    /**
     * 修改博客图片
     *
     * @param image 博客图片业务对象
     * @return 影响行数
     */
    int updateImage(BlogImageBo image);

    /**
     * 批量删除博客图片
     *
     * @param imageIds 图片ID列表
     * @return 影响行数
     */
    int deleteImages(Long[] imageIds);

    /**
     * 查询所有公开图片列表（不分页）
     *
     * @return 博客图片列表
     */
    List<BlogImageVo> selectAllPublicImages();
}


package com.han.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.blog.domain.BlogImage;
import com.han.blog.domain.bo.BlogImageBo;
import com.han.blog.domain.vo.BlogImageVo;
import com.han.blog.mapper.BlogImageMapper;
import com.han.blog.service.IBlogImageService;
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
 * @Description: 博客图片库Service业务层处理 blog_image
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BlogImageServiceImpl implements IBlogImageService {

    private final BlogImageMapper blogImageMapper;

    /**
     * 根据条件分页查询博客图片列表
     *
     * @param image 查询条件
     * @param pageQuery 分页参数
     * @return 博客图片列表
     */
    @Override
    public TableDataInfo<BlogImageVo> selectPageImageList(BlogImageBo image, PageQuery pageQuery) {
        Page<BlogImageVo> resultPage = blogImageMapper.selectPageList(pageQuery.build(), this.buildQueryWrapper(image));
        return TableDataInfo.build(resultPage);
    }

    /**
     * 构建博客图片查询条件包装器
     *
     * @param image 博客图片业务对象，包含查询条件
     * @return 查询条件包装器
     */
    private Wrapper<BlogImage> buildQueryWrapper(BlogImageBo image) {
        Map<String, Object> params = image.getParams();
        LambdaQueryWrapper<BlogImage> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(BlogImage::getDelFlag, SystemConstants.NORMAL)
            .eq(ObjectUtil.isNotNull(image.getImageId()), BlogImage::getImageId, image.getImageId())
            .eq(ObjectUtil.isNotNull(image.getOssId()), BlogImage::getOssId, image.getOssId())
            .eq(ObjectUtil.isNotNull(image.getPostId()), BlogImage::getPostId, image.getPostId())
            .eq(ObjectUtil.isNotNull(image.getCategoryId()), BlogImage::getCategoryId, image.getCategoryId())
            .eq(ObjectUtil.isNotNull(image.getImageType()), BlogImage::getImageType, image.getImageType())
            .like(ObjectUtil.isNotNull(image.getImageName()), BlogImage::getImageName, image.getImageName())
            .eq(ObjectUtil.isNotNull(image.getIsPublic()), BlogImage::getIsPublic, image.getIsPublic())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                BlogImage::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByDesc(BlogImage::getCreateTime);
        return wrapper;
    }

    /**
     * 查询博客图片详细信息
     *
     * @param imageId 图片ID
     * @return 博客图片详细信息
     */
    @Override
    public BlogImageVo queryImageDetail(Long imageId) {
        return blogImageMapper.selectVoById(imageId);
    }

    /**
     * 新增博客图片
     *
     * @param image 博客图片业务对象
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertImage(BlogImageBo image) {
        BlogImage blogImage = MapstructUtils.convert(image, BlogImage.class);
        return blogImageMapper.insert(blogImage);
    }

    /**
     * 修改博客图片
     *
     * @param image 博客图片业务对象
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateImage(BlogImageBo image) {
        BlogImage blogImage = MapstructUtils.convert(image, BlogImage.class);
        int flag = blogImageMapper.updateById(blogImage);
        if (flag < 1) {
            throw new ServiceException("修改图片失败");
        }
        return flag;
    }

    /**
     * 批量删除博客图片
     *
     * @param imageIds 图片ID列表
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteImages(Long[] imageIds) {
        List<Long> ids = List.of(imageIds);
        int flag = blogImageMapper.deleteByIds(ids);
        if (flag < 1) {
            throw new ServiceException("删除图片失败");
        }
        return flag;
    }

    /**
     * 查询所有公开图片列表（不分页）
     *
     * @return 博客图片列表
     */
    @Override
    public List<BlogImageVo> selectAllPublicImages() {
        return blogImageMapper.selectVoList(new LambdaQueryWrapper<BlogImage>()
            .eq(BlogImage::getDelFlag, SystemConstants.NORMAL)
            .eq(BlogImage::getIsPublic, SystemConstants.NORMAL)
            .orderByDesc(BlogImage::getCreateTime));
    }
}

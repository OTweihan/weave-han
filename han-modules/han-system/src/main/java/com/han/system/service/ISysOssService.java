package com.han.system.service;

import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.system.domain.SysOss;
import com.han.system.domain.bo.SysOssBo;
import com.han.system.domain.bo.SysOssCreateBo;
import com.han.system.domain.bo.SysOssPresignedUrlBo;
import com.han.system.domain.vo.SysOssVo;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 文件上传 服务层
 */
public interface ISysOssService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件对象
     */
    SysOssVo upload(MultipartFile file);

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param content   文件内容
     * @param name      文件名称，允许空
     * @param directory 目录，允许空
     * @param type      文件的 MIME 类型，允许空
     * @return 文件对象
     */
    SysOssVo createFile(@NotEmpty(message = "文件内容不能为空") byte[] content, String name, String directory, String type);

    /**
     * 生成文件预签名地址信息，用于上传
     *
     * @param name      文件名
     * @param directory 目录
     * @return 预签名地址信息
     */
    SysOssPresignedUrlBo presignPutUrl(@NotEmpty(message = "文件名不能为空") String name, String directory);

    /**
     * 创建文件
     *
     * @param createVo 创建信息
     * @return 编号
     */
    Long createFile(SysOssCreateBo createVo);

    /**
     * 查询文件
     *
     * @param id 文件ID
     * @return 文件对象
     */
    SysOss getOssFile(Long id);

    /**
     * 删除文件
     *
     * @param ids 编号列表
     */
    void deleteFile(List<Long> ids) throws Exception;

    /**
     * 获得文件内容
     *
     * @param configId 配置编号
     * @param path     文件路径
     * @return 文件内容
     */
    byte[] getFileContent(Long configId, String path) throws Exception;

    /**
     * 查询文件列表
     *
     * @param ossBo 文件信息
     * @param pageQuery 分页查询
     * @return 文件列表
     */
    TableDataInfo<SysOssVo> selectPageOssList(SysOssBo ossBo, PageQuery pageQuery);
}

package com.han.system.service;

import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.storage.core.StorageClient;
import com.han.system.domain.bo.SysStorageConfigBo;
import com.han.system.domain.vo.SysStorageConfigVo;

import java.util.Collection;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 对象存储配置Service接口
 */
public interface ISysStorageConfigService {

    /**
     * 初始化对象存储配置
     *
     * <p>一般用于系统启动时加载主对象存储配置，
     * 并完成相关客户端的初始化。</p>
     */
    void init();

    /**
     * 根据主键查询对象存储配置
     *
     * @param storageConfigId 对象存储配置ID
     * @return 对象存储配置详情
     */
    SysStorageConfigVo queryById(Long storageConfigId);

    /**
     * 分页查询对象存储配置列表
     *
     * @param storageConfigBo 查询条件
     * @param pageQuery       分页参数
     * @return 分页结果
     */
    TableDataInfo<SysStorageConfigVo> queryPageList(SysStorageConfigBo storageConfigBo, PageQuery pageQuery);

    /**
     * 新增对象存储配置
     *
     * @param storageConfigBo 对象存储配置业务对象
     */
    void insertStorageConfig(SysStorageConfigBo storageConfigBo);

    /**
     * 修改对象存储配置
     *
     * @param storageConfigBo 对象存储配置业务对象
     */
    void updateStorageConfig(SysStorageConfigBo storageConfigBo);

    /**
     * 批量删除对象存储配置
     *
     * @param storageConfigIds     需要删除的配置ID集合
     * @param isValid 是否执行删除前校验
     */
    void deleteWithValidByIds(Collection<Long> storageConfigIds, Boolean isValid);

    /**
     * 修改主对象存储配置
     *
     * <p>通常用于设置当前启用的默认对象存储配置。</p>
     *
     * @param storageConfigBo 对象存储配置业务对象
     */
    void updateStorageConfigMaster(SysStorageConfigBo storageConfigBo);

    /**
     * 获取当前主对象存储客户端
     *
     * @return 主对象存储客户端
     */
    StorageClient getStorageConfigMaster();

    /**
     * 根据配置ID获取对应的对象存储客户端
     *
     * @param id 对象存储配置ID
     * @return 对应的对象存储客户端
     */
    StorageClient getStorageConfigClient(Long id);

    /**
     * 测试对象存储配置是否可用
     *
     * @param id 对象存储配置ID
     * @return 测试结果信息
     * @throws Exception 测试过程中可能抛出的异常
     */
    String testStorageConfig(Long id) throws Exception;
}


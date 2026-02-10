package com.han.system.service;

import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.oss.core.OssClient;
import com.han.system.domain.bo.SysOssConfigBo;
import com.han.system.domain.vo.SysOssConfigVo;

import java.util.Collection;

/**
 * @Author Lion Li, 孤舟烟雨
 * @CreateTime: 2021-08-13
 * @Description: 对象存储配置Service接口
 */
public interface ISysOssConfigService {

    /**
     * 初始化OSS配置
     */
    void init();

    /**
     * 查询单个
     */
    SysOssConfigVo queryById(Long ossConfigId);

    /**
     * 查询列表
     */
    TableDataInfo<SysOssConfigVo> queryPageList(SysOssConfigBo bo, PageQuery pageQuery);

    /**
     * 根据新增业务对象插入对象存储配置
     *
     * @param ossConfigBo 对象存储配置新增业务对象
     */
    void insertOssConfig(SysOssConfigBo ossConfigBo);

    /**
     * 根据编辑业务对象修改对象存储配置
     *
     * @param bo 对象存储配置编辑业务对象
     */
    void updateOssConfig(SysOssConfigBo bo);

    /**
     * 校验并删除数据
     *
     * @param ids     主键集合
     * @param isValid 是否校验,true-删除前校验,false-不校验
     */
    void deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 设置主配置
     */
    int updateOssConfigMaster(SysOssConfigBo ossConfigBo);

    /**
     * 获得 Master 文件客户端
     *
     * @return 文件客户端
     */
    OssClient getMasterOssClient();

    /**
     * 获得指定编号的文件客户端
     *
     * @param id 配置编号
     * @return 文件客户端
     */
    OssClient getOssClient(Long id);
}

package com.han.system.service;

import com.han.system.domain.bo.SysSocialBo;
import com.han.system.domain.vo.SysSocialVo;

import java.util.List;

/**
 * @Author thiszhc
 * @CreateTime: 2026-01-23
 * @Description: 社会化关系Service接口
 */
public interface ISysSocialService {


    /**
     * 查询社会化关系
     */
    SysSocialVo queryById(String id);

    /**
     * 查询社会化关系列表
     */
    List<SysSocialVo> queryList(SysSocialBo bo);

    /**
     * 查询社会化关系列表
     */
    List<SysSocialVo> queryListByUserId(Long userId);

    /**
     * 新增授权关系
     */
    Boolean insertByBo(SysSocialBo bo);

    /**
     * 更新社会化关系
     */
    Boolean updateByBo(SysSocialBo bo);

    /**
     * 删除社会化关系信息
     */
    Boolean deleteWithValidById(Long id);


    /**
     * 根据 authId 查询
     */
    List<SysSocialVo> selectByAuthId(String authId);


}

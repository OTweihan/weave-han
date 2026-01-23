package com.han.system.service;

import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.system.domain.SysUserOnline;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 在线用户监控 服务层
 */
public interface ISysUserOnlineService {

    /**
     * 查询在线用户列表
     *
     * @param ipaddr   IP地址
     * @param userName 用户名
     * @return 在线用户列表
     */
    TableDataInfo<SysUserOnline> selectUserOnlineList(String ipaddr, String userName);

    /**
     * 强退用户
     *
     * @param tokenId token值
     */
    void forceLogout(String tokenId);

    /**
     * 查询当前登录用户在线列表
     *
     * @return 在线用户列表
     */
    TableDataInfo<SysUserOnline> selectUserOnlineListByLoginUser();

    /**
     * 强退当前登录用户指定设备
     *
     * @param tokenId token值
     */
    void removeUserOnline(String tokenId);
}

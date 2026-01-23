package com.han.system.service;

import java.util.Set;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 用户权限处理
 */
public interface ISysPermissionService {

    /**
     * 获取角色数据权限
     *
     * @param userId  用户id
     * @return 角色权限信息
     */
    Set<String> getRolePermission(Long userId);

    /**
     * 获取菜单数据权限
     *
     * @param userId  用户id
     * @return 菜单权限信息
     */
    Set<String> getMenuPermission(Long userId);

}

package com.han.common.satoken.core.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.han.common.core.domain.model.LoginUser;
import com.han.common.core.enums.UserType;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.service.PermissionService;
import com.han.common.core.utils.SpringUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.satoken.utils.LoginHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: Sa-Token 权限管理实现类
 */
public class SaPermissionImpl implements StpInterface {

    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (ObjectUtil.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
            PermissionService permissionService = getPermissionService();
            if (ObjectUtil.isNotNull(permissionService)) {
                List<String> list = StringUtils.splitList(loginId.toString(), ":");
                if (list.size() > 1) {
                    return new ArrayList<>(permissionService.getMenuPermission(Long.parseLong(list.get(1))));
                }
                return new ArrayList<>();
            } else {
                throw new ServiceException("PermissionService 实现类不存在");
            }
        }
        UserType userType = UserType.getUserType(loginUser.getUserType());
        if (userType == UserType.APP_USER) {
            // 其他端 自行根据业务编写
        }
        if (CollUtil.isNotEmpty(loginUser.getMenuPermission())) {
            // SYS_USER 默认返回权限
            return new ArrayList<>(loginUser.getMenuPermission());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (ObjectUtil.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
            PermissionService permissionService = getPermissionService();
            if (ObjectUtil.isNotNull(permissionService)) {
                List<String> list = StringUtils.splitList(loginId.toString(), ":");
                if (list.size() > 1) {
                    return new ArrayList<>(permissionService.getRolePermission(Long.parseLong(list.get(1))));
                }
                return new ArrayList<>();
            } else {
                throw new ServiceException("PermissionService 实现类不存在");
            }
        }
        UserType userType = UserType.getUserType(loginUser.getUserType());
        if (userType == UserType.APP_USER) {
            // 其他端 自行根据业务编写
        }
        if (CollUtil.isNotEmpty(loginUser.getRolePermission())) {
            // SYS_USER 默认返回权限
            return new ArrayList<>(loginUser.getRolePermission());
        } else {
            return new ArrayList<>();
        }
    }

    private PermissionService permissionService;

    private PermissionService getPermissionService() {
        if (permissionService == null) {
            try {
                permissionService = SpringUtils.getBean(PermissionService.class);
            } catch (Exception e) {
                return null;
            }
        }
        return permissionService;
    }
}

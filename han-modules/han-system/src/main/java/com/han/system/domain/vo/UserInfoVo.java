package com.han.system.domain.vo;

import lombok.Data;

import java.util.Set;

/**
 * @Author Michelle.Chung
 * @CreateTime: 2026-01-23
 * @Description: 登录用户信息
 */
@Data
public class UserInfoVo {

    /**
     * 用户基本信息
     */
    private SysUserVo user;

    /**
     * 菜单权限
     */
    private Set<String> permissions;

    /**
     * 角色权限
     */
    private Set<String> roles;
}

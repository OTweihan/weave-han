package com.han.system.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author Michelle.Chung
 * @CreateTime: 2026-01-23
 * @Description: 用户信息
 */
@Data
public class SysUserInfoVo {

    /**
     * 用户信息
     */
    private SysUserVo user;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;

    /**
     * 角色列表
     */
    private List<SysRoleVo> roles;
}

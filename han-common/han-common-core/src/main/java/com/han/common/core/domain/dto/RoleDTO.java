package com.han.common.core.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 角色 DTO
 */
@Data
@NoArgsConstructor
public class RoleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限
     */
    private String roleKey;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限 6：部门及以下或本人数据权限）
     */
    private String dataScope;
}

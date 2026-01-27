package com.han.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.han.common.core.constant.SystemConstants;
import com.han.common.core.xss.Xss;
import com.han.common.mybatis.core.domain.BaseEntity;
import com.han.system.domain.SysUser;

/**
 * @Author Michelle.Chung
 * @CreateTime: 2026-01-23
 * @Description: 用户信息业务对象 sys_user
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysUser.class, reverseConvertGenerate = false)
public class SysUserBo extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户账号
     */
    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过{max}个字符")
    private String userAccount;

    /**
     * 用户昵称
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称长度不能超过{max}个字符")
    private String nickName;

    /**
     * 用户类型（sys_user系统用户）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 角色组
     */
    @Size(min = 1, message = "用户角色不能为空")
    private Long[] roleIds;

    /**
     * 数据权限 当前角色ID
     */
    private Long roleId;

    /**
     * 用户ID
     */
    private String userIds;

    public SysUserBo(Long userId) {
        this.userId = userId;
    }

    public boolean isSuperAdmin() {
        return SystemConstants.SUPER_ADMIN_ID.equals(this.userId);
    }
}

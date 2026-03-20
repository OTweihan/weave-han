package com.han.system.controller.system;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.han.common.core.domain.R;
import com.han.common.encrypt.annotation.ApiEncrypt;
import com.han.common.idempotent.annotation.RepeatSubmit;
import com.han.common.log.annotation.Log;
import com.han.common.log.enums.BusinessType;
import com.han.common.satoken.utils.LoginHelper;
import com.han.common.web.core.BaseController;
import com.han.system.domain.bo.SysUserPasswordBo;
import com.han.system.domain.bo.SysUserProfileBo;
import com.han.system.domain.vo.ProfileUserVo;
import com.han.system.domain.vo.SysUserVo;
import com.han.system.service.ISysUserService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 个人信息 业务处理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    private final ISysUserService userService;

    /**
     * 个人信息
     */
    @GetMapping
    public R<ProfileVo> profile() {
        SysUserVo user = userService.selectUserById(LoginHelper.getUserId());
        String roleGroup = userService.selectUserRoleGroup(user.getUserId());
        // 单独做一个vo专门给个人中心用 避免数据被脱敏
        ProfileUserVo profileUser = BeanUtil.toBean(user, ProfileUserVo.class);
        ProfileVo profileVo = new ProfileVo(profileUser, roleGroup);
        return R.ok(profileVo);
    }

    /**
     * 修改用户信息
     */
    @RepeatSubmit
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateProfile(@Validated @RequestBody SysUserProfileBo profile) {
        profile.setUserId(LoginHelper.getUserId());
        userService.updateUserProfile(profile);
        return R.ok();
    }

    /**
     * 重置密码
     *
     * @param bo 新旧密码
     */
    @RepeatSubmit
    @ApiEncrypt
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public R<Void> updatePwd(@Validated @RequestBody SysUserPasswordBo bo) {
        SysUserVo user = userService.selectUserById(LoginHelper.getUserId());
        String password = user.getPassword();
        if (!BCrypt.checkpw(bo.getOldPassword(), password)) {
            return R.fail("修改密码失败，旧密码错误");
        }
        if (BCrypt.checkpw(bo.getNewPassword(), password)) {
            return R.fail("新密码不能与旧密码相同");
        }
        userService.resetUserPwd(LoginHelper.getUserId(), bo.getNewPassword());
        return R.ok();
    }

    /**
     * 头像上传
     *
     * @param avatarFile 用户头像
     */
    @RepeatSubmit
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<AvatarVo> avatar(@RequestParam(required = false) MultipartFile avatarFile) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            return R.fail("头像文件不能为空");
        }
        String avatar = userService.updateUserAvatar(LoginHelper.getUserId(), avatarFile);
        return R.ok(new AvatarVo(avatar));
    }

    /**
     * 用户头像信息
     *
     * @param imgUrl 头像地址
     */
    public record AvatarVo(String imgUrl) {
    }

    /**
     * 用户个人信息
     *
     * @param user      用户信息
     * @param roleGroup 用户所属角色组
     */
    public record ProfileVo(ProfileUserVo user, String roleGroup) {
    }
}

package com.han.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import com.han.common.core.domain.R;
import com.han.common.idempotent.annotation.RepeatSubmit;
import com.han.common.log.annotation.Log;
import com.han.common.log.enums.BusinessType;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.web.core.BaseController;
import com.han.system.domain.SysUserOnline;
import com.han.system.service.ISysUserOnlineService;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 在线用户监控
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {

    private final ISysUserOnlineService userOnlineService;

    /**
     * 获取在线用户监控列表
     *
     * @param ipaddr   IP地址
     * @param userName 用户名
     */
    @SaCheckPermission("monitor:online:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserOnline> list(String ipaddr, String userName) {
        return userOnlineService.selectUserOnlineList(ipaddr, userName);
    }

    /**
     * 强退用户
     *
     * @param tokenId token值
     */
    @SaCheckPermission("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @RepeatSubmit()
    @DeleteMapping("/{tokenId}")
    public R<Void> forceLogout(@PathVariable String tokenId) {
        userOnlineService.forceLogout(tokenId);
        return R.ok();
    }

    /**
     * 获取当前用户登录在线设备
     */
    @GetMapping()
    public TableDataInfo<SysUserOnline> getInfo() {
        return userOnlineService.selectUserOnlineListByLoginUser();
    }

    /**
     * 强退当前在线设备
     *
     * @param tokenId token值
     */
    @Log(title = "在线设备", businessType = BusinessType.FORCE)
    @RepeatSubmit()
    @DeleteMapping("/myself/{tokenId}")
    public R<Void> remove(@PathVariable String tokenId) {
        userOnlineService.removeUserOnline(tokenId);
        return R.ok();
    }
}

package com.han.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.common.core.domain.R;
import com.han.system.domain.vo.CacheListInfoVo;
import com.han.system.service.ISysCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 缓存监控
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/cache")
public class CacheController {

    private final ISysCacheService cacheService;

    /**
     * 获取缓存监控列表
     */
    @SaCheckPermission("monitor:cache:list")
    @GetMapping()
    public R<CacheListInfoVo> getInfo() throws Exception {
        return R.ok(cacheService.getCacheInfo());
    }
}

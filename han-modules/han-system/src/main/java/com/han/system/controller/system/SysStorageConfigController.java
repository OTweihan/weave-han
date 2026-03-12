package com.han.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import com.han.common.core.domain.R;
import com.han.common.core.validate.AddGroup;
import com.han.common.core.validate.EditGroup;
import com.han.common.core.validate.QueryGroup;
import com.han.common.idempotent.annotation.RepeatSubmit;
import com.han.common.log.annotation.Log;
import com.han.common.log.enums.BusinessType;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.web.core.BaseController;
import com.han.system.domain.bo.SysStorageConfigBo;
import com.han.system.domain.vo.SysStorageConfigVo;
import com.han.system.service.ISysStorageConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 对象存储配置
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/resource/storage/config")
public class SysStorageConfigController extends BaseController {

    private final ISysStorageConfigService storageConfigService;

    /**
     * 查询对象存储配置列表
     */
    @SaCheckPermission("system:storageConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SysStorageConfigVo> list(@Validated(QueryGroup.class) SysStorageConfigBo storageConfigBo, PageQuery pageQuery) {
        return storageConfigService.queryPageList(storageConfigBo, pageQuery);
    }

    /**
     * 获取对象存储配置详细信息
     *
     * @param storageConfigId 存储配置ID
     */
    @SaCheckPermission("system:storageConfig:list")
    @GetMapping("/{storageConfigId}")
    public R<SysStorageConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                         @PathVariable Long storageConfigId) {
        return R.ok(storageConfigService.queryById(storageConfigId));
    }

    /**
     * 新增对象存储配置
     */
    @SaCheckPermission("system:storageConfig:add")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysStorageConfigBo storageConfigBo) {
        storageConfigService.insertStorageConfig(storageConfigBo);
        return R.ok();
    }

    /**
     * 修改对象存储配置
     */
    @SaCheckPermission("system:storageConfig:edit")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysStorageConfigBo storageConfigBo) {
        storageConfigService.updateStorageConfig(storageConfigBo);
        return R.ok();
    }

    /**
     * 删除对象存储配置
     *
     * @param storageConfigIds 存储配置ID串
     */
    @SaCheckPermission("system:storageConfig:remove")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{storageConfigIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] storageConfigIds) {
        storageConfigService.deleteWithValidByIds(List.of(storageConfigIds), true);
        return R.ok();
    }

    /**
     * 修改对象存储状态
     */
    @SaCheckPermission("system:storageConfig:edit")
    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/updateMaster")
    public R<Void> updateMaster(@RequestBody SysStorageConfigBo storageConfigBo) {
        storageConfigService.updateStorageConfigMaster(storageConfigBo);
        return R.ok();
    }

    /**
     * 测试对象存储配置是否正确
     *
     * @param storageConfigId 存储配置ID
     */
    @SaCheckPermission("system:storageConfig:list")
    @GetMapping("/test")
    public R<String> test(@RequestParam("storageConfigId") Long storageConfigId) throws Exception {
        return R.ok(storageConfigService.testStorageConfig(storageConfigId));
    }
}

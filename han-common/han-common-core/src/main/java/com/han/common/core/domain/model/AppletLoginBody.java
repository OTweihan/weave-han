package com.han.common.core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 小程序登录对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppletLoginBody extends LoginBody {

    /**
     * 小程序id(多个小程序时使用)
     */
    private String appletId;

    /**
     * 小程序code
     */
    @NotBlank(message = "{applet.code.not.blank}")
    private String appletCode;
}

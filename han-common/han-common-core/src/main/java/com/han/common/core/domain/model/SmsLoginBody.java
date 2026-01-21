package com.han.common.core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-20
 * @Description: 短信登录对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmsLoginBody extends LoginBody {

    /**
     * 手机号
     */
    @NotBlank(message = "{user.phonenumber.not.blank}")
    private String phonenumber;

    /**
     * 短信code
     */
    @NotBlank(message = "{sms.code.not.blank}")
    private String smsCode;
}

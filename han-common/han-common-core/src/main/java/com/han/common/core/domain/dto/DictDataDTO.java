package com.han.common.core.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: AprilWind
 * @CreateTime: 2026-01-20
 * @Description: 字典数据 DTO
 */
@Data
@NoArgsConstructor
public class DictDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 是否默认（Y是 N否）
     */
    private String isDefault;

    /**
     * 备注
     */
    private String remark;
}

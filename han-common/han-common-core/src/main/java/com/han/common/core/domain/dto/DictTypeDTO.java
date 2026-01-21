package com.han.common.core.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: AprilWind
 * @CreateTime: 2026-01-20
 * @Description: 字典类型 DTO
 */
@Data
@NoArgsConstructor
public class DictTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    private Long dictId;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 备注
     */
    private String remark;
}

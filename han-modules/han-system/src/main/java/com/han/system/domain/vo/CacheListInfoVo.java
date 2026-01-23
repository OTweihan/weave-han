package com.han.system.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 缓存监控列表信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheListInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 信息
     */
    private Properties info;

    /**
     * 数据库
     */
    private Long dbSize;

    /**
     * 命令统计
     */
    private List<Map<String, String>> commandStats;
}

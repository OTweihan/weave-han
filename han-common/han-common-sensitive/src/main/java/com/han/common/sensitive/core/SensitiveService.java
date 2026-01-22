package com.han.common.sensitive.core;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 脱敏服务
 */
public interface SensitiveService {

    /**
     * 是否脱敏
     */
    boolean isSensitive(String[] roleKey, String[] perms);
}

package com.han.common.storage.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.han.common.core.utils.StringUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.storage.core.StorageClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;

/**
 * @Author WeiHan
 * @CreateTime: 2026-02-04
 * @Description: StorageClientConfig 类型处理器
 * 用于处理 StorageClientConfig 接口及其子类的 JSON 序列化与反序列化
 */
@Slf4j
@MappedTypes({StorageClientConfig.class})
public class StorageClientConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

    public StorageClientConfigTypeHandler(Class<?> type) {
        super(type);
    }

    public StorageClientConfigTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Object parse(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        // 尝试直接反序列化（依赖 @JsonTypeInfo 自动推断类型）
        try {
            StorageClientConfig config = JsonUtils.parseObject(json, StorageClientConfig.class);
            if (config != null) {
                return config;
            }
        } catch (Exception e) {
            log.error("StorageClientConfig 配置解析失败，数据可能损坏或缺失类型信息: {}", json, e);
            throw new IllegalArgumentException("StorageClientConfig 配置解析失败，请检查配置格式或类型信息", e);
        }

        log.error("StorageClientConfig 配置为空或无效: {}", json);
        throw new IllegalArgumentException("StorageClientConfig 配置无效");
    }

    @Override
    public String toJson(Object obj) {
        // 序列化时，由于 StorageClientConfig 有 @JsonTypeInfo，会自动带上 @class
        return JsonUtils.toJsonString(obj);
    }
}

package com.han.common.oss.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.han.common.core.utils.StringUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.oss.core.OssClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;

/**
 * @Author WeiHan
 * @CreateTime: 2026-02-04
 * @Description: OssClientConfig 类型处理器
 * 用于处理 OssClientConfig 接口及其子类的 JSON 序列化与反序列化
 */
@Slf4j
@MappedTypes({OssClientConfig.class})
public class OssClientConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

    public OssClientConfigTypeHandler(Class<?> type) {
        super(type);
    }

    public OssClientConfigTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public Object parse(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        // 尝试直接反序列化（依赖 @JsonTypeInfo 自动推断类型）
        try {
            OssClientConfig config = JsonUtils.parseObject(json, OssClientConfig.class);
            if (config != null) {
                return config;
            }
        } catch (Exception e) {
            log.error("OssClientConfig 配置解析失败，数据可能损坏或缺失类型信息: {}", json, e);
            throw new IllegalArgumentException("OSS配置解析失败，请检查配置格式或类型信息", e);
        }
        
        log.error("OssClientConfig 配置为空或无效: {}", json);
        throw new IllegalArgumentException("OSS配置无效");
    }

    @Override
    public String toJson(Object obj) {
        // 序列化时，由于 OssClientConfig 有 @JsonTypeInfo，会自动带上 @class
        return JsonUtils.toJsonString(obj);
    }
}

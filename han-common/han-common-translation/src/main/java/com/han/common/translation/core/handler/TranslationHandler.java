package com.han.common.translation.core.handler;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.reflect.ReflectUtils;
import com.han.common.translation.annotation.Translation;
import com.han.common.translation.core.TranslationInterface;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-22
 * @Description: 翻译处理器
 */
@Slf4j
public class TranslationHandler extends JsonSerializer<Object> implements ContextualSerializer {

    /**
     * 全局翻译实现类映射器
     */
    public static final Map<String, TranslationInterface<?>> TRANSLATION_MAPPER = new ConcurrentHashMap<>();

    private final Translation translation;
    private final TranslationInterface<?> translationInterface;

    public TranslationHandler() {
        this.translation = null;
        this.translationInterface = null;
    }

    public TranslationHandler(Translation translation, TranslationInterface<?> translationInterface) {
        this.translation = translation;
        this.translationInterface = translationInterface;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (ObjectUtil.isNull(translation) || ObjectUtil.isNull(translationInterface)) {
            gen.writeObject(value);
            return;
        }
        // 如果映射字段不为空 则取映射字段的值
        if (StringUtils.isNotBlank(translation.mapper())) {
            value = ReflectUtils.invokeGetter(gen.currentValue(), translation.mapper());
        }
        // 如果为 null 直接写出
        if (ObjectUtil.isNull(value)) {
            gen.writeNull();
            return;
        }
        try {
            Object result = translationInterface.translation(value, translation.other());
            gen.writeObject(result);
        } catch (Exception e) {
            log.error("翻译处理异常，type: {}, value: {}", translation.type(), value, e);
            // 出现异常时输出原始值而不是中断序列化
            gen.writeObject(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Translation translation = property.getAnnotation(Translation.class);
        if (Objects.nonNull(translation)) {
            TranslationInterface<?> trans = TRANSLATION_MAPPER.get(translation.type());
            if (ObjectUtil.isNull(trans)) {
                log.warn("Translation type not found: {}", translation.type());
                return this;
            }
            return new TranslationHandler(translation, trans);
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}

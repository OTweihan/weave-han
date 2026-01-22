package com.han.common.encrypt.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.han.common.encrypt.enumd.AlgorithmType;
import com.han.common.encrypt.enumd.EncodeType;
import com.han.common.encrypt.properties.EncryptorProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import com.han.common.core.constant.Constants;
import com.han.common.core.utils.ObjectUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.encrypt.annotation.EncryptField;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: 老马
 * @CreateTime: 2026-01-22
 * @Description: 加密管理类
 */
@Slf4j
public class EncryptorManager {

    /**
     * 缓存加密器
     */
    Map<Integer, IEncryptor> encryptorMap = new ConcurrentHashMap<>();

    /**
     * 类加密字段缓存
     */
    Map<Class<?>, Set<Field>> fieldCache = new ConcurrentHashMap<>();

    /**
     * 字段加密上下文缓存
     */
    Map<Field, EncryptContext> fieldEncryptContextCache = new ConcurrentHashMap<>();

    /**
     * 字段加密器缓存
     */
    Map<Field, IEncryptor> fieldEncryptorCache = new ConcurrentHashMap<>();

    private final EncryptorProperties defaultProperties;

    /**
     * 构造方法传入类加密字段缓存
     *
     * @param typeAliasesPackage 实体类包
     * @param defaultProperties  默认配置
     */
    public EncryptorManager(String typeAliasesPackage, EncryptorProperties defaultProperties) {
        this.defaultProperties = defaultProperties;
        scanEncryptClasses(typeAliasesPackage);
    }

    /**
     * 获取类加密字段缓存
     */
    public Set<Field> getFieldCache(Class<?> sourceClazz) {
        return ObjectUtils.notNullGetter(fieldCache, f -> f.get(sourceClazz));
    }

    /**
     * 注册加密执行者到缓存
     *
     * @param encryptContext 加密执行者需要的相关配置参数
     */
    public IEncryptor registAndGetEncryptor(EncryptContext encryptContext) {
        int key = encryptContext.hashCode();
        if (encryptorMap.containsKey(key)) {
            return encryptorMap.get(key);
        }
        IEncryptor encryptor = ReflectUtil.newInstance(encryptContext.getAlgorithm().getClazz(), encryptContext);
        encryptorMap.put(key, encryptor);
        return encryptor;
    }

    /**
     * 移除缓存中的加密执行者
     *
     * @param encryptContext 加密执行者需要的相关配置参数
     */
    public void removeEncryptor(EncryptContext encryptContext) {
        this.encryptorMap.remove(encryptContext.hashCode());
    }

    /**
     * 根据配置进行加密。会进行本地缓存对应的算法和对应的秘钥信息。
     *
     * @param value          待加密的值
     * @param encryptContext 加密相关的配置信息
     */
    public String encrypt(String value, EncryptContext encryptContext) {
        if (StringUtils.startsWith(value, Constants.ENCRYPT_HEADER)) {
            return value;
        }
        IEncryptor encryptor = this.registAndGetEncryptor(encryptContext);
        String encrypt = encryptor.encrypt(value, encryptContext.getEncode());
        return Constants.ENCRYPT_HEADER + encrypt;
    }

    /**
     * 根据字段进行加密
     *
     * @param value 待加密的值
     * @param field 待加密字段
     */
    public String encrypt(String value, Field field) {
        if (StringUtils.startsWith(value, Constants.ENCRYPT_HEADER)) {
            return value;
        }
        EncryptContext encryptContext = fieldEncryptContextCache.get(field);
        if (encryptContext == null) {
            return value;
        }
        IEncryptor encryptor = fieldEncryptorCache.get(field);
        if (encryptor == null) {
            encryptor = this.registAndGetEncryptor(encryptContext);
            fieldEncryptorCache.put(field, encryptor);
        }
        String encrypt = encryptor.encrypt(value, encryptContext.getEncode());
        return Constants.ENCRYPT_HEADER + encrypt;
    }

    /**
     * 根据配置进行解密
     *
     * @param value          待解密的值
     * @param encryptContext 加密相关的配置信息
     */
    public String decrypt(String value, EncryptContext encryptContext) {
        if (!StringUtils.startsWith(value, Constants.ENCRYPT_HEADER)) {
            return value;
        }
        try {
            IEncryptor encryptor = this.registAndGetEncryptor(encryptContext);
            String str = StringUtils.removeStart(value, Constants.ENCRYPT_HEADER);
            return encryptor.decrypt(str);
        } catch (Exception e) {
            log.error("解密失败: algorithm={}, encodeType={}, encryptedValue={}",
                encryptContext.getAlgorithm(),
                encryptContext.getEncode(),
                value.length() > 100 ? value.substring(0, 100) + "..." : value,
                e);
            throw e;
        }
    }

    /**
     * 根据字段进行解密
     *
     * @param value 待解密的值
     * @param field 待解密字段
     */
    public String decrypt(String value, Field field) {
        if (StringUtils.startsWith(value, Constants.ENCRYPT_HEADER)) {
            value = value.substring(Constants.ENCRYPT_HEADER.length());
        }
        EncryptContext encryptContext = fieldEncryptContextCache.get(field);
        if (encryptContext == null) {
            return value;
        }
        IEncryptor encryptor = fieldEncryptorCache.get(field);
        if (encryptor == null) {
            encryptor = this.registAndGetEncryptor(encryptContext);
            fieldEncryptorCache.put(field, encryptor);
        }
        return encryptor.decrypt(value);
    }

    /**
     * 对对象进行加密
     *
     * @param sourceObject 待加密对象
     */
    public void encryptObject(Object sourceObject) {
        handleObject(sourceObject, true);
    }

    /**
     * 对对象进行解密
     *
     * @param sourceObject 待解密对象
     */
    public void decryptObject(Object sourceObject) {
        handleObject(sourceObject, false);
    }

    private void handleObject(Object sourceObject, boolean isEncrypt) {
        if (ObjectUtil.isNull(sourceObject)) {
            return;
        }
        if (sourceObject instanceof Map<?, ?> map) {
            new HashSet<>(map.values()).forEach(item -> handleObject(item, isEncrypt));
            return;
        }
        if (sourceObject instanceof List<?> list) {
            if (CollUtil.isEmpty(list)) {
                return;
            }
            // 判断第一个元素是否含有注解。如果没有直接返回，提高效率
            Object firstItem = list.getFirst();
            if (ObjectUtil.isNull(firstItem) || CollUtil.isEmpty(getFieldCache(firstItem.getClass()))) {
                return;
            }
            list.forEach(item -> handleObject(item, isEncrypt));
            return;
        }

        // 不在缓存中的类,就是没有加密注解的类
        Set<Field> fields = getFieldCache(sourceObject.getClass());
        if (ObjectUtil.isNull(fields)) {
            return;
        }
        try {
            for (Field field : fields) {
                String value = Convert.toStr(field.get(sourceObject));
                if (value != null) {
                    if (isEncrypt) {
                        field.set(sourceObject, this.encrypt(value, field));
                    } else {
                        try {
                            field.set(sourceObject, this.decrypt(value, field));
                        } catch (Exception e) {
                            log.error("解密字段失败: class={}, field={}, value={}",
                                field.getDeclaringClass().getName(),
                                field.getName(),
                                value.length() > 50 ? value.substring(0, 50) + "..." : value,
                                e);
                            // 解密失败时保持原值
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("处理{}字段时出错", isEncrypt ? "加密" : "解密", e);
        }
    }

    /**
     * 通过 typeAliasesPackage 设置的扫描包 扫描缓存实体
     */
    private void scanEncryptClasses(String typeAliasesPackage) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
        String[] packagePatternArray = StringUtils.splitPreserveAllTokens(typeAliasesPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        String classpath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;
        try {
            for (String packagePattern : packagePatternArray) {
                String path = ClassUtils.convertClassNameToResourcePath(packagePattern);
                // 使用 /**/*.class 进行递归扫描
                Resource[] resources = resolver.getResources(classpath + path + "/**/*.class");
                for (Resource resource : resources) {
                    ClassMetadata classMetadata = factory.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Resources.classForName(classMetadata.getClassName());
                    Set<Field> encryptFieldSet = getEncryptFieldSetFromClazz(clazz);
                    if (CollUtil.isNotEmpty(encryptFieldSet)) {
                        fieldCache.put(clazz, encryptFieldSet);
                        // 预加载字段的 EncryptContext 和 Encryptor
                        for (Field field : encryptFieldSet) {
                            EncryptField encryptField = field.getAnnotation(EncryptField.class);
                            EncryptContext encryptContext = new EncryptContext();
                            encryptContext.setAlgorithm(encryptField.algorithm() == AlgorithmType.DEFAULT ? defaultProperties.getAlgorithm() : encryptField.algorithm());
                            encryptContext.setEncode(encryptField.encode() == EncodeType.DEFAULT ? defaultProperties.getEncode() : encryptField.encode());
                            encryptContext.setPassword(StringUtils.isBlank(encryptField.password()) ? defaultProperties.getPassword() : encryptField.password());
                            encryptContext.setPrivateKey(StringUtils.isBlank(encryptField.privateKey()) ? defaultProperties.getPrivateKey() : encryptField.privateKey());
                            encryptContext.setPublicKey(StringUtils.isBlank(encryptField.publicKey()) ? defaultProperties.getPublicKey() : encryptField.publicKey());

                            fieldEncryptContextCache.put(field, encryptContext);
                            fieldEncryptorCache.put(field, registAndGetEncryptor(encryptContext));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("初始化数据安全缓存时出错:{}", e.getMessage());
        }
    }

    /**
     * 获得一个类的加密字段集合
     */
    private Set<Field> getEncryptFieldSetFromClazz(Class<?> clazz) {
        Set<Field> fieldSet = new HashSet<>();
        // 判断clazz如果是接口,内部类,匿名类就直接返回
        if (clazz.isInterface() || clazz.isMemberClass() || clazz.isAnonymousClass()) {
            return fieldSet;
        }
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            fieldSet.addAll(Arrays.asList(fields));
            clazz = clazz.getSuperclass();
        }
        fieldSet = fieldSet.stream().filter(field ->
                field.isAnnotationPresent(EncryptField.class) && field.getType() == String.class)
            .collect(Collectors.toSet());
        for (Field field : fieldSet) {
            field.setAccessible(true);
        }
        return fieldSet;
    }
}

package com.han.common.sms.core.dao;

import com.han.common.core.constant.GlobalConstants;
import com.han.common.redis.utils.RedisUtils;
import org.dromara.sms4j.api.dao.SmsDao;

import java.time.Duration;

/**
 * @Author: Feng
 * @CreateTime: 2026-01-22
 * @Description: SmsDao 缓存配置 (使用框架自带RedisUtils实现 协议统一)
 * <p>主要用于短信重试和拦截的缓存</p>
 */
public class PlusSmsDao implements SmsDao {

    private static final String SMS_REDIS_PREFIX = GlobalConstants.GLOBAL_REDIS_KEY + "sms:";

    /**
     * 存储
     *
     * @param key       键
     * @param value     值
     * @param cacheTime 缓存时间（单位：秒)
     */
    @Override
    public void set(String key, Object value, long cacheTime) {
        RedisUtils.setCacheObject(SMS_REDIS_PREFIX + key, value, Duration.ofSeconds(cacheTime));
    }

    /**
     * 存储
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public void set(String key, Object value) {
        RedisUtils.setCacheObject(SMS_REDIS_PREFIX + key, value, true);
    }

    /**
     * 读取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(String key) {
        return RedisUtils.getCacheObject(SMS_REDIS_PREFIX + key);
    }

    /**
     * remove
     * 根据key移除缓存
     *
     * @param key 缓存键
     * @return 被删除的 value
     */
    @Override
    public Object remove(String key) {
        String redisKey = SMS_REDIS_PREFIX + key;
        Object value = RedisUtils.getCacheObject(redisKey);
        RedisUtils.deleteObject(redisKey);
        return value;
    }

    /**
     * 清空
     */
    @Override
    public void clean() {
        RedisUtils.deleteKeys(SMS_REDIS_PREFIX + "*");
    }
}

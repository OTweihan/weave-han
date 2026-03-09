package com.han.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.oss.factory.OssClientFactory;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.han.common.oss.core.OssClient;
import com.han.common.oss.core.OssClientConfig;
import com.han.common.oss.enums.OssStorageTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.han.common.core.constant.CacheNames;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.oss.constant.OssConstant;
import com.han.common.redis.utils.CacheUtils;
import com.han.common.redis.utils.RedisUtils;

import com.han.system.domain.SysOssConfig;
import com.han.system.domain.bo.SysOssConfigBo;
import com.han.system.domain.vo.SysOssConfigVo;
import com.han.system.mapper.SysOssConfigMapper;
import com.han.system.service.ISysOssConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author Lion Li, 孤舟烟雨
 * @CreateTime: 2021-08-13
 * @Description: 对象存储配置Service业务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysOssConfigServiceImpl implements ISysOssConfigService {

    private final SysOssConfigMapper baseMapper;
    private final OssClientFactory ossClientFactory;

    /**
     * Master 配置 ID
     */
    private static final Long CACHE_MASTER_ID = 0L;

    private LoadingCache<Long, OssClient> clientCache;

    /**
     * 项目启动时，初始化参数到缓存，加载配置类
     */
    @Override
    @PostConstruct
    public void init() {
        clientCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .maximumSize(100)
            .build(key -> {
                SysOssConfig config;
                if (CACHE_MASTER_ID.equals(key)) {
                    config = baseMapper.selectOne(new LambdaQueryWrapper<SysOssConfig>()
                        .eq(SysOssConfig::isMaster, true));
                } else {
                    config = baseMapper.selectById(key);
                }
                if (config != null) {
                    ossClientFactory.createOrUpdateFileClient(config.getOssConfigId(), config.getStorageType(), config.getConfigData());
                    return ossClientFactory.getFileClient(config.getOssConfigId());
                }
                return null;
            });

        List<SysOssConfig> list = baseMapper.selectList();
        // 加载OSS初始化配置
        for (SysOssConfig config : list) {
            String configName = config.getConfigName();
            if (config.isMaster()) {
                RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, configName);
            }
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigName(), JsonUtils.toJsonString(config.getConfigData()));
        }
    }

    @Override
    public SysOssConfigVo queryById(Long ossConfigId) {
        return baseMapper.selectVoById(ossConfigId);
    }

    @Override
    public TableDataInfo<SysOssConfigVo> queryPageList(SysOssConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOssConfig> lqw = buildQueryWrapper(bo);
        Page<SysOssConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public void insertOssConfig(SysOssConfigBo ossConfigBo) {
        checkConfigNameUnique(ossConfigBo);
        // 校验配置数据
        OssClientConfig clientConfig = parseClientConfig(ossConfigBo.getStorageType(), ossConfigBo.getConfigData());
        SysOssConfig ossConfig = MapstructUtils.convert(ossConfigBo, SysOssConfig.class);
        if (ossConfig == null) {
            throw new ServiceException("操作失败，转换对象为空");
        }
        // 使用解析后的强类型配置对象
        ossConfig.setConfigData(clientConfig);
        ossConfig.setMaster(false);

        boolean flag = baseMapper.insert(ossConfig) > 0;
        if (flag) {
            refreshCache(ossConfig.getOssConfigId());
            return;
        }
        throw new ServiceException("操作失败");
    }

    @Override
    public void updateOssConfig(SysOssConfigBo ossConfigBo) {
        // 校验配置是否存在
        SysOssConfig oldConfig = validateFileConfigExists(ossConfigBo.getOssConfigId());
        // 校验配置名是否唯一
        checkConfigNameUnique(ossConfigBo);

        // 校验配置数据
        OssClientConfig clientConfig = parseClientConfig(ossConfigBo.getStorageType(), ossConfigBo.getConfigData());
        SysOssConfig config = MapstructUtils.convert(ossConfigBo, SysOssConfig.class);
        if (config == null) {
            throw new ServiceException("操作失败，转换对象为空");
        }
        // 使用解析后的强类型配置对象
        config.setConfigData(clientConfig);
        // 保持原有的master状态，防止被重置
        config.setMaster(oldConfig.isMaster());

        boolean flag = baseMapper.updateById(config) > 0;
        if (flag) {
            refreshCache(config.getOssConfigId());
            // 清空缓存
            clearClientCache(config.getOssConfigId(), config.isMaster());
            return;
        }
        throw new ServiceException("操作失败");
    }

    /**
     * 校验并删除数据
     */
    @Override
    public void deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            if (CollUtil.containsAny(ids, OssConstant.SYSTEM_DATA_IDS)) {
                throw new ServiceException("系统内置, 不可删除!");
            }
        }
        List<SysOssConfig> list = CollUtil.newArrayList();
        for (Long configId : ids) {
            SysOssConfig config = baseMapper.selectById(configId);
            list.add(config);
        }
        boolean flag = baseMapper.deleteByIds(ids) > 0;
        if (flag) {
            list.forEach(sysOssConfig -> {
                CacheUtils.evict(CacheNames.SYS_OSS_CONFIG, sysOssConfig.getConfigName());
                // 清空缓存
                clearClientCache(sysOssConfig.getOssConfigId(), sysOssConfig.isMaster());
            });
            return;
        }
        throw new ServiceException("操作失败");
    }

    /**
     * 设置主配置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOssConfigMaster(SysOssConfigBo ossConfigBo) {
        SysOssConfig config = validateFileConfigExists(ossConfigBo.getOssConfigId());
        // 先全部设置为非主配置
        baseMapper.update(null, new LambdaUpdateWrapper<SysOssConfig>()
            .set(SysOssConfig::isMaster, false));
        // 设置当前为主配置
        int row = baseMapper.update(null, new LambdaUpdateWrapper<SysOssConfig>()
            .set(SysOssConfig::isMaster, true)
            .eq(SysOssConfig::getOssConfigId, config.getOssConfigId()));
        if (row > 0) {
            RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, config.getConfigName());
            // 清空缓存
            clearClientCache(null, true);
        }
        return row;
    }

    @Override
    public OssClient getMasterOssClient() {
        return clientCache.get(CACHE_MASTER_ID);
    }

    private void clearClientCache(Long id, Boolean master) {
        if (id != null) {
            clientCache.invalidate(id);
        }
        if (Boolean.TRUE.equals(master)) {
            clientCache.invalidate(CACHE_MASTER_ID);
        }
}

    @Override
    public OssClient getOssClient(Long id) {
        return clientCache.get(id);
    }

    @Override
    public String testOssConfig(Long id) throws Exception {
        validateFileConfigExists(id);
        byte[] content = ResourceUtil.readBytes("file/test.jpg");
        return getOssClient(id).upload(content, IdUtil.fastSimpleUUID() + ".jpg", "image/jpeg");
    }

    private OssClientConfig parseClientConfig(Integer storageType, Map<String, Object> configData) {
        // 获取配置类
        Class<? extends OssClientConfig> configClass = OssStorageTypeEnum.getByStorageType(storageType)
            .getConfigClass();
        OssClientConfig clientConfig = JsonUtils.parseObject2(JsonUtils.toJsonString(configData), configClass);
        // 参数校验
        ValidatorUtils.validate(clientConfig);
        // 设置参数
        return clientConfig;
    }

    private SysOssConfig validateFileConfigExists(Long id) {
        SysOssConfig config = baseMapper.selectById(id);
        if (config == null) {
            throw new ServiceException("文件配置不存在");
        }
        return config;
    }

    /**
     * 刷新缓存
     */
    private void refreshCache(Long id) {
        SysOssConfig config = baseMapper.selectById(id);
        CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigName(), JsonUtils.toJsonString(config.getConfigData()));
    }

    private void checkConfigNameUnique(SysOssConfigBo bo) {
        long count = baseMapper.selectCount(new LambdaQueryWrapper<SysOssConfig>()
            .eq(SysOssConfig::getConfigName, bo.getConfigName())
            .ne(bo.getOssConfigId() != null, SysOssConfig::getOssConfigId, bo.getOssConfigId()));
        if (count > 0) {
            throw new ServiceException("新增配置'" + bo.getConfigName() + "'失败，配置名称已存在");
        }
    }

    private LambdaQueryWrapper<SysOssConfig> buildQueryWrapper(SysOssConfigBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysOssConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getConfigName()), SysOssConfig::getConfigName, bo.getConfigName());
        lqw.eq(ObjectUtil.isNotNull(bo.getStorageType()), SysOssConfig::getStorageType, bo.getStorageType());
        lqw.eq(ObjectUtil.isNotNull(bo.getMaster()), SysOssConfig::isMaster, bo.getMaster());
        lqw.between(params.get("beginTime") != null && params.get("endTime") != null,
            SysOssConfig::getCreateTime, params.get("beginTime"), params.get("endTime"));
        lqw.orderByDesc(SysOssConfig::isMaster);
        lqw.orderByDesc(SysOssConfig::getCreateTime);
        return lqw;
    }
}

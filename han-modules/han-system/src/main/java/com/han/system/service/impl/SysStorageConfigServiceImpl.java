package com.han.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.storage.core.db.DbStorageClient;
import com.han.common.storage.factory.StorageClientFactory;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.han.common.storage.core.StorageClient;
import com.han.common.storage.core.StorageClientConfig;
import com.han.common.storage.enums.StorageTypeEnum;
import com.han.system.domain.SysFile;
import com.han.system.domain.SysStorageConfig;
import com.han.system.domain.vo.SysStorageConfigVo;
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
import com.han.common.storage.constant.StorageConstant;
import com.han.common.redis.utils.CacheUtils;
import com.han.common.redis.utils.RedisUtils;

import com.han.system.domain.bo.SysStorageConfigBo;
import com.han.system.mapper.SysStorageConfigMapper;
import com.han.system.mapper.SysFileMapper;
import com.han.system.service.ISysStorageConfigService;
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
public class SysStorageConfigServiceImpl implements ISysStorageConfigService {

    private final SysStorageConfigMapper storageConfigMapper;
    private final SysFileMapper fileMapper;
    private final StorageClientFactory storageClientFactory;

    /**
     * Master 配置 ID
     */
    private static final Long CACHE_MASTER_ID = 0L;

    private LoadingCache<Long, StorageClient> clientCache;

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
                SysStorageConfig config;
                if (CACHE_MASTER_ID.equals(key)) {
                    config = storageConfigMapper.selectOne(new LambdaQueryWrapper<SysStorageConfig>()
                        .eq(SysStorageConfig::isMaster, true));
                } else {
                    config = storageConfigMapper.selectById(key);
                }
                if (config != null) {
                    storageClientFactory.createOrUpdateFileClient(config.getStorageConfigId(), config.getStorageType(), config.getConfigData());
                    return storageClientFactory.getFileClient(config.getStorageConfigId());
                }
                return null;
            });

        List<SysStorageConfig> list = storageConfigMapper.selectList();
        // 加载存储配置
        for (SysStorageConfig config : list) {
            String configName = config.getConfigName();
            if (config.isMaster()) {
                RedisUtils.setCacheObject(StorageConstant.DEFAULT_CONFIG_KEY, configName);
            }
            CacheUtils.put(CacheNames.SYS_STORAGE_CONFIG, config.getConfigName(), JsonUtils.toJsonString(config.getConfigData(), StorageClientConfig.class));
        }
    }

    @Override
    public SysStorageConfigVo queryById(Long storageConfigId) {
        return storageConfigMapper.selectVoById(storageConfigId);
    }

    @Override
    public TableDataInfo<SysStorageConfigVo> queryPageList(SysStorageConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysStorageConfig> lqw = buildQueryWrapper(bo);
        Page<SysStorageConfigVo> result = storageConfigMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public void insertStorageConfig(SysStorageConfigBo storageConfigBo) {
        checkConfigNameUnique(storageConfigBo);
        // 校验配置数据
        StorageClientConfig clientConfig = parseClientConfig(storageConfigBo.getStorageType(), storageConfigBo.getConfigData());
        SysStorageConfig storageConfig = MapstructUtils.convert(storageConfigBo, SysStorageConfig.class);
        if (storageConfig == null) {
            throw new ServiceException("操作失败，转换对象为空");
        }
        // 使用解析后的强类型配置对象
        storageConfig.setConfigData(clientConfig);
        storageConfig.setMaster(false);

        boolean flag = storageConfigMapper.insert(storageConfig) > 0;
        if (flag) {
            refreshCache(storageConfig.getStorageConfigId());
            return;
        }
        throw new ServiceException("操作失败");
    }

    @Override
    public void updateStorageConfig(SysStorageConfigBo storageConfigBo) {
        // 校验配置是否存在
        SysStorageConfig oldConfig = validateFileConfigExists(storageConfigBo.getStorageConfigId());
        // 校验配置名是否唯一
        checkConfigNameUnique(storageConfigBo);

        // 校验配置数据
        StorageClientConfig clientConfig = parseClientConfig(storageConfigBo.getStorageType(), storageConfigBo.getConfigData());
        SysStorageConfig config = MapstructUtils.convert(storageConfigBo, SysStorageConfig.class);
        if (config == null) {
            throw new ServiceException("操作失败，转换对象为空");
        }
        // 使用解析后的强类型配置对象
        config.setConfigData(clientConfig);
        // 保持原有的master状态，防止被重置
        config.setMaster(oldConfig.isMaster());

        boolean flag = storageConfigMapper.updateById(config) > 0;
        if (flag) {
            refreshCache(config.getStorageConfigId());
            // 清空缓存
            clearClientCache(config.getStorageConfigId(), config.isMaster());
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
            if (CollUtil.containsAny(ids, StorageConstant.SYSTEM_DATA_IDS)) {
                throw new ServiceException("系统内置, 不可删除!");
            }
        }
        List<SysStorageConfig> list = CollUtil.newArrayList();
        for (Long configId : ids) {
            SysStorageConfig config = storageConfigMapper.selectById(configId);
            list.add(config);
        }
        boolean flag = storageConfigMapper.deleteByIds(ids) > 0;
        if (flag) {
            list.forEach(sysStorageConfig -> {
                CacheUtils.evict(CacheNames.SYS_STORAGE_CONFIG, sysStorageConfig.getConfigName());
                // 清空缓存
                clearClientCache(sysStorageConfig.getStorageConfigId(), sysStorageConfig.isMaster());
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
    public void updateStorageConfigMaster(SysStorageConfigBo storageConfigBo) {
        SysStorageConfig config = validateFileConfigExists(storageConfigBo.getStorageConfigId());
        // 先全部设置为非主配置
        storageConfigMapper.update(null, new LambdaUpdateWrapper<SysStorageConfig>()
            .set(SysStorageConfig::isMaster, false));
        // 设置当前为主配置
        int row = storageConfigMapper.update(null, new LambdaUpdateWrapper<SysStorageConfig>()
            .set(SysStorageConfig::isMaster, true)
            .eq(SysStorageConfig::getStorageConfigId, config.getStorageConfigId()));
        if (row > 0) {
            RedisUtils.setCacheObject(StorageConstant.DEFAULT_CONFIG_KEY, config.getConfigName());
            // 清空缓存
            CacheUtils.evict(CacheNames.SYS_STORAGE_CONFIG, config.getConfigName());
            clearClientCache(null, true);
        }
    }

    @Override
    public StorageClient getStorageConfigMaster() {
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
    public StorageClient getStorageConfigClient(Long id) {
        return clientCache.get(id);
    }

    @Override
    public String testStorageConfig(Long id) throws Exception {
        SysStorageConfig config = validateFileConfigExists(id);
        byte[] content = ResourceUtil.readBytes("file/test.jpg");
        String path = IdUtil.fastSimpleUUID() + ".jpg";
        StorageClient storageClient = getStorageConfigClient(id);
        if (storageClient instanceof DbStorageClient) {
            SysFile sysFile = new SysFile()
                .setConfigId(id)
                .setStorageType(resolveStorageType(config.getStorageType()))
                .setFileName(path)
                .setFilePath(path)
                .setMimeType("image/jpeg")
                .setFileSize((long) content.length);
            fileMapper.insert(sysFile);
            String url = storageClient.upload(content, path, "image/jpeg", sysFile.getId());
            sysFile.setUrl(url);
            fileMapper.updateById(sysFile);
            return url;
        }
        return storageClient.upload(content, path, "image/jpeg");
    }

    private String resolveStorageType(Integer storageType) {
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.getByStorageType(storageType);
        return storageTypeEnum != null ? storageTypeEnum.name() : String.valueOf(storageType);
    }

    private StorageClientConfig parseClientConfig(Integer storageType, Map<String, Object> configData) {
        // 获取配置类
        Class<? extends StorageClientConfig> configClass = StorageTypeEnum.getByStorageType(storageType)
            .getConfigClass();
        StorageClientConfig clientConfig = JsonUtils.parseObject2(JsonUtils.toJsonString(configData), configClass);
        // 参数校验
        ValidatorUtils.validate(clientConfig);
        // 设置参数
        return clientConfig;
    }

    private SysStorageConfig validateFileConfigExists(Long id) {
        SysStorageConfig config = storageConfigMapper.selectById(id);
        if (config == null) {
            throw new ServiceException("文件配置不存在");
        }
        return config;
    }

    /**
     * 刷新缓存
     */
    private void refreshCache(Long id) {
        SysStorageConfig config = storageConfigMapper.selectById(id);
        CacheUtils.put(CacheNames.SYS_STORAGE_CONFIG, config.getConfigName(), JsonUtils.toJsonString(config.getConfigData(), StorageClientConfig.class));
    }

    private void checkConfigNameUnique(SysStorageConfigBo bo) {
        long count = storageConfigMapper.selectCount(new LambdaQueryWrapper<SysStorageConfig>()
            .eq(SysStorageConfig::getConfigName, bo.getConfigName())
            .ne(bo.getStorageConfigId() != null, SysStorageConfig::getStorageConfigId, bo.getStorageConfigId()));
        if (count > 0) {
            throw new ServiceException("新增配置'" + bo.getConfigName() + "'失败，配置名称已存在");
        }
    }

    private LambdaQueryWrapper<SysStorageConfig> buildQueryWrapper(SysStorageConfigBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysStorageConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getConfigName()), SysStorageConfig::getConfigName, bo.getConfigName());
        lqw.eq(ObjectUtil.isNotNull(bo.getStorageType()), SysStorageConfig::getStorageType, bo.getStorageType());
        lqw.eq(ObjectUtil.isNotNull(bo.getMaster()), SysStorageConfig::isMaster, bo.getMaster());
        lqw.between(params.get("beginTime") != null && params.get("endTime") != null,
            SysStorageConfig::getCreateTime, params.get("beginTime"), params.get("endTime"));
        lqw.orderByDesc(SysStorageConfig::isMaster);
        lqw.orderByDesc(SysStorageConfig::getCreateTime);
        return lqw;
    }
}

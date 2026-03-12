package com.han.system.service.impl;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.han.common.core.constant.CacheNames;
import com.han.common.core.exception.ServiceException;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.utils.ValidatorUtils;
import com.han.common.core.utils.file.FileTypeUtils;
import com.han.common.json.utils.JsonUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.common.redis.utils.CacheUtils;
import com.han.common.redis.utils.RedisUtils;
import com.han.common.storage.constant.StorageConstant;
import com.han.common.storage.core.StorageClient;
import com.han.common.storage.core.StorageClientConfig;
import com.han.common.storage.core.db.DbStorageClient;
import com.han.common.storage.enums.StorageTypeEnum;
import com.han.common.storage.factory.StorageClientFactory;
import com.han.system.domain.SysFile;
import com.han.system.domain.SysStorageConfig;
import com.han.system.domain.bo.SysStorageConfigBo;
import com.han.system.domain.vo.SysStorageConfigVo;
import com.han.system.mapper.SysFileMapper;
import com.han.system.mapper.SysStorageConfigMapper;
import com.han.system.service.ISysStorageConfigService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
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
    private static final String TEST_FILE_RESOURCE_PATH = "file/test.jpg";
    private static final String TEST_FILE_DEFAULT_NAME = "test.jpg";
    private static final String TEST_FILE_DEFAULT_DIRECTORY = "test/storage";

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
                .build(this::loadClientByCacheKey);

        List<SysStorageConfig> list = storageConfigMapper.selectList();
        // 加载存储配置
        for (SysStorageConfig config : list) {
            String configName = config.getConfigName();
            if (config.isMaster()) {
                RedisUtils.setCacheObject(StorageConstant.DEFAULT_CONFIG_KEY, configName);
            }
            cacheConfigData(config);
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
        if (storageConfigBo == null) {
            throw new ServiceException("存储配置参数不能为空");
        }
        checkConfigNameUnique(storageConfigBo);
        SysStorageConfig storageConfig = buildStorageConfig(storageConfigBo);
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
        if (storageConfigBo == null) {
            throw new ServiceException("存储配置参数不能为空");
        }
        // 校验配置是否存在
        SysStorageConfig oldConfig = validateFileConfigExists(storageConfigBo.getStorageConfigId());
        // 校验配置名是否唯一
        checkConfigNameUnique(storageConfigBo);

        SysStorageConfig config = buildStorageConfig(storageConfigBo);
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
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        if (isValid) {
            if (CollUtil.containsAny(ids, StorageConstant.SYSTEM_DATA_IDS)) {
                throw new ServiceException("系统内置, 不可删除!");
            }
        }
        // 先批量加载并校验配置是否存在，避免逐条查询与空指针问题
        List<SysStorageConfig> list = listAndValidateConfigs(ids);
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
        if (storageConfigBo == null) {
            throw new ServiceException("存储配置参数不能为空");
        }
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
            // 切换主配置后同步刷新配置缓存，避免读取到旧值
            refreshCache(config.getStorageConfigId());
            clearClientCache(null, true);
            return;
        }
        throw new ServiceException("操作失败");
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
        if (id == null) {
            throw new ServiceException("配置编号不能为空");
        }
        return clientCache.get(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String testStorageConfig(Long id) throws Exception {
        byte[] content = ResourceUtil.readBytes(TEST_FILE_RESOURCE_PATH);
        if (content == null || content.length == 0) {
            throw new ServiceException("测试文件内容不能为空");
        }
        validateFileConfigExists(id);
        StorageClient storageClient = getStorageConfigClient(id);
        if (storageClient == null) {
            throw new ServiceException("存储客户端不存在");
        }
        String hash = DigestUtil.sha256Hex(content);
        SysFile oldFile = fileMapper.selectOneByConfigIdAndHash(id, hash);
        if (oldFile != null && StrUtil.isNotEmpty(oldFile.getUrl())) {
            return oldFile.getUrl();
        }
        String fileName = TEST_FILE_DEFAULT_NAME;
        String path = buildTestUploadPath(fileName);
        String storedName = FileUtil.getName(path);
        String resolvedMimeType = StringUtils.defaultIfBlank(FileTypeUtils.getMineType(content, fileName), "application/octet-stream");
        SysFile sysFile = new SysFile()
                .setConfigId(id)
                .setFileName(storedName)
                .setOriginalName(fileName)
                .setFilePath(path)
                .setMimeType(resolvedMimeType)
                .setFileSize((long) content.length)
                .setFileSuffix(FileUtil.extName(storedName))
                .setHash(hash);
        try {
            if (storageClient instanceof DbStorageClient) {
                fileMapper.insert(sysFile);
                String url = storageClient.upload(content, path, resolvedMimeType, sysFile.getId());
                sysFile.setUrl(url);
                fileMapper.updateById(sysFile);
                return url;
            }
            String url = storageClient.upload(content, path, resolvedMimeType);
            sysFile.setUrl(url);
            fileMapper.insert(sysFile);
            return url;
        } catch (Exception ex) {
            clearTestUpload(storageClient, path, sysFile.getId());
            throw ex;
        }
    }

    private void clearTestUpload(StorageClient storageClient, String path, Long fileId) {
        if (StringUtils.isNotBlank(path)) {
            try {
                storageClient.delete(path);
            } catch (Exception ex) {
                log.warn("测试上传失败后清理文件异常，path: {}", path, ex);
            }
        }
        if (fileId != null) {
            fileMapper.deleteById(fileId);
        }
    }

    private String buildTestUploadPath(String fileName) {
        String extension = FileUtil.extName(fileName);
        String randomFileName = StringUtils.isNotBlank(extension) ? IdUtil.fastSimpleUUID() + StrUtil.DOT + extension : IdUtil.fastSimpleUUID();
        return TEST_FILE_DEFAULT_DIRECTORY + StrUtil.SLASH + randomFileName;
    }

    private StorageClientConfig parseClientConfig(Integer storageType, Map<String, Object> configData) {
        // 先解析存储类型，避免无效类型导致空指针
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.getByStorageType(storageType);
        if (storageTypeEnum == null) {
            throw new ServiceException("不支持的存储类型: " + storageType);
        }
        Class<? extends StorageClientConfig> configClass = storageTypeEnum.getConfigClass();
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
        SysStorageConfig config = validateFileConfigExists(id);
        cacheConfigData(config);
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

    /**
     * 统一构建存储配置实体：
     * 1. 解析并校验动态配置
     * 2. BO 转实体
     * 3. 回填强类型配置对象
     */
    private SysStorageConfig buildStorageConfig(SysStorageConfigBo storageConfigBo) {
        StorageClientConfig clientConfig = parseClientConfig(storageConfigBo.getStorageType(),
                storageConfigBo.getConfigData());
        SysStorageConfig config = MapstructUtils.convert(storageConfigBo, SysStorageConfig.class);
        if (config == null) {
            throw new ServiceException("操作失败，转换对象为空");
        }
        config.setConfigData(clientConfig);
        return config;
    }

    /**
     * 根据缓存键加载客户端。
     * 说明：LoadingCache 不接受 null 返回值，因此这里统一做存在性校验并返回明确异常。
     */
    private StorageClient loadClientByCacheKey(Long key) {
        SysStorageConfig config;
        if (CACHE_MASTER_ID.equals(key)) {
            config = storageConfigMapper.selectOne(new LambdaQueryWrapper<SysStorageConfig>()
                    .eq(SysStorageConfig::isMaster, true));
            if (config == null) {
                throw new ServiceException("主存储配置不存在");
            }
        } else {
            config = validateFileConfigExists(key);
        }
        storageClientFactory.createOrUpdateFileClient(config.getStorageConfigId(),
                config.getStorageType(), config.getConfigData());
        StorageClient client = storageClientFactory.getFileClient(config.getStorageConfigId());
        if (client == null) {
            throw new ServiceException("存储客户端初始化失败，配置ID: " + config.getStorageConfigId());
        }
        return client;
    }

    /**
     * 回写存储配置到缓存。
     */
    private void cacheConfigData(SysStorageConfig config) {
        CacheUtils.put(CacheNames.SYS_STORAGE_CONFIG, config.getConfigName(),
                JsonUtils.toJsonString(config.getConfigData(), StorageClientConfig.class));
    }

    /**
     * 批量查询并校验配置存在性。
     * 说明：用于删除前校验，避免存在无效 ID 时出现空指针。
     */
    private List<SysStorageConfig> listAndValidateConfigs(Collection<Long> ids) {
        List<SysStorageConfig> configs = storageConfigMapper.selectByIds(ids);
        if (CollUtil.isEmpty(configs)) {
            throw new ServiceException("文件配置不存在");
        }
        Map<Long, SysStorageConfig> configMap = configs.stream()
                .collect(Collectors.toMap(SysStorageConfig::getStorageConfigId, item -> item, (left, right) -> left));
        for (Long id : ids) {
            if (!configMap.containsKey(id)) {
                throw new ServiceException("文件配置不存在，配置ID: " + id);
            }
        }
        return configs;
    }
}

package com.han.common.storage.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.common.storage.domain.StorageFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-03-09
 * @Description: 文件信息 Mapper 接口
 */
@Mapper
public interface StorageFileMapper extends BaseMapperPlus<StorageFile, StorageFile> {

    default StorageFile selectOneByConfigIdAndPath(Long configId, String path) {
        return selectOne(new LambdaQueryWrapper<StorageFile>()
            .eq(StorageFile::getConfigId, configId)
            .eq(StorageFile::getFilePath, path)
            .last("limit 1"));
    }
}

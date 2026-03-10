package com.han.common.storage.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.common.storage.domain.StorageFileContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: 文件内容存储 Mapper 接口
 */
@Mapper
public interface StorageFileContentMapper extends BaseMapperPlus<StorageFileContent, StorageFileContent> {

    default void deleteByFileId(Long fileId) {
        this.delete(new LambdaQueryWrapper<StorageFileContent>()
            .eq(StorageFileContent::getFileId, fileId));
    }

    default StorageFileContent selectOneByFileId(Long fileId) {
        return selectOne(new LambdaQueryWrapper<StorageFileContent>()
            .eq(StorageFileContent::getFileId, fileId)
            .last("limit 1"));
    }
}

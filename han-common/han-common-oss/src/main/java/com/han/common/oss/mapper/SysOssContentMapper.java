package com.han.common.oss.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.common.oss.domain.SysOssContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: OSS 内容存储 Mapper 接口
 */
@Mapper
public interface SysOssContentMapper extends BaseMapperPlus<SysOssContent, SysOssContent> {

    default void deleteByFileId(Long fileId) {
        this.delete(new LambdaQueryWrapper<SysOssContent>()
            .eq(SysOssContent::getFileId, fileId));
    }

    default SysOssContent selectOneByFileId(Long fileId) {
        return selectOne(new LambdaQueryWrapper<SysOssContent>()
            .eq(SysOssContent::getFileId, fileId)
            .last("limit 1"));
    }
}

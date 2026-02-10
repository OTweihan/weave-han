package com.han.common.oss.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.common.oss.domain.SysOssContent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: OSS 内容存储 Mapper 接口
 */
@Mapper
public interface SysOssContentMapper extends BaseMapperPlus<SysOssContent, SysOssContent> {

    default void deleteByConfigIdAndPath(Long ossId, String path) {
        this.delete(new LambdaQueryWrapper<SysOssContent>()
            .eq(SysOssContent::getOssConfigId, ossId)
            .eq(SysOssContent::getPath, path));
    }

    default List<SysOssContent> selectListByConfigIdAndPath(Long ossId, String path) {
        return selectList(new LambdaQueryWrapper<SysOssContent>()
            .eq(SysOssContent::getOssConfigId, ossId)
            .eq(SysOssContent::getPath, path));
    }
}

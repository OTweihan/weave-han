package com.han.common.oss.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.common.oss.domain.SysFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-03-09
 * @Description: 文件信息 Mapper 接口
 */
@Mapper
public interface SysFileMapper extends BaseMapperPlus<SysFile, SysFile> {

    default SysFile selectOneByConfigIdAndPath(Long configId, String path) {
        return selectOne(new LambdaQueryWrapper<SysFile>()
            .eq(SysFile::getConfigId, configId)
            .eq(SysFile::getFilePath, path)
            .last("limit 1"));
    }
}

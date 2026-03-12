package com.han.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysFile;
import com.han.system.domain.vo.SysFileVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 文件信息Mapper接口
 */
@Mapper
public interface SysFileMapper extends BaseMapperPlus<SysFile, SysFileVo> {

    default SysFile selectOneByConfigIdAndHash(Long configId, String hash) {
        return selectOne(new LambdaQueryWrapper<SysFile>()
            .eq(SysFile::getConfigId, configId)
            .eq(SysFile::getHash, hash)
            .orderByDesc(SysFile::getCreateTime)
            .last("limit 1"));
    }
}

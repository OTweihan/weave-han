package com.han.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysFile;
import com.han.system.domain.vo.SysOssVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 文件上传 数据层
 */
@Mapper
public interface SysFileMapper extends BaseMapperPlus<SysFile, SysOssVo> {

    default Page<SysOssVo> selectPageOssList(Page<SysFile> page, Wrapper<SysFile> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

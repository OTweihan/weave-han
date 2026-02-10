package com.han.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysOss;
import com.han.system.domain.SysUser;
import com.han.system.domain.vo.SysOssVo;
import com.han.system.domain.vo.SysUserVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 文件上传 数据层
 */
@Mapper
public interface SysOssMapper extends BaseMapperPlus<SysOss, SysOssVo> {

    default Page<SysOssVo> selectPageOssList(Page<SysOss> page, Wrapper<SysOss> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }
}

package com.han.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import com.han.common.core.utils.MapstructUtils;
import com.han.common.core.utils.StringUtils;
import com.han.common.core.exception.ServiceException;
import com.han.system.domain.SysSocial;
import com.han.system.domain.bo.SysSocialBo;
import com.han.system.domain.vo.SysSocialVo;
import com.han.system.mapper.SysSocialMapper;
import com.han.system.service.ISysSocialService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author thiszhc
 * @CreateTime: 2023-06-12
 * @Description: 社会化关系Service业务层处理
 */
@RequiredArgsConstructor
@Service
public class SysSocialServiceImpl implements ISysSocialService {

    private final SysSocialMapper baseMapper;

    /**
     * 查询社会化关系
     */
    @Override
    public SysSocialVo queryById(String id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 授权列表
     */
    @Override
    public List<SysSocialVo> queryList(SysSocialBo bo) {
        LambdaQueryWrapper<SysSocial> lqw = new LambdaQueryWrapper<SysSocial>()
            .eq(ObjectUtil.isNotNull(bo.getUserId()), SysSocial::getUserId, bo.getUserId())
            .eq(StringUtils.isNotBlank(bo.getAuthId()), SysSocial::getAuthId, bo.getAuthId())
            .eq(StringUtils.isNotBlank(bo.getSource()), SysSocial::getSource, bo.getSource());
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<SysSocialVo> queryListByUserId(Long userId) {
        return baseMapper.selectVoList(new LambdaQueryWrapper<SysSocial>().eq(SysSocial::getUserId, userId));
    }

    /**
     * 新增社会化关系
     */
    @Override
    public void insertByBo(SysSocialBo bo) {
        SysSocial add = MapstructUtils.convert(bo, SysSocial.class);
        if (ObjectUtil.isNull(add)) {
            throw new ServiceException("新增社会化关系失败，请联系管理员");
        }
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        } else {
            throw new ServiceException("新增社会化关系失败，请联系管理员");
        }
    }

    /**
     * 更新社会化关系
     */
    @Override
    public void updateByBo(SysSocialBo bo) {
        SysSocial update = MapstructUtils.convert(bo, SysSocial.class);
        if (ObjectUtil.isNull(update)) {
            throw new ServiceException("更新社会化关系失败，请联系管理员");
        }
        validEntityBeforeSave(update);
        int rows = baseMapper.updateById(update);
        if (rows <= 0) {
            throw new ServiceException("更新社会化关系失败，请联系管理员");
        }
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysSocial entity) {
        // 校验 AuthId 唯一性
        if (StringUtils.isNotBlank(entity.getAuthId())) {
            boolean exists = baseMapper.exists(new LambdaQueryWrapper<SysSocial>()
                .eq(SysSocial::getAuthId, entity.getAuthId())
                .ne(ObjectUtil.isNotNull(entity.getId()), SysSocial::getId, entity.getId()));
            if (exists) {
                throw new ServiceException("此三方账号已经被其他用户绑定");
            }
        }
        // 校验 User + Source 唯一性
        if (ObjectUtil.isNotNull(entity.getUserId()) && StringUtils.isNotBlank(entity.getSource())) {
            boolean exists = baseMapper.exists(new LambdaQueryWrapper<SysSocial>()
                .eq(SysSocial::getUserId, entity.getUserId())
                .eq(SysSocial::getSource, entity.getSource())
                .ne(ObjectUtil.isNotNull(entity.getId()), SysSocial::getId, entity.getId()));
            if (exists) {
                throw new ServiceException("该用户已绑定过[" + entity.getSource() + "]平台账号");
            }
        }
    }

    /**
     * 删除社会化关系
     */
    @Override
    public void deleteWithValidById(Long id) {
        int rows = baseMapper.deleteById(id);
        if (rows <= 0) {
            throw new ServiceException("删除社会化关系失败，请联系管理员");
        }
    }

    /**
     * 根据 authId 查询用户信息
     *
     * @param authId 认证id
     * @return 授权信息
     */
    @Override
    public List<SysSocialVo> selectByAuthId(String authId) {
        return baseMapper.selectVoList(new LambdaQueryWrapper<SysSocial>().eq(SysSocial::getAuthId, authId));
    }
}

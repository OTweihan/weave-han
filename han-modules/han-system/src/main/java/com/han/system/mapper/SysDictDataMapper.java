package com.han.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.common.mybatis.core.mapper.BaseMapperPlus;
import com.han.system.domain.SysDictData;
import com.han.system.domain.vo.SysDictDataVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 字典表 数据层
 */
@Mapper
public interface SysDictDataMapper extends BaseMapperPlus<SysDictData, SysDictDataVo> {

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 符合条件的字典数据列表
     */
    default List<SysDictDataVo> selectDictDataByType(String dictType) {
        return selectVoList(
            new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .orderByAsc(SysDictData::getDictSort));
    }
}

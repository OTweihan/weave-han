package com.han.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.han.common.mybatis.core.domain.BaseEntity;
import com.han.system.domain.SysFile;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 文件分页查询对象 sys_file
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysFile.class, reverseConvertGenerate = false)
public class SysFileBo extends BaseEntity {

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 存储配置ID
     */
    private Long configId;

    /**
     * 存储文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件类型
     */
    private String mimeType;

    /**
     * 访问URL
     */
    private String url;

}

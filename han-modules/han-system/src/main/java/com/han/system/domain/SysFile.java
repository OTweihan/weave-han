package com.han.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.han.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 文件存储对象
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {

    /**
     * 文件ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 存储配置ID
     */
    private Long configId;

    /**
     * 存储文件名
     */
    @TableField("stored_name")
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 访问URL
     */
    private String url;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 文件大小(byte)
     */
    private Long fileSize;

    /**
     * 文件hash(md5/sha256)
     */
    private String hash;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer delFlag;
}

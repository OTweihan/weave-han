package com.han.common.storage.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-03-09
 * @Description: 文件信息对象 sys_file
 */
@Data
@TableName("sys_file")
public class StorageFile {

    /**
     * 文件ID
     */
    @TableId("id")
    private Long id;

    /**
     * 存储配置ID
     */
    @TableField("config_id")
    private Long configId;

    /**
     * 存储路径
     */
    @TableField("file_path")
    private String filePath;
}

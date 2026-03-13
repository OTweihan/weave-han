package com.han.common.storage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: 存储内容存储对象 sys_file_content
 */
@Data
@Accessors(chain = true)
@TableName("sys_file_content")
public class StorageFileContent {

    /**
     * 文件ID（主键）
     */
    @TableId(value = "file_id", type = IdType.INPUT)
    private Long fileId;

    /**
     * 文件内容
     */
    private byte[] content;
}

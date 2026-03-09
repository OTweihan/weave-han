package com.han.common.oss.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: OSS内容存储对象 sys_file_content
 */
@Data
@Accessors(chain = true)
@TableName("sys_file_content")
public class SysOssContent {

    /**
     * 主键
     */
    @TableId("id")
    private Long contentId;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件内容
     */
    private byte[] content;
}

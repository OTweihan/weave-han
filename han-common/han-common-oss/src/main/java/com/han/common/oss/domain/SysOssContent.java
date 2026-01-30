package com.han.common.oss.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: OSS内容存储对象 sys_oss_content
 */
@Data
@TableName("sys_oss_content")
public class SysOssContent {

    /**
     * 主键
     */
    @TableId
    private Long contentId;

    /**
     * 文件路径(UUID)
     */
    private String path;

    /**
     * 文件内容
     */
    private byte[] content;

    /**
     * OSS主键(参考)
     */
    private Long ossId;
}

package com.han.common.oss.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-01-30
 * @Description: OSS内容存储对象 sys_oss_content
 */
@Data
@Accessors(chain = true)
@TableName("sys_oss_content")
public class SysOssContent {

    /**
     * 主键
     */
    @TableId
    private Long contentId;

    /**
     * OSS配置编号
     */
    private Long ossConfigId;

    /**
     * 文件路径(UUID)即文件名
     */
    private String path;

    /**
     * 文件内容
     */
    private byte[] content;
}

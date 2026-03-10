package com.han.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 数据库文件内容对象
 */
@Data
@Accessors(chain = true)
@TableName("sys_file_content")
public class SysFileContent implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件二进制内容
     */
    private byte[] content;
}

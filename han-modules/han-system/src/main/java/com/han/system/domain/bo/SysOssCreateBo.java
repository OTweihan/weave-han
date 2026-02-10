package com.han.system.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-02-09
 * @Description: 文件上传 创建 VO
 */
@Data
public class SysOssCreateBo {

    @NotNull(message = "文件配置编号不能为空")
    @Schema(description = "文件配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @NotNull(message = "文件路径不能为空")
    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "hqmall.jpg")
    private String path;

    @NotNull(message = "原文件名不能为空")
    @Schema(description = "原文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "hqmall.jpg")
    private String name;

    @NotNull(message = "文件 URL不能为空")
    @Schema(description = "文件 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/hqmall.jpg")
    private String url;

    @Schema(description = "文件 MIME 类型", example = "application/octet-stream")
    private String type;

    @Schema(description = "文件大小", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long size;
}

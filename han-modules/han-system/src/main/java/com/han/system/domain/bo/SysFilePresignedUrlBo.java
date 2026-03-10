package com.han.system.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: WeiHan
 * @CreateTime: 2026-02-09
 * @Description: 文件预签名地址信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysFilePresignedUrlBo {

    @Schema(description = "配置编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long configId;

    @Schema(description = "文件上传 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uploadUrl;

    @Schema(description = "文件访问 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED)
    private String path;
}

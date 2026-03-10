package com.han.system.domain.bo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author WeiHan
 * @CreateTime: 2026-02-09
 * @Description: 文件上传 Request VO
 */
@Data
public class SysFileUploadBo {

    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    @Schema(description = "文件目录", example = "XXX/YYY")
    private String directory;

    @AssertTrue(message = "文件目录不正确")
    @JsonIgnore
    public boolean isDirectoryValid() {
        return isDirectoryValid(directory);
    }

    public static boolean isDirectoryValid(String directory) {
        // 1. 不能包含 .. 防止目录穿越
        // 2. 不能以 / 或 \ 开头，防止上传到根目录
        // 如果目录为空，则认为是有效的（允许不指定目录）
        if (StrUtil.isEmpty(directory)) {
            return true;
        }
        return !StrUtil.contains(directory, "..")
            && !StrUtil.startWithAny(directory, "/", "\\");
    }
}

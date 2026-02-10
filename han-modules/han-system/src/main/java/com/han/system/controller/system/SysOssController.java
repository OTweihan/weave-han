package com.han.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.common.core.utils.file.FileUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.system.domain.SysOss;
import com.han.system.domain.bo.SysOssBo;
import org.springframework.http.MediaType;

import java.io.IOException;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.han.common.core.domain.R;
import com.han.common.web.core.BaseController;
import com.han.system.domain.bo.SysOssCreateBo;
import com.han.system.domain.bo.SysOssPresignedUrlBo;
import com.han.system.domain.bo.SysOssUploadBo;
import com.han.system.domain.vo.SysOssVo;
import com.han.system.service.ISysOssService;
import com.han.common.core.utils.MapstructUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author: Lion Li
 * @CreateTime: 2026-01-23
 * @Description: 文件上传 控制层
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/resource/oss")
public class SysOssController extends BaseController {

    private final ISysOssService ossService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    @Parameter(name = "file", description = "文件附件", required = true,
        schema = @Schema(type = "string", format = "binary"))
    public R<String> uploadFile(@Valid SysOssUploadBo uploadVo) throws Exception {
        MultipartFile file = uploadVo.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return R.ok(ossService.createFile(content, file.getOriginalFilename(), uploadVo.getDirectory(), file.getContentType()).getUrl());
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址（上传）", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @Parameters({
        @Parameter(name = "name", description = "文件名称", required = true),
        @Parameter(name = "directory", description = "文件目录")
    })
    public R<SysOssPresignedUrlBo> getFilePresignedUrl(
        @RequestParam("name") String name,
        @RequestParam(value = "directory", required = false) String directory) {
        return R.ok(ossService.presignPutUrl(name, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    public R<Long> createFile(@Valid @RequestBody SysOssCreateBo createVo) {
        return R.ok(ossService.createFile(createVo));
    }

    @GetMapping("/get")
    @Operation(summary = "获得文件")
    @Parameter(name = "id", description = "编号", required = true)
    @SaCheckPermission("system:oss:query")
    public R<SysOssVo> getFile(@RequestParam("id") Long id) {
        return R.ok(MapstructUtils.convert(ossService.getOssFile(id), SysOssVo.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    @Parameter(name = "id", description = "编号", required = true)
    @SaCheckPermission("system:oss:delete")
    public R<Boolean> deleteFile(@RequestParam("ids") List<Long> ids) throws Exception {
        ossService.deleteFile(ids);
        return R.ok(true);
    }

    @GetMapping("/{ossConfigId}/get/**")
    @PermitAll
    @Operation(summary = "下载文件")
    @Parameter(name = "ossConfigId", description = "配置编号", required = true)
    public void getFileContent(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable Long ossConfigId) throws Exception {
        // 获取请求的路径
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        path = URLUtil.decode(path, StandardCharsets.UTF_8, false);

        // 读取内容
        byte[] content = ossService.getFileContent(ossConfigId, path);
        if (content == null) {
            log.warn("配置编号：{}，路径：{}，文件不存在]", ossConfigId, path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        writeAttachment(response, path, content);
    }

    @GetMapping("/page")
    @Operation(summary = "获得文件分页")
    @SaCheckPermission("system:oss:query")
    public TableDataInfo<SysOssVo> getFilePage(SysOssBo ossBo, PageQuery pageQuery) {
        return ossService.selectPageOssList(ossBo, pageQuery);
    }

    private void writeAttachment(HttpServletResponse response, String filename, byte[] content) throws IOException {
        // 设置 header
        FileUtils.setAttachmentResponseHeader(response, filename);
        // 设置 contentType
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 输出附件
        IoUtil.write(response.getOutputStream(), false, content);
    }
}

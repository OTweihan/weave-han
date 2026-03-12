package com.han.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.han.common.core.domain.dto.FileDTO;
import com.han.common.core.utils.file.FileUtils;
import com.han.common.mybatis.core.page.PageQuery;
import com.han.common.mybatis.core.page.TableDataInfo;
import com.han.system.domain.bo.SysFileBo;
import org.springframework.http.MediaType;

import java.io.IOException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.han.common.core.domain.R;
import com.han.common.web.core.BaseController;
import com.han.system.domain.bo.SysFileCreateBo;
import com.han.system.domain.bo.SysFilePresignedUrlBo;
import com.han.system.domain.bo.SysFileUploadBo;
import com.han.system.domain.vo.SysFileVo;
import com.han.system.service.ISysFileService;
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
 * @Author: WeiHan
 * @CreateTime: 2026-03-10
 * @Description: 文件上传 控制层
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/resource/file")
public class SysFileController extends BaseController {

    private final ISysFileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    @Parameter(name = "file", description = "文件附件", required = true,
        schema = @Schema(type = "string", format = "binary"))
    @SaCheckPermission("system:file:upload")
    public R<SysFileVo> uploadFile(@Valid SysFileUploadBo uploadVo) throws Exception {
        MultipartFile file = uploadVo.getFile();
        // 一次性读取内容，沿用现有服务层入参（byte[]）完成上传
        byte[] content = IoUtil.readBytes(file.getInputStream());
        return R.ok(fileService.createFile(content, file.getOriginalFilename(), uploadVo.getDirectory(), file.getContentType()));
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址（上传）", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @Parameters({
        @Parameter(name = "name", description = "文件名称", required = true),
        @Parameter(name = "directory", description = "文件目录")
    })
    @SaCheckPermission("system:file:upload")
    public R<SysFilePresignedUrlBo> getFilePresignedUrl(
        @RequestParam("name") String name,
        @RequestParam(value = "directory", required = false) String directory) {
        return R.ok(fileService.presignPutUrl(name, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @SaCheckPermission("system:file:upload")
    public R<Long> createFile(@Valid @RequestBody SysFileCreateBo createVo) {
        return R.ok(fileService.createFile(createVo));
    }

    @GetMapping("/get")
    @Operation(summary = "获得文件")
    @Parameter(name = "id", description = "编号", required = true)
    @SaCheckPermission("system:file:query")
    public R<SysFileVo> getFile(@RequestParam("id") Long id) {
        return R.ok(MapstructUtils.convert(fileService.getFile(id), SysFileVo.class));
    }

    @GetMapping("/listByIds/{fileIds}")
    @Operation(summary = "按编号批量获取文件")
    @Parameter(name = "fileIds", description = "文件编号串，逗号分隔", required = true)
    public R<List<FileDTO>> listByIds(@PathVariable String fileIds) {
        return R.ok(fileService.selectByIds(fileIds));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @SaCheckPermission("system:file:delete")
    public R<Boolean> deleteFile(@RequestParam("ids") List<Long> ids) throws Exception {
        fileService.deleteFile(ids);
        return R.ok(true);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载文件（按编号）")
    @Parameter(name = "id", description = "文件编号", required = true)
    @SaCheckPermission("system:file:download")
    public void downloadFile(HttpServletResponse response, @PathVariable Long id) throws Exception {
        var sysFile = fileService.getFile(id);
        byte[] content = fileService.getFileContent(sysFile.getConfigId(), sysFile.getFilePath());
        if (content == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        writeAttachment(response, sysFile.getOriginalName(), content);
    }

    @GetMapping("/{configId}/get/**")
    @PermitAll
    @Operation(summary = "下载文件")
    @Parameter(name = "configId", description = "配置编号", required = true)
    public void getFileContent(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable Long configId) throws Exception {
        // 解析下载路径：/resource/file/{configId}/get/** 中的 **
        String path = extractPath(request);

        // 读取并输出文件内容
        byte[] content = fileService.getFileContent(configId, path);
        if (content == null) {
            log.warn("配置编号：{}，路径：{}，文件不存在", configId, path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        writeAttachment(response, FileUtil.getName(path), content);
    }

    @GetMapping("/page")
    @Operation(summary = "获得文件分页")
    @SaCheckPermission("system:file:query")
    public TableDataInfo<SysFileVo> getFilePage(SysFileBo fileBo, PageQuery pageQuery) {
        return fileService.selectPageFileList(fileBo, pageQuery);
    }

    private void writeAttachment(HttpServletResponse response, String filename, byte[] content) throws IOException {
        // 设置 header
        FileUtils.setAttachmentResponseHeader(response, filename);
        // 设置 contentType
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 输出附件
        IoUtil.write(response.getOutputStream(), false, content);
    }

    /**
     * 提取并解码下载路径，统一路径为空时的异常处理。
     */
    private String extractPath(HttpServletRequest request) {
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        return URLUtil.decode(path, StandardCharsets.UTF_8, false);
    }
}

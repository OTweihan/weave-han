package com.han.common.core.utils.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.IOException;

/**
 * @Author WeiHan
 * @CreateTime: 2026-02-10
 * @Description: 文件类型工具类
 */
@Slf4j
public class FileTypeUtils {

    private static final Tika TIKA = new Tika();

    /**
     * 通过文件内容检测 MIME 类型
     * 仅基于文件内容进行检测，对于某些文件类型（如 doc、jar 等）可能会有识别误差
     *
     * @param data 文件内容字节数组
     * @return MIME 类型，无法识别时返回 "application/octet-stream"
     */
    @SneakyThrows
    public static String getMineType(byte[] data) {
        return TIKA.detect(data);
    }

    /**
     * 通过文件名检测 MIME 类型
     * 仅基于文件扩展名进行检测，在某些情况下比通过字节数组更准确，
     * 例如对于 jar 文件，通过文件名检测更为可靠
     *
     * @param name 文件名（包含扩展名）
     * @return MIME 类型，无法识别时返回 "application/octet-stream"
     */
    public static String getMineType(String name) {
        return TIKA.detect(name);
    }

    /**
     * 结合文件内容和文件名检测 MIME 类型（推荐）
     * 同时使用文件内容和文件名进行检测，准确性最高，
     * 是处理文件类型检测的最佳实践
     *
     * @param data 文件内容字节数组
     * @param name 文件名（包含扩展名）
     * @return MIME 类型，无法识别时返回 "application/octet-stream"
     */
    public static String getMineType(byte[] data, String name) {
        return TIKA.detect(data, name);
    }

    /**
     * 根据 MIME 类型获取文件扩展名
     * 将标准 MIME 类型转换为对应的文件扩展名
     *
     * @param mineType MIME 类型（如 "image/png"）
     * @return 文件扩展名（如 ".png"），获取失败或发生异常时返回 null
     */
    public static String getExtension(String mineType) {
        try {
            return MimeTypes.getDefaultMimeTypes().forName(mineType).getExtension();
        } catch (MimeTypeException e) {
            log.warn("[getExtension] 获取文件后缀 '{}' 失败: {}", mineType, e.getMessage());
            return null;
        }
    }

    /**
     * 处理文件下载响应
     * 自动检测文件类型并设置合适的响应头：
     * - Content-Type：根据文件内容自动检测
     * - Content-Disposition：图片内联显示，其他文件附件下载
     * - Accept-Ranges 和 Content-Length：针对视频文件的特殊处理
     *
     * @param response HTTP 响应对象
     * @param filename 文件名
     * @param content  文件内容字节数组
     * @throws IOException IO 异常
     */
    public static void writeAttachment(HttpServletResponse response, String filename, byte[] content) throws IOException {
        // 设置 header 和 contentType
        String mineType = getMineType(content, filename);
        response.setContentType(mineType);

        // 设置内容显示、下载文件名
        if (isImage(mineType)) {
            response.setHeader("Content-Disposition", "inline;filename=" + FileUtils.percentEncode(filename));
        } else {
            response.setHeader("Content-Disposition", "attachment;filename=" + FileUtils.percentEncode(filename));
        }

        // 针对 video 的特殊处理，解决视频地址在移动端播放的兼容性问题
        if (StrUtil.containsIgnoreCase(mineType, "video")) {
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(content.length));
        }

        // 输出附件
        IoUtil.write(response.getOutputStream(), false, content);
    }

    /**
     * 判断是否为图片类型
     * 通过检查 MIME 类型是否以 "image/" 开头来判断
     *
     * @param mineType MIME 类型
     * @return true 表示是图片类型，false 表示不是
     */
    public static boolean isImage(String mineType) {
        return StrUtil.startWith(mineType, "image/");
    }
}

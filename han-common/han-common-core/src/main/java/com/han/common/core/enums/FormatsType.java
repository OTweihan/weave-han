package com.han.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.han.common.core.utils.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Lion Li
 * @CreateTime: 2026-01-21
 * @Description: 日期格式与时间格式枚举
 */
@Getter
@AllArgsConstructor
public enum FormatsType {

    /**
     * 年份（2位）
     * 示例：2023 → "23"
     */
    YY("yy"),

    /**
     * 年份（4位）
     * 示例：2023 → "2023"
     */
    YYYY("yyyy"),

    /**
     * 年-月
     * 示例：2023-07
     */
    YYYY_MM("yyyy-MM"),

    /**
     * 年-月-日
     * 示例：2023-07-22
     */
    YYYY_MM_DD("yyyy-MM-dd"),

    /**
     * 年-月-日 时:分（24小时制）
     * 示例：2023-07-22 15:30
     */
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),

    /**
     * 年-月-日 时:分:秒（24小时制）
     * 示例：2023-07-22 15:30:45
     */
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),

    /**
     * 时:分:秒（24小时制）
     * 示例：15:30:45
     */
    HH_MM_SS("HH:mm:ss"),

    /**
     * 年/月
     * 示例：2023/07
     */
    YYYY_MM_SLASH("yyyy/MM"),

    /**
     * 年/月/日
     * 示例：2023/07/22
     */
    YYYY_MM_DD_SLASH("yyyy/MM/dd"),

    /**
     * 年/月/日 时:分
     * 示例：2023/07/22 15:30
     */
    YYYY_MM_DD_HH_MM_SLASH("yyyy/MM/dd HH:mm"),

    /**
     * 年/月/日 时:分:秒
     * 示例：2023/07/22 15:30:45
     */
    YYYY_MM_DD_HH_MM_SS_SLASH("yyyy/MM/dd HH:mm:ss"),

    /**
     * 年.月
     * 示例：2023.07
     */
    YYYY_MM_DOT("yyyy.MM"),

    /**
     * 年.月.日
     * 示例：2023.07.22
     */
    YYYY_MM_DD_DOT("yyyy.MM.dd"),

    /**
     * 年.月.日 时:分
     * 示例：2023.07.22 15:30
     */
    YYYY_MM_DD_HH_MM_DOT("yyyy.MM.dd HH:mm"),

    /**
     * 年.月.日 时:分:秒
     * 示例：2023.07.22 15:30:45
     */
    YYYY_MM_DD_HH_MM_SS_DOT("yyyy.MM.dd HH:mm:ss"),

    /**
     * 年月（紧凑）
     * 示例：202307
     */
    YYYYMM("yyyyMM"),

    /**
     * 年月日（紧凑）
     * 示例：20230722
     */
    YYYYMMDD("yyyyMMdd"),

    /**
     * 年月日时（紧凑）
     * 示例：2023072215
     */
    YYYYMMDDHH("yyyyMMddHH"),

    /**
     * 年月日时分（紧凑）
     * 示例：202307221530
     */
    YYYYMMDDHHMM("yyyyMMddHHmm"),

    /**
     * 年月日时分秒（紧凑）
     * 示例：20230722153045
     */
    YYYYMMDDHHMMSS("yyyyMMddHHmmss");

    /**
     * 日期时间格式字符串（符合 SimpleDateFormat 规范）
     */
    private final String timeFormat;

    /**
     * 缓存枚举值的 Map，用于快速查找
     */
    private static final Map<String, FormatsType> FORMAT_MAP = Arrays.stream(values())
        .collect(Collectors.toMap(FormatsType::getTimeFormat, Function.identity()));

    /**
     * 根据传入的格式字符串查找匹配的枚举值
     * <p>
     * 匹配规则：字符串中只要包含该枚举的格式串即可匹配（使用 String.contains 判断）。
     * </p>
     *
     * @param str 待匹配的格式字符串
     * @return 匹配到的 FormatsType 的 Optional
     */
    public static Optional<FormatsType> getFormatsType(String str) {
        if (StringUtils.isBlank(str)) {
            return Optional.empty();
        }
        return FORMAT_MAP.entrySet().stream()
            .filter(entry -> StringUtils.contains(str, entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    /**
     * 返回枚举的描述性字符串
     *
     * @return 格式字符串
     */
    @Override
    public String toString() {
        return timeFormat;
    }
}

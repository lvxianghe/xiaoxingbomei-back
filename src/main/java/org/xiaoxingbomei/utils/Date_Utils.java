package org.xiaoxingbomei.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class Date_Utils
{

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_yyyy_MM_dd = "yyyy-MM-dd";
    private static final String DEFAULT_DATE_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    private static final String DEFAULT_DATE_yyyyMMddHHmm = "yyyyMMddHHmm";
    private static final String DEFAULT_DATE_yyyyMMddHH = "yyyyMMddHH";
    private static final String DEFAULT_DATE_yyyyMMdd = "yyyyMMdd";
    private static final String DEFAULT_DATE_yyyyMM = "yyyyMM";

    // ======================================================================

    /**
     * 获取当前时间
     */
    public static String getCurrentTime()
    {
        return getCurrentTime(DEFAULT_DATE_FORMAT);
    }
    public static String getCurrentTime(String format)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.now().format(formatter);
    }
}

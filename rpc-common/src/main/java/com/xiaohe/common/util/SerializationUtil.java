package com.xiaohe.common.util;

import java.util.stream.IntStream;

/**
 * @author : 小何
 * @Description :
 * @date : 2023-12-03 16:36
 */
public class SerializationUtil {
    /**
     * 不够字节数时，向后填充的字符，默认为"0"
     */
    private static final String PADDING_STRING = "0";

    /**
     * 约定序列化类型最大长度为16
     */
    public static final int MAX_SERIALIZATION_TYPE_COUNT = 16;

    /**
     * 不满16字节时填充字符
     * @param str
     * @return
     */
    public static String paddingString(String str) {
        str = transNullToEmpty(str);
        if (str.length() >= MAX_SERIALIZATION_TYPE_COUNT) {
            return str;
        }
        int paddingCount = MAX_SERIALIZATION_TYPE_COUNT - str.length();
        StringBuilder paddingString = new StringBuilder(str);
        IntStream.range(0, paddingCount).forEach(i -> {
            paddingString.append(PADDING_STRING);
        });
        return paddingString.toString();
    }

    /**
     * null转换为空字符串
     * @param str
     * @return
     */
    public static String transNullToEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * 字符串去零操作
     * @param str
     * @return
     */
    public static String subString(String str) {
        str = transNullToEmpty(str);
        return str.replace(PADDING_STRING, "");
    }



}

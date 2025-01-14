package com.ymd.cloud.common.utils;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @version 1.0
 * @description 判断是否为空
 */
public class EmptyUtil {
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;
        else if (obj instanceof CharSequence) return ((CharSequence) obj).length() == 0;
        else if (obj instanceof Collection) return ((Collection) obj).isEmpty();
        else if (obj instanceof Map) return ((Map) obj).isEmpty();
        else if (obj.getClass().isArray()) return Array.getLength(obj) == 0;

        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static void main(String[] args) {
        String str=" ";
        System.out.println(isEmpty(str.trim()));
    }
}

package com.ymd.cloud.common.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class IdGenUtil {
    private static SecureRandom random = new SecureRandom();
    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    /**
     * 使用SecureRandom随机生成Long.
     */
    public static long randomLong() {
        return Math.abs(random.nextLong());
    }

    public static int randomInteger(){
        return Math.abs(random.nextInt());
    }
}
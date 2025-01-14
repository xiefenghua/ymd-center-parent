package com.ymd.cloud.common.utils;

public class ClzUtil{
    public static String getMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stackTrace[2];
        return e.getMethodName();
    }
}

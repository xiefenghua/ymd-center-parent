package com.ymd.cloud.common.utils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * @author zhanggy
 * @version 1.0
 * @date 2021/3/13 1:39
 */
@Service
@Lazy(false)
public class IdGen {

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



    public static void main(String[] args) {
//        System.out.println(IdGen.uuid());
//        System.out.println(IdGen.uuid().length());
        for (int i=0; i<920; i++){
//			System.out.println(IdGen.randomLong());
            System.out.println(IdGen.randomInteger());
//            System.out.println(uuid());
        }
    }

}
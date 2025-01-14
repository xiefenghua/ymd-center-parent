package com.ymd.cloud.common.utils;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import sun.security.provider.MD5;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

public class UUIDUtil {
    // 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f","g","h","i","j","k","l","m","n","o","p","q","r","s","t",
            "u","v","w","x","y","z"
    };
    public static String generateNo() {
        String taskNo = IdGenUtil.uuid().toUpperCase()+ DateUtil.format(new Date(), DateUtil.yyyyMMddHHmmssSSS);
        return taskNo;
    }
    //根据内容生成8位uid
    @SneakyThrows
    public static String getUid(String content) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Hash = md.digest(content.getBytes());
        String md5Hex = Hex.encodeHexString(md5Hash);
        String uid = md5Hex.substring(0, 8);
        return uid;
    }
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组

            resultString = byteToString(md.digest(strObj.getBytes("UTF-8"))).toUpperCase();

        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resultString;
    }
}

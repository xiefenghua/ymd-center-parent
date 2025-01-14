package com.ymd.cloud.authorizeCenter.util;

import com.ymd.cloud.common.utils.EmptyUtil;

/**
 * @author wangyakun_775303056@qq.com
 * @version 1.0
 * @description
 * @date 2020/10/16 14:42
 */
public class AuthorizeUtil {
    public static String toByte="0123456789ABCDEF";
    // 生成校验位
    public static int getCRC(String str) {
        if(EmptyUtil.isEmpty(str)){
            return 0;
        }
        byte[] data = hexStringToBytes(str);
        int result = 0x0;
        for (byte b : data) {
            result += (b & 0xFF);
            result &= 0xFFFF;
        }
        result &= 0xFFFF;

        return result;

    }

    // 字符串转16进制字符串
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }


    // 其他格式转换
    public static byte[] intToByteArray_LITTLE_ENDIAN2(long i) {
        byte[] result = new byte[2];
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }


    // 时间格式转换
    public static byte[] intToByteArray_LITTLE_ENDIAN(long i) {
        byte[] result = new byte[4];
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    public static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        if (hexString.length() % 2 != 0) {
            hexString = "0"+ hexString;
        }
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    public static byte charToByte(char c) {
        return (byte) toByte.indexOf(c);
    }

    // 小端模式
    public static long bytes2Int_LITTLE_ENDIAN(byte[] bytes) {
        long time = (bytes[3] << 24) & 0xFF000000 | (bytes[2] << 16) & 0xFF0000 | (bytes[1] << 8) & 0xFF00
                | (bytes[0]) & 0xFF;
        return time;
    }
}

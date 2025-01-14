package com.ymd.cloud.authorizeCenter.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class ImgToBase64 {
    public static byte[] getImgByte(String uri) {
        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        //读取图片字节数组
        try {
            URL url = new URL(uri);
            final byte[] by = new byte[1024];
            // 创建链接获取图片
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream in = conn.getInputStream();
            int len = -1;
            while ((len = in.read(by)) != -1) {
                data.write(by, 0, len);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toByteArray();
    }
    public static File generateFile(String uri) {
        File file =null;
        //读取图片字节数组
        try {
            URL url = new URL(uri);
            final byte[] by = new byte[1024];
            // 创建链接获取图片
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream in = conn.getInputStream();
            file = new File(url.getFile());
            FileUtils.copyInputStreamToFile(in,file);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    public static String getBase64(String uri) {
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        //返回Base64编码过的字节数组字符串
        String encode = encoder.encode(getImgByte(uri));
        encode = encode.replaceAll("[\\s*\t\n\r]", "");
        return encode;
    }
    /**
     * 将网络图片转换成Base64编码字符串
     *
     * @param imgUrl 网络图片Url
     * @return
     */
    public String getImgUrlToBase64(String imgUrl) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            byte[] buffer;
            // 创建URL
            URL url = new URL(imgUrl);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            inputStream = conn.getInputStream();
            outputStream = new ByteArrayOutputStream();
            // 将内容读取内存中
            buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            buffer = outputStream.toByteArray();
            // 对字节数组Base64编码
            return new BASE64Encoder().encode(buffer);
        } catch (IOException ex) {
            log.error("同步浙江省居住证登记信息[getImgUrlToBase64]",ex.fillInStackTrace());
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // 关闭outputStream流
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        return "";
    }
}

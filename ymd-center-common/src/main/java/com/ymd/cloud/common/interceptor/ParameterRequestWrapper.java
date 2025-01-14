/**
 * Copyright (C), 杭州云门道科技有限公司
 * FileName:
 * Author:   hhd668@163.com
 * Date:     2019/3/18 11:23
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.ymd.cloud.common.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.common.utils.EmptyUtil;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 重写HttpServletRequestWrapper类<br>
 *
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;

    public ParameterRequestWrapper(HttpServletRequest request)
            throws IOException {
        super(request);
        body = readBytes(request, "UTF-8");
        //body = readBytes(request.getReader(), "utf-8");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    /**
     * 通过BufferedReader和字符编码集转换成byte数组
     * @param br
     * @param encoding
     * @return
     * @throws IOException
     */
    private byte[] readBytes(BufferedReader br,String encoding) throws IOException{
        String str = null,retStr="";
        while ((str = br.readLine()) != null) {
            retStr += str;
        }
        if (EmptyUtil.isNotEmpty(retStr)) {
            return retStr.getBytes(Charset.forName(encoding));
        }
        return null;
    }
    
    /**
     * 通过BufferedReader和字符编码集转换成byte数组
     * @param request
     * @param encoding
     * @return
     * @throws IOException
     */
    public byte[] readBytes(HttpServletRequest request, String encoding) throws IOException{
        String str = null,retStr="";      
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        
        while ((str = br.readLine()) != null) {
            retStr += str;
        }
        if (EmptyUtil.isNotEmpty(retStr)) {
            return retStr.getBytes(Charset.forName(encoding));
        }else{
            Map<String, String[]> parameterMap = request.getParameterMap();
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String[] value = entry.getValue();
                if(value != null){
                    obj.put(entry.getKey(),value);
                }
            }
            return obj.toJSONString().getBytes("UTF-8");
        }
    }    
}
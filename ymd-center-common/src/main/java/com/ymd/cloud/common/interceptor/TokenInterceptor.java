/**
 * Copyright (C), 杭州云门道科技有限公司
 * FileName:
 * Author:   hhd668@163.com
 * Date:     2019/3/8 11:31
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.ymd.cloud.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * TokenInterceptor 拦截器父类 <br>
 */
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {
    //前置拦截
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception ex) throws Exception {
        if (ex != null) {
            log.error("<== afterCompletion - 解析token失败. ex={}", ex.getMessage(), ex);
            handleException(response, ErrorCodeEnum.GL99990007.code(),ErrorCodeEnum.GL99990007.msg());
        }
    }

    protected boolean checkParams(Map<String,String> paramsMap, HttpServletResponse response){
        if(EmptyUtil.isEmpty(paramsMap)) return false;
        String time = paramsMap.get("time");
        if(EmptyUtil.isEmpty(time)) {
            handleException(response, ErrorCodeEnum.GL99990006.code(),ErrorCodeEnum.GL99990006.msg());
            log.info("<== preHandle - time参数为NULL　　==〉 ");
            return false;
        }
        String token = paramsMap.get("token");
        if (EmptyUtil.isEmpty(token)) {
            handleException(response, ErrorCodeEnum.GL99990005.code(),ErrorCodeEnum.GL99990005.msg());
            log.info("<== preHandle - 请求无token参数  　　==〉 ");
            return false;
        }
        return true;
    }
    protected Map<String,String>  getParameterMap(HttpServletRequest request, HttpServletResponse response) {
        Map<String,String> paramsMap = new HashMap<String,String>();
        try{
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (EmptyUtil.isNotEmpty(parameterMap)){
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    String[] value = entry.getValue();
                    if(EmptyUtil.isNotEmpty(value)){
                        paramsMap.put(entry.getKey(),value[0]);
                    }
                }
            }else{
                // 获取请求的输入流
                String body = getBodyString(request);
                if(EmptyUtil.isNotEmpty(body)) {
                    if(!isJson(body)){
                        body = getJsonStrByQueryUrl(body);
                    }
                    JSONObject json = JSON.parseObject(body);
                    for (Map.Entry<String, Object> entry : json.entrySet()) {
                        paramsMap.put(entry.getKey(),String.valueOf(entry.getValue()));
                    }
                }
            }
        }catch (Exception ex){
            handleException(response, ErrorCodeEnum.GL99990004.code(),ErrorCodeEnum.GL99990004.msg());
            log.info("getParameterMap exception ,messages : " + ex.getMessage());
        }

        return paramsMap;
    }

    private  String getJsonStrByQueryUrl (String paramStr){
        String[] params = paramStr.split("&");
        JSONObject obj = new JSONObject();
        for (int i = 0; i < params.length; i++) {
            String[] param = params[i].split("=");
            if (param.length >= 2) {
                String key = param[0];
                String value = param[1];
                for (int j = 2; j < param.length; j++) {
                    value += "=" + param[j];
                }
                try {
                    value = URLDecoder.decode(value,"utf-8");
                    obj.put(key,value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return obj.toString();
    }
    private boolean isJson(String content) {
        try {
            JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    protected void handleException(HttpServletResponse res,String status,String message) {
        try {
            res.resetBuffer();
            res.setHeader("Access-Control-Allow-Origin", "*");
            res.setHeader("Access-Control-Allow-Credentials", "true");
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write("{\"status\":\"" + status + "\" ,\"message\" :\"" + message + "\"}");
            res.flushBuffer();
        }catch (IOException ex){
            log.info("handleException  : " + ex.getMessage());
        }
    }

    /**
     * 获取请求Body
     *
     * @param request
     *
     * @return
     */
    public static String getBodyString(final ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = cloneInputStream(request.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * Description: 复制输入流</br>
     *
     * @param inputStream
     *
     * @return</br>
     */
    public static InputStream cloneInputStream(ServletInputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return byteArrayInputStream;
    }
}

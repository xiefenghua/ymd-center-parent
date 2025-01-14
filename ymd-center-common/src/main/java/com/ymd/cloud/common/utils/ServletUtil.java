package com.ymd.cloud.common.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtil
{
    /**
     * 获取String参数
     */
    public static String getParameter(String name)
    {
        return getRequest().getParameter(name);
    }
    
    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue)
    {
        return ConvertUtil.toStr(getRequest().getParameter(name), defaultValue);
    }
    
    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name)
    {
        return ConvertUtil.toInt(getRequest().getParameter(name));
    }
    
    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue)
    {
        return ConvertUtil.toInt(getRequest().getParameter(name), defaultValue);
    }
    
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }
    
    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes)attributes;
    }
    
    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        return getRequestAttributes().getResponse();
    }
}

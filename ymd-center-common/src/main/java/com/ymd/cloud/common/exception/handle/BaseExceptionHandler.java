package com.ymd.cloud.common.exception.handle;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Web异常处理基类
 * 
 * @author 黄华东
 * @version [V1.00, 2020年04月22月]
 * @see [相关类/方法]
 * @since V1.00
 */
public class BaseExceptionHandler
{
    @ExceptionHandler(Exception.class)
    public ModelAndView hanlerExceptions(Exception ex)
    {
        String message = ex.getMessage();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code","");
        modelAndView.addObject("message", message);
        modelAndView.addObject("timestamp", System.currentTimeMillis());
        return modelAndView;
    }
}

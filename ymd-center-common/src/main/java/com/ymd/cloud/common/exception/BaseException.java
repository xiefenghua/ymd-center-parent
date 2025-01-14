package com.ymd.cloud.common.exception;

import com.ymd.cloud.common.enumsSupport.HttpCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;

/**
 * 异常基类
 *
 * @author xfh
 * @since V1.00
 */
public abstract class BaseException extends RuntimeException
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4816694446914275501L;
    
    public BaseException()
    {
    }
    
    public BaseException(Throwable ex)
    {
        super(ex);
    }
    
    public BaseException(String message)
    {
        super(message);
    }
    
    public BaseException(String message, Throwable ex)
    {
        super(message, ex);
    }
    
    public void handler(ModelMap modelMap)
    {
        modelMap.put(ReturnMessageKey.CODE, getCode().value());
        if (StringUtils.isNotBlank(getMessage()))
        {
            modelMap.put(ReturnMessageKey.MESSAGE, getMessage());
        }
        else
        {
            modelMap.put(ReturnMessageKey.MESSAGE, getCode().msg());
        }
        modelMap.put(ReturnMessageKey.TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
    }
    
    protected abstract HttpCode getCode();
}

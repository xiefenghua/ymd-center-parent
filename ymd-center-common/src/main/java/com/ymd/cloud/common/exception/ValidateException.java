package com.ymd.cloud.common.exception;


import com.ymd.cloud.common.enumsSupport.HttpCode;

/**
 * 校验异常类
 * @author xfh
 * @since V1.00
 */
public class ValidateException extends BaseException
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2489661095537374374L;

    public ValidateException()
    {
    }

    public ValidateException(Throwable ex)
    {
        super(ex);
    }
    
    public ValidateException(String message)
    {
        super(message);
    }
    
    public ValidateException(String message, Throwable ex)
    {
        super(message, ex);
    }
    
    protected HttpCode getCode()
    {
        return HttpCode.PRECONDITION_FAILED;
    }
}

package com.ymd.cloud.common.exception;


import com.ymd.cloud.common.enumsSupport.HttpCode;

/**
 * 数据转换异常类
 *
 * @author xfh
 * @since V1.00
 */
public class DataParseException extends BaseException
{
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2920068362812787009L;
    
    public DataParseException()
    {
    }
    
    public DataParseException(Throwable ex)
    {
        super(ex);
    }
    
    public DataParseException(String message)
    {
        super(message);
    }
    
    public DataParseException(String message, Throwable ex)
    {
        super(message, ex);
    }
    
    @Override
    protected HttpCode getCode()
    {
        return HttpCode.INTERNAL_SERVER_ERROR;
    }
    
}

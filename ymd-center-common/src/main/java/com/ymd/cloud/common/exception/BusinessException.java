package com.ymd.cloud.common.exception;


import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.enumsSupport.HttpCode;

/**
 * 业务异常类
 *
 * @author xfh
 * @since V1.00
 */
public class BusinessException extends BaseException
{
    ErrorCodeEnum errorCode;
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1180441752646198840L;
    
    public BusinessException(Throwable ex)
    {
        super(String.valueOf(ex));
    }
    
    public BusinessException(String message)
    {
        super(message);
    }

    public BusinessException(HttpCode httpCode)
    {
        super(httpCode.msg());
    }
    public BusinessException(ErrorCodeEnum errorCode)
    {
        super(errorCode.msg());
        this.errorCode=errorCode;
    }
    public BusinessException(String message, Throwable ex)
    {
        super(message, ex);
    }

    protected HttpCode getCode()
    {
        return HttpCode.CONFLICT;
    }

    public ErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }
}

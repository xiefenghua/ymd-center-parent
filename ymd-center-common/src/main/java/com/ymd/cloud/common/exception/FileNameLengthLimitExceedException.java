package com.ymd.cloud.common.exception;

import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.enumsSupport.HttpCode;

/**
 * 文件名异常
 *
 * @author xfh
 * @since V1.00
 */
public class FileNameLengthLimitExceedException extends BusinessException
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6058456890048533281L;

    public FileNameLengthLimitExceedException(Throwable ex) {
        super(ex);
    }

    public FileNameLengthLimitExceedException(String message) {
        super(message);
    }

    public FileNameLengthLimitExceedException(HttpCode httpCode) {
        super(httpCode);
    }

    public FileNameLengthLimitExceedException(ErrorCodeEnum errorCode) {
        super(errorCode);
    }

    public FileNameLengthLimitExceedException(String message, Throwable ex) {
        super(message, ex);
    }

    @Override
    protected HttpCode getCode()
    {
        return HttpCode.FILE_NAME_LENGTH_LIMIT;
    }
    
}

package com.ymd.cloud.common.exception;


import com.ymd.cloud.common.enumsSupport.HttpCode;

/**
 * 文件大小异常
 *
 * @author xfh
 * @since V1.00
 */
public class FileSizeLimitExceededException extends BaseException
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8864058958502178602L;

    @Override
    protected HttpCode getCode()
    {
        return HttpCode.FILE_SIZE__LIMIT;
    }
    
}

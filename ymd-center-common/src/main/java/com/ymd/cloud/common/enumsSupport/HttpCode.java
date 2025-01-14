package com.ymd.cloud.common.enumsSupport;


/**
 * HTTP状态码
 * 
 */
public enum HttpCode{
    /**
     * SUCCESS
     */
    SUCCESS(1),

    OK(200),
    /**
     * 操作失败
     */
    FAIL(201),
    /**
     * USERNAME_OR_PASSWORD_ERROR
     */
    USERNAME_OR_PASSWORD_ERROR(202),
    
    PRECONDITION_FAILED(203),
    
    /**
     * 未授权
     */
    
    NOT_AUTH(403),
    
    /**
     * 未找到相应的请求
     */
    NOT_FOUND(404),
    /**
     * 系统内部异常
     */
    INTERNAL_SERVER_ERROR(500), 
    
    CONFLICT(207),
    
    /**
     * 文件名长度限制
     */
    FILE_NAME_LENGTH_LIMIT(600),
    
    /**
     * 文件大小限制
     */
    FILE_SIZE__LIMIT(601),


    /**
     * 系统暂无数据
     **/
    SYSTEM_HAS_NO_DATA(700),

    /**
     * 设备未注册
     **/
    DEVICE_NOT_REGISTER(701);

    
    private final Integer value;
    
    private HttpCode(Integer value)
    {
        this.value = value;
    }
    
    public Integer value()
    {
        return this.value;
    }
    
    public String msg()
    {
        return "HTTPCODE_" + this.value;
    }
    
    public String toString()
    {
        return this.value.toString();
    }
    
}

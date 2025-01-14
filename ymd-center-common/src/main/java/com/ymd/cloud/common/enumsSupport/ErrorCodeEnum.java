package com.ymd.cloud.common.enumsSupport;


/**
 * 错误码
 *
 * @author xingjing
 * @version [V1.00, 2020年07月28日]
 * @since V1.00
 */
public enum ErrorCodeEnum {
    TASK_NOT_EXIST("2001",""),
    TASK_POINT_NOT_EXIST("2002",""),
    ALARM_MSG_NOT_EXIST("3001",""),
    ALARM_MSG_HAS_DEAL("3002",""),
    PARAM_NEED("1001",""),
    SYS_ERROR_TRY("1002",""),
    SUCCESS("1","操作成功"),
    SYSTEM_ERROR_STATUS("500","系统错误，请联系管理员"),
    SYSTEM_PARAM_FORMAT_ERROR("-1","参数格式异常，请联系管理员"),
    MESSAGE_PARAM_LESS("-1","参数不全，请联系管理员"),
    MESSAGE_PARAM_ERROR("-1","参数不能为空，请联系管理员"),
    MESSAGE_PARAM_LENGTH_ERROR("-1","参数最大长度不能超过，请联系管理员"),
    FAIL("0","操作失败"),
    GL99990001("99990001", "授权微服务不在线,或者网络超时"),
    GL99990002("99990002", "appKey和appSecret参数不能为空"),
    GL99990003("99990003", "appKey或appSecret错误"),
    GL99990004("99990004", "参数异常"),
    GL99990005("99990005", "token参数不能为空"),
    GL99990006("99990006", "time参数不能为空"),
    GL99990007("99990007", "TokenInterceptor异常"),
    GL99990008("99990008", "token参数错误"),
    GL99990009("99990009", "token校验异常"),
    GL99990010("99990010", "参数解密异常"),
    GL99990011("99990011", "无此版本接口"),
    GL99990012("99990012", "sign参数不能为空"),
    GL99990013("99990013", "sign参数错误"),
    GL99990014("99990014", "微服务不在线,或者网络超时"),
    GL99990015("99990015", "数据库更新异常"),
    GL99990016("99990016", "参数错误"),
    GL99990017("99990017", "系统错误，请联系管理员"),
    GL99990018("99990018", "无效token"),
    GL99990019("99990019", "设备繁忙，请检查现场网络和设备，稍后再试!"),
    SDK4100001("4100001", "SDK 硬件版本ID为空"),
    ORD10000002("10000002", "查询出错"),
    ORD10000003("10000003", "获取用户信息失败"),
    GL999900019("999900019", "appId参数不能为空"),
    GL999900020("999900020", "手机号格式错误"),;
    private String code;
    private String msg;

    /**
     * Msg string.
     *
     * @return the string
     */
    public String msg() {
        return msg;
    }

    /**
     * Code int.
     *
     * @return the String
     */
    public String code() {
        return code;
    }

    ErrorCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * Gets enum.
     *
     * @param code the code
     *
     * @return the enum
     */
    public static ErrorCodeEnum getEnum(String code) {
        for (ErrorCodeEnum ele : values()) {
            if (ele.code().equals(code)) {
                return ele;
            }
        }
        return null;
    }

}

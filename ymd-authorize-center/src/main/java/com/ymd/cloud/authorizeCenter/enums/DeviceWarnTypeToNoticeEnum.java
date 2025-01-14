package com.ymd.cloud.authorizeCenter.enums;

/**
 * 设备告警类型 对应通知 拥有权限的的人员权限
 */
public enum DeviceWarnTypeToNoticeEnum {
    NOT_FIND(999,"未知告警","MANAGER"),
    LOW_POWER16(80,"电池电量低告警","USER"),
    LOW_POWER(128,"电池电量低告警","USER"),
    POWER_RECOVERY(201,"电量恢复通知","USER"),
    NO_CLOSE16(81,"未关门告警","USER"),
    NO_CLOSE(129,"未关门告警","USER"),
    DISMANTLE16(    82,"暴力拆卸告警","MANAGER"),
    DISMANTLE(130,"暴力拆卸告警","MANAGER"),
    NETWORK_FAIL16(    83,"网络连接失败告警","MANAGER"),
    NETWORK_FAIL(131,"网络连接失败告警","MANAGER"),
    NO_NETWORK(   132,"无可用网络告警","MANAGER"),
    NO_NETWORK16(84,"无可用网络告警","MANAGER"),
    PWD_FAIL(    133,"连续尝试错误密码错误告警","MANAGER"),
    PWD_FAIL16(    85,"连续尝试错误密码错误告警","MANAGER"),
    FINGERPRINT(134,"连续尝试错误指纹告警","MANAGER"),
    FINGERPRINT16(86,"连续尝试错误指纹告警","MANAGER"),
    BUTTON(134,"按键告警","OWNER"),
    BUTTON16(87,"按键告警","OWNER"),
    OFFLINE(404,"离线告警","MANAGER"),
    ONLINE(405,"设备恢复在线","MANAGER"),
    OPEN_LOCK_TYPE_FOR_KIDNAP(17,"挟持开锁告警","MANAGER"),

    JL_PERSON_NOTICE (1300,"陌生人比对自动入库后上报通知","MANAGER"),
    JL_PERSON_RESULT (1301,"特征值添加(异步)上报添加人员结果通知","MANAGER"),

    BATTERY_USE_BEYOND_LIMIT (2001,"门锁电池电量消耗异常","MANAGER"),

    ;
    public int warnType;
    public String desc;
    public String userType;


    private DeviceWarnTypeToNoticeEnum(Integer warnType, String desc, String userType) {
        this.warnType = warnType;
        this.userType = userType;
        this.desc = desc;
    }

    public static String getUserTypeByWarnType(Integer warnType){
        if (warnType == null)
            return null;
        for (DeviceWarnTypeToNoticeEnum enumDto : DeviceWarnTypeToNoticeEnum.values()) {
            if (enumDto.warnType==warnType)
                return enumDto.userType;
        }
        return null;
    }
    public static String getDescByWarnType(Integer warnType){
        if (warnType == null)
            return null;
        for (DeviceWarnTypeToNoticeEnum enumDto : DeviceWarnTypeToNoticeEnum.values()) {
            if (enumDto.warnType==warnType)
                return enumDto.desc;
        }
        return null;
    }


}

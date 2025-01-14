package com.ymd.cloud.authorizeCenter.enums;

/**
 * 门禁/门锁设备类型枚举
 */
public enum LockTypeDeviceAndCommModeEnum {
    LOCK_BLUETOOTH("1","00","1","蓝牙锁"),
    LOCK_LORA("2","00","2","LORA锁"),
    LOCK_NB("3","00","3","NBIOT锁"),
    LOCK_WIFI("4","00","4","WIFI锁"),
    LOCK_RS485("5","00","5","RS485锁"),
    LOCK_RF433("6","00","6","RF433锁"),
    LOCK_GSM("7","00","7","GSM锁"),
    DOOR_BLUETOOTH("9","01","1","蓝牙门禁"),
    DOOR_LORA("A","01","2","LORA门禁"),
    DOOR_NB("11","01","3","NB门禁"),
    DOOR_WIFI("12","01","4","WIFI门禁"),
    DOOR_RS485("14","01","5","RS485门禁"),
    DOOR_RF433("15","01","6","RF433门禁"),
    DOOR_GSM("16","01","7","GSM门禁"),
    DOOR_FACE("13","01","13","人脸门禁"),
    LIFT_BLUETOOTH16("29", "05", "1","蓝牙梯控"),
    LIFT_LORA16("2A", "05", "2","lora梯控"),
    LIFT_NB16("2B", "05", "3","NBIOT梯控"),
    LIFT_WIFI16("2C", "05", "4","WIFI梯控"),
    ;

    private String lockType;
    private String deviceMode;
    private String commMode;
    private String text;

    LockTypeDeviceAndCommModeEnum(String lockType, String deviceMode, String commMode, String text) {
        this.lockType = lockType;
        this.deviceMode=deviceMode;
        this.commMode=commMode;
        this.text = text;
    }
    public String getLockType() {
        return lockType;
    }
    public String getText() {
        return text;
    }
    public String getCommMode(){
        return commMode;
    }
    public String getDeviceMode(){
        return deviceMode;
    }

}

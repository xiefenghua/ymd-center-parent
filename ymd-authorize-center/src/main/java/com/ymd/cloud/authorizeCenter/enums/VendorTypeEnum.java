package com.ymd.cloud.authorizeCenter.enums;

/**
 * 锁模块枚举
 */
public enum VendorTypeEnum {
    RY("47A5AD64","ry","锐颖",3),
    BT("485A4254","bt","比特",1),
    JL("495A42B5","jl","巨龙",2),
    ;
    private String ventor;
    private String name;
    private String desc;
    private int type;
    VendorTypeEnum(String ventor, String name, String desc, int type) {
        this.ventor=ventor;
        this.name = name;
        this.desc = desc;
        this.type = type;
    }
    public String getVentor() {
        return ventor;
    }
    public String getName(){
        return name;
    }
    public String getDesc(){
        return desc;
    }
    public int getType(){
        return type;
    }

    public static int getTypeByVentor(String ventor) {
        if (ventor == null)
            return 0;
        for (VendorTypeEnum anEnum : VendorTypeEnum.values()) {
            if (anEnum.getVentor().equals(ventor))
                return anEnum.getType();
        }
        return 0;
    }
    public static String getVentorByType(int type) {
        for (VendorTypeEnum anEnum : VendorTypeEnum.values()) {
            if (anEnum.getType()==type)
                return anEnum.getVentor();
        }
        return null;
    }


    public static String getDescByVentor(String ventor) {
        if (ventor == null)
            return "";
        for (VendorTypeEnum anEnum : VendorTypeEnum.values()) {
            if (anEnum.getVentor().equals(ventor))
                return anEnum.getDesc();
        }
        return "";
    }
}

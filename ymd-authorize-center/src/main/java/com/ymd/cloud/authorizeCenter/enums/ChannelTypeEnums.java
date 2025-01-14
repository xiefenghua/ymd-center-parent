package com.ymd.cloud.authorizeCenter.enums;

public class ChannelTypeEnums {
    public static String MODULETYPE_01="1";//密码
    public static String MODULETYPE_02="2";//刷卡
    public static String MODULETYPE_03="3";//红外
    public static String MODULETYPE_04="4";//指纹
    public static String MODULETYPE_05="5";//指静脉
    public static String MODULETYPE_06="6";//人脸
    public static String MODULETYPE_07="7";//内部
    public static String MODULETYPE_08="8";//手机

    public static String CHANNELTYPE_101="101";//管理员密码
    public static String CHANNELTYPE_102="102";//我的密码
    public static String CHANNELTYPE_103="103";//随身码
    public static String CHANNELTYPE_104="104";//一次性密码
    public static String CHANNELTYPE_105="105";//劫持码
    public static String CHANNELTYPE_201="201";//身份证
    public static String CHANNELTYPE_202="202";//钥匙卡
    public static String CHANNELTYPE_203="203";//手环
    public static String CHANNELTYPE_204="204";//蓝牙钥匙
    public static String CHANNELTYPE_205="205";//UID卡
    public static String CHANNELTYPE_206="206";//华为钱包
    public static String CHANNELTYPE_401="401";//身份证指纹
    public static String CHANNELTYPE_402="402";//身份证补录指纹
    public static String CHANNELTYPE_403="403";//挟制指纹
    public static String CHANNELTYPE_404="404";//普通指纹
    public static String CHANNELTYPE_501="501";//指静脉
    public static String CHANNELTYPE_601="601";//人脸
    public static String CHANNELTYPE_801="801";//app

    //用于判断系统类型程序判断
    public static String getModuleTypeMapName(String moduleType){
        String name="";
        if(MODULETYPE_01.equals(moduleType)) {
            name="密码";
        }else if(MODULETYPE_02.equals(moduleType)) {
            name="刷卡";
        }else if(MODULETYPE_03.equals(moduleType)) {
            name="红外";
         }else if(MODULETYPE_04.equals(moduleType)) {
            name="指纹";
         }else if(MODULETYPE_05.equals(moduleType)) {
            name="指静脉";
         }else if(MODULETYPE_06.equals(moduleType)) {
            name="人脸";
         }else if(MODULETYPE_07.equals(moduleType)) {
            name="内部";
         }else if(MODULETYPE_08.equals(moduleType)) {
            name="手机";
        }
        return name;
    }

    public static String getChannelTypeName(String channelType){
        String name="";
        if(CHANNELTYPE_101.equals(channelType)) {
            name="管理员密码";
        }else if(CHANNELTYPE_102.equals(channelType)) {
            name="我的密码";
        }else if(CHANNELTYPE_103.equals(channelType)) {
            name="随身码";
        }else if(CHANNELTYPE_104.equals(channelType)) {
            name="一次性密码";
        }else if(CHANNELTYPE_105.equals(channelType)) {
            name="劫持码";
        }else if(CHANNELTYPE_201.equals(channelType)) {
            name="身份证";
        }else if(CHANNELTYPE_202.equals(channelType)) {
            name="钥匙卡";
        }else if(CHANNELTYPE_203.equals(channelType)) {
            name="手环";
        }else if(CHANNELTYPE_204.equals(channelType)) {
            name="蓝牙钥匙";
        }else if(CHANNELTYPE_205.equals(channelType)) {
            name="UID卡";
        }else if(CHANNELTYPE_206.equals(channelType)) {
            name="华为钱包";
        }else if(CHANNELTYPE_401.equals(channelType)) {
            name="身份证指纹";
        }else if(CHANNELTYPE_402.equals(channelType)) {
            name="身份证补录指纹";
        }else if(CHANNELTYPE_403.equals(channelType)) {
            name="挟制指纹";
        }else if(CHANNELTYPE_404.equals(channelType)) {
            name="普通指纹";
        }else if(CHANNELTYPE_501.equals(channelType)) {
            name="指静脉";
        }else if(CHANNELTYPE_601.equals(channelType)) {
            name="人脸";
        }else if(CHANNELTYPE_801.equals(channelType)) {
            name="app";
        }



        return name;
    }
}

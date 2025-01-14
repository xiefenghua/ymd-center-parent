package com.ymd.cloud.authorizeCenter.enums;

/**
 * @author Administrator
 * @Title: MqMsg
 * @ProjectName ymd-platform-parent
 * @Description: TODO
 * @date 2020/4/2514:10
 */
public enum MqMsg {
    ADD_USER_AUTH(1,"添加或修改授权信息"),
    DEL_USER_AUTH(2,"删除授权"),

    FACE_FEATURE_UPLOAD(3,"人脸特征值文件推送"),
    USER_ENTER_RECORD(4,"人员出入记录"),
    ID_CARD_INFO_UPLOAD(5,"身份证信息上传"),

    ADD_PWD_CMD(6,"添加密码授权指令"),
    DEL_PWD_CMD(7,"删除密码授权指令"),

    ADD_CARD_CMD(8,"添加卡片授权指令"),
    DEL_CARD_CMD(9,"删除卡片授权指令"),

    REMOTE_OPEN_DOOR_CMD(10,"远程开门指令"),
    SET_VISITOR_CODE_CMD(11,"下发访客码指令"),
    SET_TIME_CODE_CMD(12,"同步时间指令"),
    RESTART_CMD(13,"重启设备指令");

    private int cmd;
    private String des;
    MqMsg(int cmd, String des){
        this.cmd=cmd;
        this.des=des;
    }
    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}

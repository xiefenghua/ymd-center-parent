package com.ymd.cloud.authorizeCenter;

import com.ymd.cloud.authorizeCenter.util.StringUtilBle;
import com.ymd.cloud.common.utils.AESUtil;
import java.util.HashMap;
import java.util.Map;

public class SettingDataOldTest {
    public static String getSetting(String settings) {

        String detail = "";
        if (settings!=null && (settings.length() == 32 || settings.length() == 32 * 4)) {
            byte[] settingsData = StringUtilBle.hexStringToBytes(settings);


            int id = Integer.parseInt(settings.substring(0, 2), 16);
            if (id == 0) {
                detail += "配置数据无效";
            } else {
                detail += "配置id：" + id + "\n";

                String[] setttingTitle = new String[]{
                        "门锁配置字",
                        "模块配置字",
                        "运行配置字",
                        "密码配置字",
                        "系统配置字",
                        "指纹配置字",
                        "同步配置字",
                        "刷卡配置字",
                        "权限配置字",
                        "限时功能",
                        "服务有效期",
                        "人脸配置字",
                        "锁舌配置字",
                        "雷达配置字",
                        "电机配置字"
                };
                //门锁配置字
                detail += "\n" + setttingTitle[0] + "\n";
                String lockSetting = StringUtilBle.byteToBit(settingsData[1]);
                Map<String, Integer> lockSettingMap = new HashMap<>();
                String[] lockSettingTitle = new String[]{
                        "门锁反锁状态检测", "机械钥匙开门检测", "门锁上提状态检测", "网络异常告警提示音", "门锁开关状态检测",
                        "门锁防拆报警检测", "手动回锁", "未关门告警提示音"
                };
                String[] lockSettingValue = new String[]{"禁用", "启用"};
                for (int i = 0; i < lockSetting.length(); i++) {
                    int value = Integer.valueOf(lockSetting.substring(i, i + 1));
                    if (!StringUtilBle.isEmpty(lockSettingTitle[i])) {
                        lockSettingMap.put(lockSettingTitle[i], value);
                        detail += lockSettingTitle[i] + ":" + lockSettingValue[value] + "\n";
                    }
                }
                //模块配置字
                detail += "\n" + setttingTitle[1] + "\n";
                String modelSetting = StringUtilBle.byteToBit(settingsData[2]);
                Map<String, Integer> modelSettingMap = new HashMap<>();
                String[] modelSettingTitle = new String[]{"", "", "", "指纹模块", "红外模块", "刷卡模块", "键盘模块", "通信模块"};
                String[] modelSettingValue = new String[]{"启用", "禁用"};
                for (int i = 0; i < modelSetting.length(); i++) {
                    int value = Integer.valueOf(modelSetting.substring(i, i + 1));
                    if (!StringUtilBle.isEmpty(modelSettingTitle[i])) {
                        modelSettingMap.put(modelSettingTitle[i], value);
                        detail += modelSettingTitle[i] + ":" + modelSettingValue[value] + "\n";
                    }
                }
                //运行配置字
                detail += "\n" + setttingTitle[2] + "\n";
                String actionSetting = StringUtilBle.byteToBit(settingsData[3]);
                Map<String, Integer> actionSettingMap = new HashMap<>();
                String[] actionSettingTitle = new String[]{"设备运行模式", "", "", "指纹运行模式",
                        "红外运行模式", "刷卡运行模式", "键盘运行模式", "蓝牙运行模式"};
                String[] actionSettingValue = new String[]{"智能模式", "最低功耗"};
                for (int i = 0; i < actionSetting.length(); i++) {
                    int value = Integer.valueOf(actionSetting.substring(i, i + 1));
                    if (!StringUtilBle.isEmpty(actionSettingTitle[i])) {
                        actionSettingMap.put(actionSettingTitle[i], value);
                        detail += actionSettingTitle[i] + ":" + actionSettingValue[value] + "\n";
                    }
                }
                //密码配置字
                detail += "\n" + setttingTitle[3] + "\n";
                String pwdSetting = StringUtilBle.byteToBit(settingsData[4]);
                Map<String, Integer> pwdSettingMap = new HashMap<>();
                String[] pwdSettingTitle = new String[]{
                        "总开关", "", "访客码（6位)", "访客码（15位）", "劫持码", "随身码", "期限码", "管理员密码"
                };
                String[] pwdSettingValue = new String[]{"启用", "禁用"};
                for (int i = 0; i < pwdSetting.length(); i++) {
                    int value = Integer.valueOf(pwdSetting.substring(i, i + 1));
                    if (!StringUtilBle.isEmpty(pwdSettingTitle[i])) {
                        pwdSettingMap.put(pwdSettingTitle[i], value);
                        detail += pwdSettingTitle[i] + ":" + pwdSettingValue[value] + "\n";
                    }
                }

                //系统配置字
                detail += "\n" + setttingTitle[4] + "\n";
                String sysSetting = StringUtilBle.byteToBit(settingsData[5]);
                Map<String, Integer> sysSettingMap = new HashMap<>();
                String[] sysSettingTitle = new String[]{"回锁时间", "音量", "蓝牙发射功率"};
                int lockBackDuring = Integer.parseInt(sysSetting.substring(0, 3), 2);
                sysSettingMap.put(sysSettingTitle[0], lockBackDuring);
                String[] duringValues = new String[]{
                        "默认", "250ms", "500ms", "1000ms", "2000ms", "3500ms", "5500ms", "8000ms"
                };
                detail += sysSettingTitle[0] + ":" + duringValues[lockBackDuring] + "\n";
                int soundSetting = Integer.parseInt(sysSetting.substring(3, 5), 2);
                sysSettingMap.put(sysSettingTitle[1], soundSetting);
                String[] soundValues = new String[]{
                        "大（默认）", "中", "小", "静音"
                };
                detail += sysSettingTitle[1] + ":" + soundValues[soundSetting] + "\n";
                int btPower = Integer.parseInt(sysSetting.substring(5, 8), 2);
                sysSettingMap.put(sysSettingTitle[2], btPower);
                String[] btPowerValues = new String[]{"默认（-12dBm）", "-20dBm", "-16dBm", "-12dBm"
                        , "-8dBm", "-4dBm", "0dBm", "4dBm"};
                detail += sysSettingTitle[2] + ":" + btPowerValues[btPower] + "\n";

                //指纹配置字
                detail += "\n" + setttingTitle[5] + "\n";
                String fingerSetting = StringUtilBle.byteToBit(settingsData[6]);
                Map<String, Integer> fingerSettingMap = new HashMap<>();
                String[] fingerSettingTitle = new String[]{"总开关", "", "", "", "", "劫持指纹", "期限指纹", "管理员指纹"};
                String[] fingerSettingValue = new String[]{"启动", "禁用"};
                for (int i = 0; i < fingerSetting.length(); i++) {
                    int value = Integer.valueOf(fingerSetting.substring(i, i + 1));
                    if (!StringUtilBle.isEmpty(fingerSettingTitle[i])) {
                        fingerSettingMap.put(fingerSettingTitle[i], value);
                        detail += fingerSettingTitle[i] + ":" + fingerSettingValue[value] + "\n";
                    }
                }
                //同步配置字
                detail += "\n" + setttingTitle[6] + "\n";
                String sycnSetting = StringUtilBle.byteToBit(settingsData[7]);
                String[] syscModelValue = new String[]{"禁用", "分钟", "小时", "天"};
                int syscModel = Integer.parseInt(sycnSetting.substring(0, 2), 2);
                int sycsData = Integer.parseInt(sycnSetting.substring(2, 8), 2);
                if (syscModel == 0) {
                    detail += "同步配置：" + syscModelValue[syscModel] + "\n";
                } else {
                    detail += "同步配置：" + sycsData + syscModelValue[syscModel] + "\n";
                }
                //刷卡配置字
                detail += "\n" + setttingTitle[7] + "\n";
                String cardSetting = StringUtilBle.byteToBit(settingsData[8]);
                Map<String, Integer> cardSettingMap = new HashMap<>();
                String[] cardSettingTitle = new String[]{"滚动码", "远程解码", "身份证", "钥匙卡"};
                String[] cardSettingValue = new String[]{"禁用", "启用"};
                String[] cardSettingValue1 = new String[]{"启用", "禁用"};

                for (int i = 0; i < cardSettingTitle.length; i++) {
                    int value = Integer.valueOf(cardSetting.substring(i, i + 1));
                    if (!StringUtilBle.isEmpty(cardSettingTitle[i])) {
                        cardSettingMap.put(cardSettingTitle[i], value);
                        if (i == 0 || i == 1) {
                            detail += cardSettingTitle[i] + ":" + cardSettingValue[value] + "\n";
                        }
                        if (i == 2 || i == 3) {
                            detail += cardSettingTitle[i] + ":" + cardSettingValue1[value] + "\n";
                        }
                    }
                }
                int cardInterval = Integer.parseInt(cardSetting.substring(4, 8), 2);
                if (cardInterval == 0) {
                    detail += "刷卡间隔时间:默认" + "\n";
                } else {
                    detail += "刷卡间隔时间:" + 250 * cardInterval + "ms" + "\n";
                }
                //权限配置字
                detail += "\n" + setttingTitle[8] + "\n";
                String accessSetting = StringUtilBle.byteToBit(settingsData[9]);
                String[] accessSettingTitle = new String[]{"双人模式", "勿扰模式"};
                String[] doubleModeSettingValue = new String[]{"禁用", "启用"};
                String[] disturbModeSettingValue = new String[]{"禁用", "仅管理员", "仅管理员+普通用户", "仅管理员+普通用户+保洁"};
                detail += accessSettingTitle[0] + ":" + doubleModeSettingValue[Integer.parseInt(accessSetting.substring(0, 1))] + "\n";
                detail += accessSettingTitle[1] + ":" + disturbModeSettingValue[Integer.parseInt(accessSetting.substring(4, 8), 2)] + "\n";

                //限时配置字
                detail += "\n" + setttingTitle[9] + "\n";

                String timeLimitSetting = StringUtilBle.byteToBit(settingsData[11]) + StringUtilBle.byteToBit(settingsData[10]);
                String[] timeLimitTitle = new String[]{"运行日历", "运行时间"};
                String[] timeLimitDataValue = new String[]{"禁用", "工作日", "双休日", "每天"};
                int limitValue = Integer.parseInt(timeLimitSetting.substring(0, 2), 2);
                detail += timeLimitTitle[0] + ":" + timeLimitDataValue[limitValue] + "\n";
                if (limitValue != 0) {
                    detail += (timeLimitTitle[1] + ":" + Integer.parseInt(timeLimitSetting.substring(11, 16), 2) + ":" +
                            ((Integer.parseInt(timeLimitSetting.substring(10, 11)) == 0) ? "00" : "30")
                            + "至" + Integer.parseInt(timeLimitSetting.substring(3, 8), 2)
                            + ":" + ((Integer.parseInt(timeLimitSetting.substring(2, 3)) == 0) ? "00" : "30"));
                }

                //有效期
                byte[] validityTime = new byte[4];
                System.arraycopy(settingsData, 12, validityTime, 0, 4);
                String validityValue = "";
                if (StringUtilBle.bytesToHexString(validityTime).equalsIgnoreCase("00000000")) {
                    validityValue = "长期有效" + "\n";
                } else {
                    validityValue = AESUtil.parseHexStr2Byte(StringUtilBle.bytesToHexString(validityTime)) + "\n";
                }
                detail += "\n" + setttingTitle[10] + ":" + validityValue + "\n";

                if (settings.length() == 32 * 4) {
                    //人脸配置字
                    String faceSetting = StringUtilBle.byteToBit(settingsData[16]);
                    detail += "\n" + setttingTitle[11] + "\n";
                    String[] faceSettingTitle = new String[]{"总开关", "", "", "", "一次性刷脸授权", "劫持刷脸授权", "期限刷脸授权", "管理员刷脸授权"};
                    String[] faceSettingValue = new String[]{"启用", "禁用"};
                    for (int i = 0; i < faceSetting.length(); i++) {
                        int value = Integer.parseInt(faceSetting.substring(i, i + 1));
                        if (!StringUtilBle.isEmpty(faceSettingTitle[i])) {
                            fingerSettingMap.put(faceSettingTitle[i], value);
                            detail += faceSettingTitle[i] + ":" + faceSettingValue[value] + "\n";
                        }
                    }
                    //锁舌配置字
                    String lockTongueSetting = StringUtilBle.byteToBit(settingsData[17]);
                    detail += "\n" + setttingTitle[12] + "\n";
                    String[] lockTongueSettingTitle = new String[]{"主舌弹出使能", "主舌弹出时间", "斜舌弹出使能", "斜舌弹出时间"};
                    String[] mainTime = new String[]{"15秒（默认)", "10秒", "20秒", "30秒", "45秒", "60秒", "150秒", "300秒"};
                    String[] secondTime = new String[]{"3秒（默认)", "2秒", "2.5秒", "3.5秒", "4秒", "5秒", "8秒", "10秒"};
                    String mainStatus = lockTongueSetting.substring(0, 1);
                    detail += lockTongueSettingTitle[0] + ":" + (mainStatus.equalsIgnoreCase("0") ? "禁用" : "启用") + "\n";
                    if (mainStatus.equalsIgnoreCase("1")) {
                        detail += lockTongueSettingTitle[1] + ":" + mainTime[Integer.parseInt(lockTongueSetting.substring(1, 4), 2)] + "\n";
                    }
                    String secondStatus = lockTongueSetting.substring(4, 5);
                    detail += lockTongueSettingTitle[2] + ":" + (secondStatus.equalsIgnoreCase("0") ? "禁用" : "启用") + "\n";
                    if (secondStatus.equalsIgnoreCase("1")) {
                        detail += lockTongueSettingTitle[3] + ":" + secondTime[Integer.parseInt(lockTongueSetting.substring(5, 8), 2)] + "\n";
                    }
                    //雷达配置字
                    String radarSetting = StringUtilBle.byteToBit(settingsData[18]);
                    detail += "\n" + setttingTitle[13] + "\n";
                    String[] radarValue = new String[]{"1米（默认)", "0.5米", "0.8米", "1.2米"};
                    detail += "雷达识别距离：" + radarValue[Integer.parseInt(radarSetting.substring(6, 8), 2)] + "\n";
                    //电机配置字
                    String machineSetting = StringUtilBle.byteToBit(settingsData[19]);
                    detail += "\n" + setttingTitle[14] + "\n";

                    String[] machineValue = new String[]{"正向", "反向"};
                    detail += "电机方向：" + machineValue[Integer.parseInt(machineSetting.substring(7, 8))] + "\n";

                }
            }


        }
        return detail;
    }


    public static void main(String[] args) {


        System.out.println(getSetting("0B200086052B000070000A4B00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));

    }
}

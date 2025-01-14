package com.ymd.cloud.authorizeCenter.model.lock;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName(value = "t_hzbit_lock",schema = "`lock`")
public class Lock implements Serializable {
    /**
     * 锁ID
     */
    @TableId(value="lockid",type = IdType.INPUT)
    private String lockid;

    /**
     * 锁名称
     */
    @TableField( "lockName")
    private String lockname;

    /**
     * 锁类型
     */
    @TableField( "lockType")
    private String locktype;

    /**
     * 电量
     */
    private String battery;


    /**
     * 软件版本
     */
    @TableField( "softVersion")
    private String softversion;
    /**
     * 人脸模组固件版本号
     */
    @TableField( "firmwareVersion")
    private String firmwareVersion;
    /**
     * 人脸模组特征值算法版本
     */
    @TableField( "featureVersion")
    private String featureVersion ;
    /**
     * 特征值厂商
     */
    @TableField("feature_vendor")
    private String featureVendor;
    /**
     * 状态1.正常0维护
     */
    private String state;

    /**
     * 根据锁的经纬度获取的地址
     */
    private String address;

    /**
     * 锁的MAC
     */
    private String mac;

    /**
     * 厂商
     */
    private String ventor;

    /**
     * 校验位
     */
    @TableField( "checkByte")
    private String checkbyte;

    /**
     * 密码表
     */
    @TableField( "pwdTable")
    private String pwdtable;

    /**
     * 二维码内容
     */
    @TableField( "qrContent")
    private String qrcontent;

    /**
     * 用户自定义地址(安装地址)
     */
    private String address2;

    /**
     * 产生时间
     */
    @TableField( "createTime")
    private String createtime;

    /**
     * 公安门牌二维码
     */
    @TableField( "addressQr")
    private String addressqr;

    /**
     * 经度
     */
    private String longtitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 用户自定义地址ID
     */
    @TableField( "addrCode")
    private String addrcode;

    /**
     * 是否需要实名校验
     */
    @TableField( "authIdcardNeedRealName")
    private String authidcardneedrealname;

    /**
     * 办公室模式
     */
    @TableField( "officeMode")
    private String officemode;

    /**
     * 配置数据
     */
    @TableField( "settingsData")
    private String settingsdata;

    /**
     * 添加人
     */
    @TableField( "addUser")
    private String adduser;

    /**
     * 硬件版本
     */
    @TableField( "hdVer")
    private String hdver;

    private String mcu;

    /**
     * 是否受公安监管1.是0.否
     */
    @TableField( "isSupervise")
    private String issupervise;

    /**
     * 具体小区单元编号
     */
    @TableField( "locationCode")
    private String locationcode;

    @TableField( "is_public")
    private String isPublic;//是否公有设备 0私有，1公有。
    /**
     * 幢
     */
    private String building;

    /**
     * 单元
     */
    private String unit;

    /**
     * 室
     */
    private String house;

    /**
     * 具体房间
     */
    private String room;

    /**
     * 场强
     */
    private String rssi;

    /**
     * 1.是 0.否
     */
    @TableField( "isParent")
    private String isparent;

    /**
     * 是否收费
     */
    @TableField( "isCharge")
    private String ischarge;

    @TableField( "lockAttribute")
    private String lockattribute;

    @TableField( "activeDays")
    private String activedays;

    /**
     * 代理商编码
     */
    @TableField( "agentCode")
    private String agentcode;

    /**
     * WIFI 锁消息上报时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField( "upload_time")
    private Date uploadTime;

    @TableField(exist = false)
    private String startTime;

    @TableField(exist = false)
    private String endTime;

    @TableField( "orgId")
    private String orgid;

    @TableField( "orgName")
    private String orgname;

    //update 2021-09-19 门禁/门锁设备入库增加设备类型和设备通讯模式
    //设备类型 见ymd_iot中数据字典device_mode
    @TableField("deviceMode")
    private String deviceMode;
    //通讯方式 见ymd_iot中数据字典comm_mode
    @TableField("commMode")
    private String commMode;
    /**
     * 初装免费天数
     */
    @TableField(exist = false)
    private Integer freeDays;
    /**
     * 服务费单价
     */
    @TableField(exist = false)
    private Double serviceUnitprice;

    @TableField(exist = false)
    private String deviceType;
    @TableField(exist = false)
    private String deviceName;
    @TableField(exist = false)
    private String deviceMac;
    @TableField(exist = false)
    private String organId;
    private Boolean onlineStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rksj;
    @TableField(exist = false)
    private String lockTypeDesc;
    @TableField(exist = false)
    private String ownerName;//所有者
    @TableField(exist = false)
    private String ownerId;//所有者id
    @TableField(exist = false)
    private String adminName;//管理者
    @TableField(exist = false)
    private Integer userCount;//使用者数量
    @TableField(exist = false)
    private List<String> lockTypes;
    @TableField(exist = false)
    private String gateMac;//网关MAC
    @TableField(exist = false)
    private String roomId;
    @TableField(exist = false)
    private String houseId;
    @TableField(exist = false)
    private List<String> orgIds;
    @TableField(exist=false)
    private String isBind;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date onlineTime;
    @TableField(exist=false)
    private String deviceGroupName;
    @TableField(exist = false)
    private String isShowAll;//是否显示所有

    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String idcardNo;
    /**
     * 实名人脸照片
     */
    @TableField(exist = false)
    private String ossUrl;
    /**
     * 是否修改所有者（1是，0否）
     */
    @TableField(exist = false)
    private String alterType;
    @TableField("auth_interval")
    private Integer authInterval;

    /**
     * 一标三实
     */
    @TableField("one_of_three")
    private String oneOfThree;
    /**
     * 第三方房间编号
     */
    @TableField("third_room_no")
    private String thirdRoomNo;

    @TableField("house_struct_path")
    private String houseStructPath;

    @TableField(exist = false)
    private String virtualOrgId;
    @TableField(exist = false)
    private int isHealthCode=0;
    @TableField(exist = false)
    private int isRegistration=0;
    @TableField(exist = false)
    private int isCardNoDeCode=0;
}

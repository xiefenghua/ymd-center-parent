package com.ymd.cloud.authorizeCenter.model.lock;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备基础信息表(EDeviceBaseInfo)实体类
 *
 * @author zgy
 * @since 2020-05-15 17:14:23
 */
@Data
@Accessors(chain = true)
@TableName(value = "e_device_base_info")
public class EDeviceBaseInfo implements Serializable {
    /**
    * 设备ID：(厂商代码9+设备大类2+随机)
    */
    @TableId(value="device_id",type = IdType.INPUT)
    private String deviceId;
    /**
    * 产品ID
    */
    private Integer productId;
    /**
    * 设备MAC
    */
    private String deviceMac;
    /**
    * 设备名称
    */
    private String deviceName;
    /**
    * 设备类型
    */
    private Integer deviceType;
    /**
    * 设备型号
    */
    private String deviceModel;
    /**
     * 设备型号字符
     */
    private String deviceModelStr;
    /**
    * 组织ID
    */
    private String organId;
    /**
    * 厂商编码
    */
    private String ventor;
    /**
    * 硬件版本（设备生产时的真实版本）
    */
    private String hardVersion;
    /**
    * 硬件升级版本（设备固件升级时使用的版本）
    */
    private String hardUpgradeVersion;
    /**
    * 通讯方式：除了蓝牙外，其它通讯方式见通讯方式字典表
    */
    private Integer communModeId;
    /**
    * 通讯硬件版本
    */
    private String communHardVer;
    /**
    * 通讯软件版本
    */
    private String communSoftVer;
    /**
    * 是否是公有设备 0私有，1公有
    */
    private String isPublic;
    /**
    * 经度
    */
    private String longtitude;
    /**
    * 纬度
    */
    private String latitude;
    /**
    * 安装详细地址
    */
    private String address;
    /**
    * 安装区域编码
    */
    private String areaCode;
    /**
    * 质保时长
    */
    private Float qaDur;
    /**
    * 管理者( 组织或个人)
    */
    private String adminUser;
    /**
    * 所有者( 组织或个人)
    */
    private String ownerUser;
    /**
    * 设备状态:  0 未激活  1.已激活（正常）   2 停机（或维护禁用）
    */
    private String state;
    /**
    * 入库时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rksj;
    /**
    * 入库人员ID
    */
    private String rkUser;
    /**
    * 激活时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date activeSj;
    /**
    * 激活人员ID
    */
    private String activeUserId;
    /**
    * 初次绑定时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date firstBindSj;
    /**
    * 最近绑定时间
    */
    private Date curBindSj;
    /**
    * 一级代理商编码（2位国际码 + 6位邮编 + 2位序号）
    */
    private String primaryAgentDm;
    /**
    * 二级代理商编码（一级代理商编码 + 2位序号）
    */
    private String secondAgentDm;
    /**
    * 三级代理商编码（二级代理商编码 + 2位序号）
    */
    private String thirdAgentDm;
    /**
    * 四级代理商编码（三级代理商编码 + 2位序号）
    */
    private String fourAgentDm;
    /**
    * 一级代理价格
    */
    private Double firstUnitprice;
    /**
    * 二级代理价格
    */
    private Double secondUnitprice;
    /**
    * 三级代理价格
    */
    private Double thirdUnitprice;
    /**
    * 四级代理价格
    */
    private Double fourUnitprice;
    /**
    * 是否收费: 0 免费， 1 收费
    */
    private String isCharge;
    /**
    * 服务有效期截止日期
    */
    private Date expires;
    /**
    * 停机保号起始日期
    */
    private Date pauseDate;
    /**
    * 停机保号最大次数
    */
    private Integer pauseMaxTimes;
    /**
    * 停机保号累计次数
    */
    private Integer pauseCumsumTimes;
    /**
    * 停机保号折算率
    */
    private Float pauseRate;
    /**
    * 初装免费天数
    */
    private Integer initFreeDays;
    /**
    * 到期延长天数
    */
    private Integer extendDays;
    /**
    * 服务费单价
    */
    private Double serviceUnitprice;

    @TableField(exist=false)
    private String deviceSecret;
    @TableField(exist=false)
    private String organName;

    @TableField(exist=false)
    private Boolean onlineStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date onlineTime;

    @TableField(exist=false)
    Map<String,String> onlineTimeStandardMap=new HashMap<>();
    @TableField(exist=false)
    private List<String> orgIds;
    @TableField(exist=false)
    private String battery;

    @TableField(exist=false)
    private List<String> deviceIds;

    @TableField(exist=false)
    private String isBind;
    @TableField(exist=false)
    private String deviceTypes;
    @TableField(exist=false)
    private List<String> collectDeviceTypes;
    @TableField(exist=false)
    private String lockType;
    @TableField(exist=false)
    private List<String> lockTypes;
    @TableField(exist = false)
    private String building;
    @TableField(exist = false)
    private String room;
    @TableField(exist = false)
    private String floor;

    @TableField(exist = false)
    private String deviceMode;
    @TableField(exist = false)
    private String commMode;

    @TableField(exist = false)
    private String brakeMac;
    @TableField(exist = false)
    private String collectorType;
    @TableField(exist = false)
    private String brakeHost;
}

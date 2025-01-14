package com.ymd.cloud.authorizeCenter.model.lock;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "t_hzbit_lock_gateway")
@Data
public class LockGateWay implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField( "gateId")
    private String gateid;

    private String mac;

    @TableField( "gateName")
    private String gatename;

    private String time;

    @TableField( "productId")
    private String productid;

    @TableField( "deviceSecret")
    private String devicesecret;

    /**
     * 1.在线 0.离线
     */
    private String status;

    private String address;

    private String model;

    @TableField( "loraChannel")
    private Integer lorachannel;

    @TableField( "loraCfgByte")
    private Integer loracfgbyte;

    /**
     * 最新上报数据时间
     */
    @TableField( "updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;

    /**
     * 软件版本
     */
    @TableField( "softVersion")
    private String softversion;
}
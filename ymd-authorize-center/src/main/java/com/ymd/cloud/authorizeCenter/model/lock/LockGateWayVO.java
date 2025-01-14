package com.ymd.cloud.authorizeCenter.model.lock;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈?〉<br>
 *
 * @author hhd668@163.com
 * @create 2020/8/7
 * @since 1.0
 */
@Data
public class LockGateWayVO implements Serializable {
    private String gateid;
    private String mac;

    private String gatename;

    private String time;

    private String productid;

    private String devicesecret;

    /**
     * 1.在线 0.离线
     */
    private String status;

    private String address;

    private String model;

    private Integer lorachannel;

    private Integer loracfgbyte;

    /**
     * 最新上报数据时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatetime;

    /**
     * 系统时间
     */
    private String sysTime;

    /**
     * 软件版本
     */
    private String softversion;

    private Integer num;

    private String gateIp;
}

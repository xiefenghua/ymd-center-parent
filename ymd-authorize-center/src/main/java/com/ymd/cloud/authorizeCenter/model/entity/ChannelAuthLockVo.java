package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelAuthLockVo implements Serializable {
    private String lockId;//必填

    private String lockName;//从数据库查询，如果数据库查询为空则默认用lockMac
    private String lockMac; //从数据库查询获取无需传
    private Integer useLevel=0;//默认0
    private String lockType;//从数据库查询获取无需传
}

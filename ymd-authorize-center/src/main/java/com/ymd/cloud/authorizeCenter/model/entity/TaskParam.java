package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class TaskParam implements Serializable {
    private String taskNo;
    private String startDate;
    private String endDate;
    private String orgId;
    private String operator;
    private String taskType;
    private Integer taskStatus;////0=提交 1=完成  2=执行中 3=失败 4=异常
    private String userTarget;
    private String exceptionDesc;
    private Integer status;//status 0=结果失败  1=结果成功
    private String channelType;
    private String lockId;
    private String mac;

}

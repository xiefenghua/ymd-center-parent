package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthParam implements Serializable {
    private String userAccount;
    private String userName;
    private String authChannelModuleType;
    private String authChannelType;
    private String startTime;
    private String endTime;
    private String lockId;
    private String authChannelValue;
    private Map<String, Object> params;
    private String ids;
    private Long id;
    private Integer sync;
    private Integer uploadStatus;
    private Integer freezeStatus;
}

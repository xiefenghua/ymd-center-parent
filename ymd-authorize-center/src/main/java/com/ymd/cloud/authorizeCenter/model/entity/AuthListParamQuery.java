package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthListParamQuery implements Serializable {
    private String userAccount;
    private String userName;
    private String authChannelModuleType;
    private String authChannelType;
    private Date startDate;
    private Date endDate;
    private String lockId;
    private String authChannelValue;
}

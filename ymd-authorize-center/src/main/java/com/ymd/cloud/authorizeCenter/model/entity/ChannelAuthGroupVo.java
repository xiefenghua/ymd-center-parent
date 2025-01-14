package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelAuthGroupVo implements Serializable {
    private String userGroupName;
    private Long userGroupId;
    private String deviceGroupName;
    private Long deviceGroupId;
}

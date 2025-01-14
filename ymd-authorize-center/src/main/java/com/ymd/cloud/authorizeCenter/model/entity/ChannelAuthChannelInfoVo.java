package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelAuthChannelInfoVo implements Serializable {
    private String authChannelModuleType;//必填
    private String authChannelType;//必填
    private String authChannelValue;//必填

    private String authChannelAlias;//如果不填默认用账号名称
    private String authChannelName;//如果不填默认用账号名称
    private Integer level=0;           //默认0
    private Long templateFeatureId; //介质模版主键id

}

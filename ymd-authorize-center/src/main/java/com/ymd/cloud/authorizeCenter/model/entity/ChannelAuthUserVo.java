package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class ChannelAuthUserVo implements Serializable {
    private String userAccount;//必填

    private String userName;//如果不填默认用账号
    private String userIdentity;//选填
    private List<ChannelAuthChannelInfoVo> channelAuthChannelInfoVoList;//介质授权必填，人员授权不用填
}

package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChannelAuthorizeEntity implements Serializable {
    private BaseAuthorizeVo baseAuthorizeVo;
    private ChannelAuthGroupVo channelAuthGroupVo;
    private ChannelAuthTaskVo channelAuthTaskVo;//当前参数不需要前端传，需要在web controller创建事件中心时创建参数
    private List<ChannelAuthLockVo> channelAuthLockVoList;
    private List<ChannelAuthUserVo> channelAuthUserVoList;
}

package com.ymd.cloud.authorizeCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterGroupUserDeviceAuthorize;
import com.ymd.cloud.authorizeCenter.model.entity.ChannelAuthorizeEntity;

public interface AuthorizeCenterGroupUserDeviceAuthorizeService extends IService<AuthorizeCenterGroupUserDeviceAuthorize> {
    JSONObject saveGroupAuth(ChannelAuthorizeEntity channelAuthorizeEntity);
    JSONObject groupAuthorizeGenerateConsumerHandler(String taskNo, String topic, String jobType, String msgBody);
}

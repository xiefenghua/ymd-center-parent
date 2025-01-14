package com.ymd.cloud.api.eventCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;

public interface EventCenterServiceClientApi {
    JSONObject push(EventCenterPush model);
    String getTopicByTopicType(String topicType);
}

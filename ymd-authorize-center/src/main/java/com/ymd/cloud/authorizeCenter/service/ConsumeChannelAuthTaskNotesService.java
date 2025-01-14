package com.ymd.cloud.authorizeCenter.service;

import com.alibaba.fastjson.JSONObject;

public interface ConsumeChannelAuthTaskNotesService {
    JSONObject channelAuthConsumerHandler(String taskNo);
}

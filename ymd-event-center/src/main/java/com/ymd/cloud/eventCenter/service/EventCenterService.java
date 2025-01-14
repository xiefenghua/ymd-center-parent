package com.ymd.cloud.eventCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;

import java.util.List;

public interface EventCenterService extends IService<EventCenterTopicTaskRecord> {
    JSONObject push(EventCenterPush model);
    String consumerHandler(String className, String taskNo, String topic, String jobType, String msgBody);
    JSONObject subscribe(EventCenterPush model);

    JSONObject saveCacheQueue(EventCenterTopicTaskRecord eventCenterTopicTaskRecord);
    JSONObject processCacheQueue(EventCenterTopicTaskRecord eventCenterTopicTaskRecord);
    JSONObject processReturn(String taskNo, String mqMessageId, String mqReturnCode, String returnBody, long postTime);
    String batchSendDelayMsgPolicy(boolean compelRefresh, boolean action);
    JSONObject getCacheList();
    void clearResetDelayTask();
    List<EventCenterTopicTaskRecord> selectTaskByLikeMsgBody(String taskMsgBody);
    void updatePushStatus();
    EventCenterTopicTaskRecord selectDelayTaskByMessageId(String mqMessageId);
    EventCenterTopicTaskRecord selectDelayTaskByTaskNo(String taskNo);



}

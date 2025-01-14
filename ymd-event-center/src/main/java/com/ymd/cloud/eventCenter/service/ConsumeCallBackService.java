package com.ymd.cloud.eventCenter.service;


import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;

public interface ConsumeCallBackService {
    void returnTo(String taskNo, String mqMessageId, String body, String replyCode, String replyText, long postTime);
    boolean retryCount(String mqMessageId);
    EventCenterTopicTaskRecord getMessageByMessageId(String mqMessageId);
}

package com.ymd.cloud.eventCenter.service;

public interface EventCenterConsumerService {
    String handler(String taskNo, String topic, String jobType, String msgBody);
}

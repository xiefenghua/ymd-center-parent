package com.ymd.cloud.eventCenter.service;


import com.ymd.cloud.eventCenter.model.domain.EventCenterTaskQueue;

import java.util.List;

public interface RabbitMqTopicQueueService {
    EventCenterTaskQueue analysis(String topicType);
    List<EventCenterTaskQueue> eventCenterTaskQueueList();
    List<String> queueList();
    String queueNameToJobType(String queueName) ;
    String exchangeToJobType(String exchange);
    String routingKeyToJobType(String routingKey);
}

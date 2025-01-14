package com.ymd.cloud.eventCenter.service.impl;

import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.config.TopicConfig;
import com.ymd.cloud.eventCenter.enums.EventCenterEnum;
import com.ymd.cloud.eventCenter.model.domain.EventCenterTaskQueue;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RabbitMqTopicQueueServiceImpl implements RabbitMqTopicQueueService {
    @Autowired
    private TopicConfig topicConfig;
    @Value("${spring.profiles.active}")
    private String env;
    public EventCenterTaskQueue analysis(String jobType) {
        if(EmptyUtil.isEmpty(jobType)) return null;
        EventCenterTaskQueue delayTaskQueue=new EventCenterTaskQueue();
        delayTaskQueue.setJob(jobType);
        delayTaskQueue.setQueue(EventCenterEnum.event_center.queue+"."+ env+"."+jobType );
        delayTaskQueue.setExchange(EventCenterEnum.event_center.exchange+"."+ env+ "."+jobType);
        delayTaskQueue.setRoutingKey(EventCenterEnum.event_center.routeKey+"."+ env+ "."+jobType);
        return delayTaskQueue;
    }
    @Override
    public List<EventCenterTaskQueue> eventCenterTaskQueueList() {
        List<EventCenterTaskQueue> delayTaskQueueList=new ArrayList<>();
        List<String> jobTypeList= topicConfig.allRabbitMqJobTypeList();
        jobTypeList.forEach(jobType -> {
            EventCenterTaskQueue delayTaskQueue=analysis(jobType);
            if(EmptyUtil.isNotEmpty(delayTaskQueue)) {
                delayTaskQueueList.add(delayTaskQueue);
            }
        });
        return delayTaskQueueList;
    }

    @Override
    public List<String> queueList() {
        List<EventCenterTaskQueue> delayTaskQueueList=eventCenterTaskQueueList();
        List<String> queueList = delayTaskQueueList.stream().map(EventCenterTaskQueue::getQueue).collect(Collectors.toList());
        return queueList;
    }
    public String queueNameToJobType(String queueName) {
        if(EmptyUtil.isEmpty(queueName))return null;
        return queueName.replace(EventCenterEnum.event_center.queue+"."+ env+".","").trim();
    }
    public String exchangeToJobType(String exchange) {
        if(EmptyUtil.isEmpty(exchange))return null;
        return exchange.replace(EventCenterEnum.event_center.exchange+"."+ env+".","").trim();
    }
    public String routingKeyToJobType(String routingKey) {
        if(EmptyUtil.isEmpty(routingKey))return null;
        return routingKey.replace(EventCenterEnum.event_center.routeKey+"."+ env+".","").trim();
    }
}

package com.ymd.cloud.eventCenter.config;

import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.enums.EventCenterEnum;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.service.EventCenterTopicRegisterService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
@Component
public class TopicConfig {
    public static  final String topicFix="ymd_";
    @Autowired
    EventCenterTopicRegisterService eventCenterTopicRegisterService;

    public EventCenterTopicRegister getByTopic(String topic){
        return eventCenterTopicRegisterService.selectByTopic(topic);
    }
    public EventCenterTopicRegister getByType(String topicType){
        return eventCenterTopicRegisterService.selectByType(topicType);
    }
    public EventCenterTopicRegister getByJobType(String jobType){
        return eventCenterTopicRegisterService.selectByJobType(jobType);
    }
    public List<String> allRabbitMqTopicList() {
        List<String> topicList = new ArrayList<>();
        List<EventCenterTopicRegister> eventCenterTopicRegisters=eventCenterTopicRegisterService.selectSystemInitAllRabbitMqTopic();
        if(EmptyUtil.isNotEmpty(eventCenterTopicRegisters)){
            for (EventCenterTopicRegister eventCenterTopicRegister:eventCenterTopicRegisters) {
                String topic=eventCenterTopicRegister.getTopic();
                topicList.add(topic);
            }
        }
        return new ArrayList<>(new HashSet<>(topicList));
    }
    public List<String> allRabbitMqJobTypeList() {
        List<String> jobTypeList=new ArrayList<>();
        jobTypeList.add(EventCenterEnum.event_center.job);
        List<String> eventCenterQueueList=eventCenterTopicRegisterService.selectAllRabbitMqJobType();
        if(EmptyUtil.isNotEmpty(eventCenterQueueList)){
            jobTypeList.addAll(eventCenterQueueList);
        }
        return new ArrayList<>(new HashSet<>(jobTypeList));
    }
}

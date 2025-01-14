package com.ymd.cloud.eventCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRule;

import java.util.List;

public interface EventCenterTopicRegisterService extends IService<EventCenterTopicRegister> {
    PageInfo<EventCenterTopicRegister> pageEventCenterTopicRegisterList(PageRequest request, EventCenterTopicRegister model);
    JSONObject saveTopic(EventCenterTopicRegister model);
    JSONObject delTopic(String topic, String currUserAccount);
    JSONObject doBindRule(EventCenterTopicRule model);

    List<String> selectAllMqttTopicList(String topicFix );
    List<EventCenterTopicRegister> selectSystemInitAllRabbitMqTopic();
    List<String> selectAllRabbitMqJobType();
    EventCenterTopicRegister selectByTopic(String topic);
    EventCenterTopicRegister selectByType(String type);
    EventCenterTopicRegister selectByJobType(String jobType);
}


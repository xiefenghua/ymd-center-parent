package com.ymd.cloud.eventCenter.service.impl;

import com.ymd.cloud.eventCenter.mapper.EventCenterRuleMapper;
import com.ymd.cloud.eventCenter.service.EventCenterConsumerService;
import com.ymd.cloud.eventCenter.service.EventCenterLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class EventCenterConsumerServiceImpl implements EventCenterConsumerService {
    @Resource
    EventCenterRuleMapper eventCenterRuleMapper;
    @Autowired
    EventCenterLogService eventCenterLogService;

    @Override
    public String handler(String taskNo ,String topic,String jobType,String msgBody) {
        log.info("======================================事件中心:{}{}{}{}",taskNo , topic, jobType, msgBody);
        return "200";
    }
}

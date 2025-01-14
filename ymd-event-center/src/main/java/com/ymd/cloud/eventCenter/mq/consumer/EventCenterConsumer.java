package com.ymd.cloud.eventCenter.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.HttpClientUtils;
import com.ymd.cloud.common.utils.SpinLock;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.mq.BaseConsumer;
import com.ymd.cloud.eventCenter.service.EventCenterService;
import com.ymd.cloud.eventCenter.service.EventCenterTopicRegisterService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventCenterConsumer implements BaseConsumer {
    @Autowired
    EventCenterTopicRegisterService eventCenterTopicRegisterService;
    @Autowired
    EventCenterService eventCenterService;
    @SneakyThrows
    @Override
    public String consume(String taskNo,String topic,String jobType,String msgBody) {
        String result = null;
        if (EmptyUtil.isNotEmpty(msgBody)) {
            EventCenterTopicRegister eventCenterTopicRegister = eventCenterTopicRegisterService.selectByTopic(topic);
            if (EmptyUtil.isNotEmpty(eventCenterTopicRegister)) {
                if (eventCenterTopicRegister.getCallBackType() == 1) {//内部调用=1  http形式=2
                    result = eventCenterService.consumerHandler(eventCenterTopicRegister.getClassName(), taskNo, topic, jobType, msgBody);
                } else {
                    JSONObject param=new JSONObject();
                    param.put("taskNo",taskNo);
                    param.put("topic",topic);
                    param.put("msgBody",msgBody);
                    param.put("taskNo",taskNo);
                    log.info("事件中心构造参数调用方式处理参数:{}", param.toJSONString());
                    result = HttpClientUtils.doPost(eventCenterTopicRegister.getHttpUrl(), param).toJSONString();
                }
                log.info("======================================事件中心msgBody:{} result:{}", msgBody, result);
            }
        }
        return result;
    }
}

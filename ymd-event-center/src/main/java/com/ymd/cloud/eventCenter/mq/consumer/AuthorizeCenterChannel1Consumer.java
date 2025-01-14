package com.ymd.cloud.eventCenter.mq.consumer;

import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.mq.BaseConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizeCenterChannel1Consumer implements BaseConsumer {
    @Autowired
    private EventCenterConsumer eventCenterConsumer;
    @Override
    public String consume(String taskNo ,String topic,String jobType,String msgBody) {
        if(EmptyUtil.isNotEmpty(msgBody)) {
            return eventCenterConsumer.consume(taskNo,topic,jobType,msgBody);
        }
        return null;
    }
}

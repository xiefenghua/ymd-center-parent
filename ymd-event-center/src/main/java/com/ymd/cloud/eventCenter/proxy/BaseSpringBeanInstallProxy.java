package com.ymd.cloud.eventCenter.proxy;

import com.ymd.cloud.common.config.SpringContextUtil;
import com.ymd.cloud.eventCenter.service.EventCenterConsumerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BaseSpringBeanInstallProxy {
    EventCenterConsumerService eventCenterConsumerService;
    @SneakyThrows
    public BaseSpringBeanInstallProxy getEventCenterConsumerProxy(String className) {
        this.eventCenterConsumerService = (EventCenterConsumerService) SpringContextUtil.getBean(className);
        return this;
    }

    public String handler(String taskNo ,String topic,String jobType,String msgBody) {
        return this.eventCenterConsumerService.handler(taskNo ,topic,jobType,msgBody);
    }
}

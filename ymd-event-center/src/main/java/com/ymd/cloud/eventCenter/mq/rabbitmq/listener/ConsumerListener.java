package com.ymd.cloud.eventCenter.mq.rabbitmq.listener;

import com.ymd.cloud.eventCenter.mq.BaseConsumerProxy;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumerListener {
    @Autowired
    private RabbitMqTopicQueueService eventCenterTopicQueueService;
    @Autowired
    private BaseConsumerProxy baseConsumerProxy;
    @RabbitListener(queues = "#{rabbitMqServiceImpl.queues()}")
    public void consume(Message message) {
        String jobType=eventCenterTopicQueueService.queueNameToJobType(message.getMessageProperties().getConsumerQueue());
        baseConsumerProxy.getProxy(jobType).consume(message);
    }
}

package com.ymd.cloud.eventCenter.mq.consumer;

import com.rabbitmq.client.Channel;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.eventCenter.config.RabbitMqConfig;
import com.ymd.cloud.eventCenter.mq.BaseConsumerProxy;
import com.ymd.cloud.eventCenter.mq.MessageHelper;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class BasicConsume {
    private Channel channel;
    @Autowired
    private RabbitMqConfig rabbitMqConfig;
    @Autowired
    private RabbitMqTopicQueueService eventCenterTopicQueueService;
    @Autowired
    private BaseConsumerProxy baseConsumerProxy;
    public void basicConsume(String queueName, String direction) {
        channel=rabbitMqConfig.getChannel();
        /**
         * 消费者负责消费指定队列中的消息
         * 参数：
         * 1、队列名
         * 2、是否自动确认，true代表自动确认，消费者接收到消息后会自动相MQ发送确认信号，表示我已经接收到消息了，你可以把消息删除了。
         * 3、接收消息的回调函数
         * 4、当一个消费者取消订阅时的回调
         */
        try {
            channel.basicConsume(queueName, true, (consumerTag, messDelivery) -> {
                log.info("消费者[{}]通过[{}]队列名[{}]手动消费rabbitmq消息的回调函数时间：{}",consumerTag,direction,queueName, DateUtil.getTime());
                String body=new String(messDelivery.getBody());
                String job=eventCenterTopicQueueService.queueNameToJobType(queueName);
                Message message= MessageHelper.objToMsg(body);
                message.getMessageProperties().setMessageId(messDelivery.getProperties().getMessageId());
                message.getMessageProperties().setDelay(-1);
                message.getMessageProperties().setReceivedDelay(-1);
                baseConsumerProxy.getProxy(job).consume(message);
            }, consumerTag -> {
                log.info("消费者[{}]取消订阅时的回调",consumerTag);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

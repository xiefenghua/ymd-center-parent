package com.ymd.cloud.eventCenter.mq.rabbitmq.producer;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.config.RabbitMqConfig;
import com.ymd.cloud.eventCenter.enums.EventCenterEnum;
import com.ymd.cloud.eventCenter.model.domain.EventCenterTaskQueue;
import com.ymd.cloud.eventCenter.model.domain.MqSendVo;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import org.apache.commons.codec.CharEncoding;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class RabbitMqMessage {
    @Autowired
    private RabbitMqConfig rabbitMqConfig;
    @Autowired
    RabbitMqTopicQueueService eventCenterTopicQueueService;
    public void send(MqSendVo mqSendVo){
        RabbitTemplate rabbitTemplate=rabbitMqConfig.getRabbitTemplate();
        Integer delay=1;
        if(0!=mqSendVo.getDelay()) {
            delay = mqSendVo.getDelay() * 1000;
        }
        // 通过广播模式发布延时消息 超时n秒 持久化消息 消费后销毁 这里无需指定路由，会广播至每个绑定此交换机的队列
        Integer finalDelay = delay;
        EventCenterTaskQueue default_delayTaskQueue=eventCenterTopicQueueService.analysis(EventCenterEnum.event_center.job);
        String exchange= EmptyUtil.isNotEmpty(mqSendVo.getExchange())?mqSendVo.getExchange():default_delayTaskQueue.getExchange();
        String routeKey=EmptyUtil.isNotEmpty(mqSendVo.getRoutingKey())?mqSendVo.getRoutingKey():default_delayTaskQueue.getRoutingKey();
        rabbitTemplate.convertAndSend(exchange, routeKey, JSONObject.toJSONString(mqSendVo), message -> {
            //设置消息延迟时间n秒,n秒之后投递给队列 针对的是交换机
            message.getMessageProperties().setDelay(finalDelay);
            message.getMessageProperties().setMessageId(mqSendVo.getMessageId());
            message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
            message.getMessageProperties().setContentEncoding(CharEncoding.UTF_8);
            return message;
        });
    }

    public void basicPublish(String exchangeName, String routeKey,String msgBody) {
        /**
         * 生产者只需要将消息投递给交换机，而交换机负责将消息发送给对应的队列
         * 参数：
         * 1、交换机名
         * 2、routingKey，交换机根据routingKey去寻找相匹配的队列，并将消息发送到队列中
         * 3、消息属性
         * 4、消息体
         */
        try {
            Channel channel=rabbitMqConfig.getChannel();
            channel.basicPublish(exchangeName, routeKey, null, msgBody.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.ymd.cloud.eventCenter.mq.mqtt.listener;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.config.TopicConfig;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.mq.BaseConsumerProxy;
import com.ymd.cloud.eventCenter.mq.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * MQTT消息处理器（消费者）
 *
 */
@Component
@Slf4j
public class MqttMessageListener implements MessageHandler {
    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
    @Autowired
    private BaseConsumerProxy baseConsumerProxy;
    @Autowired
    TopicConfig topicConfig;
    @ServiceActivator(inputChannel = CHANNEL_NAME_IN)
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String body = message.getPayload().toString();
        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        if(EmptyUtil.isNotEmpty(body)&&JSONObject.isValid(body)) {
            JSONObject bodyJson = JSONObject.parseObject(body);
            String messageId = bodyJson.getString("messageId");
            EventCenterTopicRegister eventCenterTopicRegister= topicConfig.getByTopic(topic);
            if(EmptyUtil.isNotEmpty(eventCenterTopicRegister)) {
                String job=eventCenterTopicRegister.getJobType();
                org.springframework.amqp.core.Message consumeMessage= MessageHelper.objToMsg(body);
                consumeMessage.getMessageProperties().setMessageId(messageId);
                consumeMessage.getMessageProperties().setDelay(-1);
                consumeMessage.getMessageProperties().setReceivedDelay(-1);
                baseConsumerProxy.getProxy(job).consume(consumeMessage);
            }
        }
    }
}

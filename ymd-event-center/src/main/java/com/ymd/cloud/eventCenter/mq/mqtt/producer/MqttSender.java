package com.ymd.cloud.eventCenter.mq.mqtt.producer;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttSender {
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}
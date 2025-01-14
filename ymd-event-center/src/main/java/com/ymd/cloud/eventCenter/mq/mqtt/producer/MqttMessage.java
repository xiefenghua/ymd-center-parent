package com.ymd.cloud.eventCenter.mq.mqtt.producer;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.eventCenter.model.domain.MqSendVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class MqttMessage{
    @Resource
    MqttSender mqttSender;

    public void send(MqSendVo mqSendVo) throws IOException {
        mqttSender.sendToMqtt(JSONObject.toJSONString(mqSendVo), mqSendVo.getTopic());
    }
    public void basicPublish(String exchangeName, String routeKey,String msgBody) {
    }
}

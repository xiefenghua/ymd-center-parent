package com.ymd.cloud.eventCenter.web;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.eventCenter.mq.mqtt.producer.MqttSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("ymd/api/mq")
@Slf4j
@Api(value = "事件消息", tags = "事件消息接口")
public class MqController {
    @Resource
    MqttSender mqttSender;
    @PostMapping(value = "/send")
    @ApiOperation(value = "发送mqtt信息")
    public void send(@RequestBody String mqBody) {
        log.info("发送mqtt信息 参数:{}",mqBody);
        JSONObject jsonObject=JSONObject.parseObject(mqBody);
        String notifyJson=jsonObject.getString("msg");
        String topic=jsonObject.getString("topic");
        mqttSender.sendToMqtt(notifyJson, topic);
    }
}

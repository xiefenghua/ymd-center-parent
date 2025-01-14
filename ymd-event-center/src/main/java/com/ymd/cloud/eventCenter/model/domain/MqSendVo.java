package com.ymd.cloud.eventCenter.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class MqSendVo {
    private String exchange;
    private String routingKey;
    private String topic;
    private String msgBody;
    private String pushTime;
    private Integer delay;
    private String messageId;
}

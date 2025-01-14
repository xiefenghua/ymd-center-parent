package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MqttConsumerEntity implements Serializable {
    private String taskNo;
    private String topic;
    private String jobType;
    private String msgBody;
}

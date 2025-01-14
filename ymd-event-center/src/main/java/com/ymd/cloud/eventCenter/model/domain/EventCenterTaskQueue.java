package com.ymd.cloud.eventCenter.model.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventCenterTaskQueue implements Serializable {
    private String job;
    private String queue;
    private String exchange;
    private String routingKey;
}

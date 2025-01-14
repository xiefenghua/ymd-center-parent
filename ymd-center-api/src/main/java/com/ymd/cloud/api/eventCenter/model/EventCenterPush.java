package com.ymd.cloud.api.eventCenter.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventCenterPush implements Serializable {
    private String topic;
    private String msgBody;
    private String createAuthUserId;
    private String remark;

    //延迟时长，0代表立即 单位秒
    private Integer delayTime;
     /**
     * 权重,如果不传则系统生成取当前时间戳
     * 值越小权重越高 自定义范围 1~99
     * 不支持 负数
     */
    private Long weight;
}
package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelAuthTaskVo implements Serializable {
    private String taskType;//必填

    private String taskNo;//后台自动生成
    private String authNo;//后台自动生成
}

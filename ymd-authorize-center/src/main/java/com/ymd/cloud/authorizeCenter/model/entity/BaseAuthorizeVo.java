package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseAuthorizeVo implements Serializable {
    private String orgId;//必填
    private Date startTime;//必填
    private Date endTime;//必填
    private String createUserAccount;//必填

    private String authExtend;//授权扩展字段(json 如介质安全等级)，选填，主要存入数据库
    private String authDesc;//授权备注，选填，主要存入数据库
    private String authResource;//数据库来源，选填，主要存入数据库

    private Integer isGroup;//此数据由 PersonnelAuthGroupVo对象参数系统检测生成
    private Long groupAuthId;//组授权id,此数据由系统组授权生成，主键id
}

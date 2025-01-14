package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@TableName("t_authorize_center_authorize_template_data")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterAuthorizeTemplateData implements Serializable {
    @JsonSerialize(using= ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    protected Long id;
    /**
     * 授权id
     */
    @TableField("auth_id")
    private Long authId;
    /**
     * 介质模板id
     */
    @TableField("template_feature_id")
    private Long templateFeatureId;
    /**
     * 特征值类型 card,face、finger等
     */
    @TableField("feature_type")
    private String featureType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField("create_time")
    private Date createTime;

    //=================TODO 以下需要从介质模版获取===根据模版id查询模版特征值  dncode 指纹id 等数据================
    @TableField(exist=false)
    private String cardDnCode;
    @TableField(exist=false)
    private Integer fingerId ;
    @TableField(exist=false)
    private Integer fingerType;
    @TableField(exist=false)
    private String feature;
}
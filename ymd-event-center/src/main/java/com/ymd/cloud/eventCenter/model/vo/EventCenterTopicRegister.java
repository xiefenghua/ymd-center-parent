package com.ymd.cloud.eventCenter.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;

@TableName("ymd_event_center_topic_register")
@Data
@EqualsAndHashCode(callSuper = false)
public class EventCenterTopicRegister implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主题名称topic(业务类型_主题类别_操作类型_topic)
     */
    private String topic;

    /**
     * 业务分类 
     */
    @TableField("job_type")
    private String jobType;

    /**
     * 主题类别
     */
    private String type;

    /**
     * 主题类别操--作类型
     */
    @TableField("opt_type")
    private String optType;
    /**
     * mqtt类型(mqtt,rabbitmq)
     */
    @TableField("mqtt_type")
    private String mqttType;

    /**
     * 回调接口形式   内部调用=1  http形式=2
     */
    @TableField("call_back_type")
    private Integer callBackType;

    /**
     * http地址，CallBackType=2 此参数必填    接口要求 post 参数以body形式传输json
     */
    @TableField("http_url")
    private String httpUrl;

    /**
     * spring业务接口类路径，如果没有业务接口类只实现EventCenterConsumerService的话就填业务实现类， CallBackType=1 此参数必填  CallBackType=1 此参数必填
     */
    @TableField("class_name")
    private String className;
    /**
     * topic注册状态 0=删除 1=开放
     */
    private Integer status;

    private String remark;

    /**
     * 操作者userid
     */
    @TableField("create_auth_user_id")
    private String createAuthUserId;
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
    @TableField(exist = false)
    private String startTime;
    @TableField(exist = false)
    private String endTime;
    @TableField(exist = false)
    private Long ruleId;
    @TableField(exist = false)
    private String ruleAction;
}
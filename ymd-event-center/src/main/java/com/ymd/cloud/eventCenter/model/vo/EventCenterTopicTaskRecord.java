package com.ymd.cloud.eventCenter.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ymd.cloud.common.utils.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@TableName("ymd_event_center_topic_task_record")
@Data
@EqualsAndHashCode(callSuper = false)
public class EventCenterTopicTaskRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String topic;
    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;
    /**
     * 延迟时长 单位秒
     */
    @TableField("delay_time")
    private Integer delayTime;
    /**
     * 队列报文
     */
    @TableField("task_msg_body")
    private String taskMsgBody;


    /**
     * 任务唯一code,如果不存则系统生产
     */
    @TableField("task_no")
    private String taskNo;
    /**
     * 权重,如果不传则系统生成取当前时间戳
     * 值越小权重越高 自定义范围 1~99
     * 不支持 负数
     */
    private Long weight;

    @TableField("exchange")
    private String exchange;
    @TableField("routing_key")
    private String routingKey;
    //#------------------------------------------
    /**
     * 延迟队列推送时间点
     */
    @TableField("push_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date pushTime;
    /**
     * 推送状态 1=已推送 0=未推送
     */
    private int pushStatus;
    @TableField("create_time")
    @JsonFormat(pattern = DateUtil.yyyy_MM_ddHH_mm_ss, timezone = "GMT+8")
    private Date createTime;

    @TableField("update_time")
    @JsonFormat(pattern = DateUtil.yyyy_MM_ddHH_mm_ss, timezone = "GMT+8")
    private Date updateTime;
    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;
    /**
     * 失败重试次数
     */
    @TableField("error_retry_count")
    private Integer errorRetryCount;
    //#------------------------------------------
    @TableField("mq_message_id")
    private String mqMessageId;
    @TableField("mq_return_code")
    private String mqReturnCode;
    @TableField("return_body")
    private String returnBody;
    /**
     * 操作者userid
     */
    @TableField("create_auth_user_id")
    private String createAuthUserId;
    //#------------------------------------------
    @TableField(exist=false)
    private String opt;
    @TableField(exist=false)
    private int pageSize;
    @TableField(exist = false)
    private String startTime;
    @TableField(exist = false)
    private String endTime;
}
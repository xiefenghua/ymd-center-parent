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

@TableName("ymd_event_center_topic_rule")
@Data
@EqualsAndHashCode(callSuper = false)
public class EventCenterTopicRule implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * topic注册表id
     */
    @TableField("topic_id")
    private Long topicId;

    /**
     * 规则id
     */
    @TableField("rule_id")
    private Long ruleId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
    /**
     * 操作者userid
     */
    @TableField("create_auth_user_id")
    private String createAuthUserId;
}
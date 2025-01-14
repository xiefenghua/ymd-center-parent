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

@TableName("ymd_event_center_rule")
@Data
@EqualsAndHashCode(callSuper = false)
public class EventCenterRule implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 规则描述
     */
    @TableField("rule_remark")
    private String ruleRemark;

    /**
     * 规则优先级
     */
    @TableField("rule_level")
    private String ruleLevel;

    /**
     * 规则语句 规则语句：
     *   规则语句：
     *     采用json格式保存，比如{"ack":"AUTO","durable":"true","retry_count":3}
     *         ack确认模式 AUTO表示自动，MANUAL表示手动，NONE表示无
     *         durable 是否所有topic持久化类型 如果true代表所有topic使用此类型，如果null则根据topic注册表规则是否持久化
     *         retry_count 重试机制
     */
    @TableField("rule_action")
    private String ruleAction;

    /**
     * 规则状态 1=有效 0=无效
     */
    @TableField("rule_status")
    private Integer ruleStatus;

    /**
     * 是否全局（1=全局，0=局部（默认）全局规则只能允许一条）
     */
    @TableField("rule_is_all")
    private Integer ruleIsAll;

    /**
     * 操作者userid
     */
    @TableField("create_auth_user_id")
    private String createAuthUserId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
    @TableField(exist = false)
    private String startTime;
    @TableField(exist = false)
    private String endTime;
}
package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@TableName("t_authorize_center_task_result")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterTaskResult extends BaseModel<Long> implements Serializable {
    /**
     * auth_id
     */
    @TableField("auth_id")
    private String authId;

    /**
     * task_no
     */
    @TableField("task_no")
    private String taskNo;

    /**
     * auth_no
     */
    @TableField("auth_no")
    private String authNo;

    /**
     * lock_id
     */
    @TableField("lock_id")
    private String lockId;

    /**
     * user_account
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * channel_type
     */
    @TableField("channel_type")
    private String channelType;

    /**
     * status 0=结果失败  1=结果成功
     */
    private Integer status;
    /**
     * channel_value
     */
    @TableField("channel_value")
    private String channelValue;

    /**
     * result
     */
    private String result;
    /**
     * 表分区日期格式yyyyMM
     */
    @TableField("partition_date")
    private Integer partitionDate;
}
package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
@TableName("t_authorize_center_task")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterTask extends BaseModel<Long> implements Serializable {
    /**
     * org_id
     */
    @TableField("org_id")
    private String orgId;

    /**
     * task_no
     */
    @TableField("task_no")
    private String taskNo;

    /**
     * operator
     */
    private String operator;

    /**
     * auth_start_time
     */
    @TableField("auth_start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date authStartTime;

    /**
     * auth_end_time
     */
    @TableField("auth_end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date authEndTime;

    /**
     * task_auth_count
     */
    @TableField("task_count")
    private Integer taskCount;

    /**
     * auth_success_count
     */
    @TableField("auth_success_count")
    private Integer authSuccessCount;

    /**
     * auth_failure_count
     */
    @TableField("auth_failure_count")
    private Integer authFailureCount;

    /**
     * vali_failure_count
     */
    @TableField("vali_failure_count")
    private Integer valiFailureCount;
    @TableField("retry_count")
    private Integer retryCount;
    /**
     * task_type 授权类型  如授权，人员组与设备组授权
     */
    @TableField("task_type")
    private String taskType;

    /**
     * task_status
     * //0=提交 1=完成  2=执行中 3=失败 4=异常
     */
    @TableField("task_status")
    private Integer taskStatus;
    /**
     * lock_id
     */
    @TableField("lock_id")
    private String lockId;

    /**
     * user_target
     */
    @TableField("user_target")
    private String userTarget;

    /**
     * exception_desc
     */
    @TableField("exception_desc")
    private String exceptionDesc;
}
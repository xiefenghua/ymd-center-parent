package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
@TableName("t_authorize_center_authorize_log")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterAuthorizeLog extends BaseModel<Long> implements Serializable {
    /**
     * 锁主键id
     */
    @TableField("lock_id")
    private String lockId;
    /**
     * module
     */
    private String module;

    /**
     * operate_type
     */
    @TableField("operate_type")
    private String operateType;

    /**
     * operate_name
     */
    @TableField("operate_name")
    private String operateName;

    /**
     * operation
     */
    private String operation;

    /**
     * req_param
     */
    @TableField("req_param")
    private String reqParam;

    /**
     * resp_result
     */
    @TableField("resp_result")
    private String respResult;
    /**
     * 表分区日期格式yyyyMM
     */
    @TableField("partition_date")
    private Integer partitionDate;
}
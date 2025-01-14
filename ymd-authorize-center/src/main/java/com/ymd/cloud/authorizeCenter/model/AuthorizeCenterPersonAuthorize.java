package com.ymd.cloud.authorizeCenter.model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@TableName("t_authorize_center_person_authorize")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterPersonAuthorize extends BaseModel<Long> implements Serializable {
    @TableField("org_id")
    private String orgId;

    /**
     * 锁主键id
     */
    @TableField("lock_id")
    private String lockId;

    /**
     * 授权用户账号
     */
    @TableField("user_account")
    private String userAccount;

    /**
     * 授权用户姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 授权用户身份
     */
    @TableField("user_identity")
    private String userIdentity;

    /**
     * 授权开始时间
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;

    /**
     * 授权结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;
    /**
     * 表分区日期格式yyyyMM
     */
    @TableField("partition_date")
    private Integer partitionDate;

    /**
     * 授权伪删除标识 1正常 0删除
     */
    private Integer status;

}
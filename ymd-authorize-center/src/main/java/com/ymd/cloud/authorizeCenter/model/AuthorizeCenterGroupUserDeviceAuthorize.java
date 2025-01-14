package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
@TableName("t_authorize_center_group_user_device_authorize")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterGroupUserDeviceAuthorize extends BaseModel<Long> implements Serializable {

    /**
     * org_id
     */
    @TableField("org_id")
    private String orgId;

    /**
     * name
     */
    private String name;

    /**
     * group_type
     */
    @TableField("group_type")
    private String groupType;

    /**
     * user_group_id
     */
    @TableField("user_group_id")
    private Long userGroupId;

    /**
     * device_group_id
     */
    @TableField("device_group_id")
    private Long deviceGroupId;

    /**
     * start_time
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;

    /**
     * end_time
     */
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;
    /**
     * last_auth_update_time
     */
    @TableField("last_auth_update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date lastAuthUpdateTime;

    /**
     * action_status 0=创建 1=进行中 2=成功  3=失败
     */
    @TableField("action_status")
    private Integer actionStatus;

    /**
     * delay_task_no
     */
    @TableField("delay_task_no")
    private String delayTaskNo;
    /**
     * sync_task_no
     */
    @TableField("sync_task_no")
    private String syncTaskNo;




}
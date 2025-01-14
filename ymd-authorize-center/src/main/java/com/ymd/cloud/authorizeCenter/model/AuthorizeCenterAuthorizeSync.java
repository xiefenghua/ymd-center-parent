package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
@TableName("t_authorize_center_authorize_sync")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterAuthorizeSync extends BaseModel<Long> implements Serializable {
    /**
     * 授权id
     */
    @TableField("auth_id")
    private Long authId;
    /**
     * 判断lockId+user_account+auth_channel_type+auth_channel_value 是否相同的数据并且含时间交集的时间点判断为同一数据
     */
    @TableField("auth_equal_uuid")
    private String authEqualUuid;

    /**
     * 同步操作类型
     */
    @TableField("sync_opt_type")
    private String syncOptType;

    /**
     * 同步名称
     */
    @TableField("sync_name")
    private String syncName;

    /**
     * 同步状态 1同步 0未同步
     */
    private Integer sync;

    /**
     * 上报同步状态  1已上报 0未上报
     */
    @TableField("upload_status")
    private Integer uploadStatus;

    /**
     * 下发同步状态 1下发 0未下发
     */
    @TableField("push_status")
    private Integer pushStatus;

    /**
     * 表分区日期格式yyyyMMdd
     */
    @TableField("partition_date")
    private Integer partitionDate;
    @TableField(exist=false)
    private String lockId;
}
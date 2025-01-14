package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@TableName("t_authorize_center_authorize_record")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterAuthorizeRecord extends BaseModel<Long> implements Serializable {
    /**
     * 人员授权id
     */
    @TableField("person_auth_id")
    private Long personAuthId;
    /**
     * 判断lockId+user_account+auth_channel_type+auth_channel_value 是否相同的数据并且含时间交集的时间点判断为同一数据
     */
    @TableField("auth_equal_uuid")
    private String authEqualUuid;

    private String uid;
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
     * 授权介质模块类型
     */
    @TableField("auth_channel_module_type")
    private String authChannelModuleType;

    /**
     * 授权介质类别如比
     */
    @TableField("auth_channel_type")
    private String authChannelType;
    /**
     * 授权特征值
     */
    @TableField("auth_channel_value")
    private String authChannelValue;
    /**
     * 授权介质别名
     */
    @TableField("auth_channel_alias")
    private String authChannelAlias;

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
     * 授权伪删除标识 1正常 0删除
     */
    private Integer status;

    /**
     * 授权是否有效 1=有效 0=无效
     */
    private Integer flag;

    /**
     * 冻结状态 1正常 0冻结
     */
    @TableField("freeze_status")
    private Integer freezeStatus;

    /**
     * 授权备注
     */
    @TableField("auth_desc")
    private String authDesc;

    /**
     * 授权来源
     */
    @TableField("auth_resource")
    private String authResource;

    /**
     * 是否为授权组操作 1=是 0否
     */
    @TableField("is_group")
    private Integer isGroup;

    /**
     * 授权类型
     */
    @TableField("auth_channel_name")
    private String authChannelName;

    /**
     * 授权扩展字段(json 如介质安全等级)
     */
    @TableField("auth_extend")
    private String authExtend;

    /**
     * 表分区日期格式yyyyMM
     */
    @TableField("partition_date")
    private Integer partitionDate;
    @TableField(exist=false)
    private String lockName;
    @TableField(exist=false)
    private String channelName;
    @TableField(exist=false)
    private String lockMac;
    @TableField(exist=false)
    private Long templateFeatureId;
    @TableField(exist=false)
    private String lockType;
    @TableField(exist=false)
    private Long groupAuthId;
    @TableField(exist=false)
    private Integer sync;
}
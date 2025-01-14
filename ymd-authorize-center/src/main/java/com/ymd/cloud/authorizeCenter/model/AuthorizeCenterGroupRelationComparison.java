package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@TableName("t_authorize_center_group_relation_comparison")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterGroupRelationComparison extends BaseModel<Long> implements Serializable {
    /**
     * group_auth_id
     */
    @TableField("group_auth_id")
    private Long groupAuthId;

    /**
     * group_type 1-用户组 2=设备组
     */
    @TableField("group_type")
    private Integer groupType;

    /**
     * group_id
     */
    @TableField("group_id")
    private Long groupId;

    /**
     * group_user_lock_id
     */
    @TableField("group_user_lock_id")
    private String groupUserLockId;
    @TableField(exist=false)
    private int pageStart;
    @TableField(exist=false)
    private int pageSize ;
    @TableField(exist=false)
    private List<String> groupList;
}
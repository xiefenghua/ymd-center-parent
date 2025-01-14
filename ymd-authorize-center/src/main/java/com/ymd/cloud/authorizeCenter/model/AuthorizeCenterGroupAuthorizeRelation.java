package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
@TableName("t_authorize_center_group_authorize_relation")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterGroupAuthorizeRelation extends BaseModel<Long> implements Serializable {

    /**
     * auth_id
     */
    @TableField("auth_id")
    private Long authId;

    /**
     * group_auth_id
     */
    @TableField("group_auth_id")
    private Long groupAuthId;
}
package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
@TableName("t_authorize_center_authorize_third_sync_record")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterAuthorizeThirdSyncRecord extends BaseModel<Long> implements Serializable {
    /**
     * 授权记录id
     */
    @TableField("auth_id")
    private Long authId;

    /**
     * 厂商
     */
    private String vendor;

    /**
     * 来源
     */
    private String resouce;

    /**
     * uuid
     */
    private String uuid;

    /**
     * sync
     */
    private Integer sync;

}
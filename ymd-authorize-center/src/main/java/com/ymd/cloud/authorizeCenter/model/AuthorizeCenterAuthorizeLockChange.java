package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@TableName("t_authorize_center_authorize_lock_change")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterAuthorizeLockChange extends BaseModel<Long> implements Serializable {
    /**
     * 锁id
     */
    @TableField("lock_id")
    private String lockId;

    /**
     * 卡片平台版本号
     */
    @TableField("card_has_change_version")
    private Integer cardHasChangeVersion;

    /**
     * 卡片硬件上报版本号
     */
    @TableField("card_pre_change_version")
    private Integer cardPreChangeVersion;

    /**
     * 密码平台版本号
     */
    @TableField("pwd_has_change_version")
    private Integer pwdHasChangeVersion;

    /**
     * 密码硬件上报版本号
     */
    @TableField("pwd_pre_change_version")
    private Integer pwdPreChangeVersion;

    /**
     * 人脸平台版本号
     */
    @TableField("face_has_change_version")
    private Integer faceHasChangeVersion;

    /**
     * 人脸硬件上报版本号
     */
    @TableField("face_pre_change_version")
    private Integer facePreChangeVersion;

    /**
     * 指纹平台版本号
     */
    @TableField("finger_has_change_version")
    private Integer fingerHasChangeVersion;

    /**
     * 指纹硬件上报版本号
     */
    @TableField("finger_pre_change_version")
    private Integer fingerPreChangeVersion;

}
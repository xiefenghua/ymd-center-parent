package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@TableName("t_authorize_center_task_notes")
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeCenterTaskNotes extends BaseModel<Long> implements Serializable {
    /**
     * task_no
     */
    @TableField("task_no")
    private String taskNo;

    /**
     * auth_no
     */
    @TableField("auth_no")
    private String authNo;

    /**
     * auth_content
     */
    @TableField("auth_content")
    private String authContent;

}
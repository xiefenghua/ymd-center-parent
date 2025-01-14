package com.ymd.cloud.authorizeCenter.model.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskNotes;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
public class TaskResultEntity implements Serializable {
    private AuthorizeCenterTaskNotes authorizeCenterTaskNotes;
    private AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord;
    private JSONObject result;
}
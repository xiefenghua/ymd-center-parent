package com.ymd.cloud.authorizeCenter.model.third;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "t_third_server_auth_return_log")
public class ThirdServerAuthReturnLog implements Serializable {
    @TableId(value="id",type = IdType.AUTO)
    private Long id;
    @TableField("msgId")
    private Long msgId;
    @TableField("userAccount")
    private String userAccount;
    @TableField("serialNo")
    private String serialNo;
    @TableField("phone")
    private String phone;
    @TableField("method")
    private String method;
    @TableField("msgJson")
    private String msgJson;
    @TableField("resultContent")
    private String resultContent;
    @TableField("resultCode")
    private Boolean resultCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("createTime")
    private Date createTime= new Date();
    @TableField("deviceOnline")
    private Boolean deviceOnline;
    public void setCreateTime(Date createTime) {
        this.createTime = new Date();
    }
}
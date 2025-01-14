package com.ymd.cloud.authorizeCenter.model.lock;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@TableName(value = "t_hzbit_open_lock")
@Data
public class OpenLock implements Serializable {
    @Id
    private Integer id;

    @TableField("username")
    private String userName;

    @TableField("`time`")
    private String time;

    @TableField("lockId")
    private String lockId;

    @TableField("msg")
    private String msg;

    @TableField("longtitude")
    private String longtitude;

    @TableField("latitude")
    private String latitude;

    @TableField("address")
    private String address;

    @TableField("`type`")
    private String type;


    @TableField("`open_type`")
    private String openType;


    @TableField("remark")
    private String remark;


}
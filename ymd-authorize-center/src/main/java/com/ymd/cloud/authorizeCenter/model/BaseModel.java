package com.ymd.cloud.authorizeCenter.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Data
public abstract class BaseModel<T> implements Serializable
{
    /**
     * id
     */
    @JsonSerialize(using= ToStringSerializer.class)
    @TableId(type = IdType.AUTO)
    protected T id;
    /**
     * 备注
     */
    @TableField("remark")
    protected String remark;
    /**
     * 操作人
     */
    @TableField("create_by")
    protected String createBy;

    @TableField("update_by")
    protected String updateBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField("create_time")
    protected Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField("update_time")
    protected Date updateTime;

    /** 请求参数 */
    @TableField(exist=false)
    protected Map<String, Object> params;

    @TableField(exist = false)
    protected List<String> ids=new ArrayList<>();




}

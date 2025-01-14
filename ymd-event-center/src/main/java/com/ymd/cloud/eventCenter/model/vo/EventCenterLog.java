package com.ymd.cloud.eventCenter.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@TableName("ymd_event_center_log")
@Data
@EqualsAndHashCode(callSuper = false)
public class EventCenterLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 主题名称topic
     */
    private String topic;

    /**
     * 日志分类
     */
    @TableField("log_type")
    private String logType;

    /**
     * 任务号
     */
    @TableField("task_no")
    private String taskNo;

    /**
     * 权重
     */
    private Long weight;

    /**
     * 操作类和方法
     */
    @TableField("post_content")
    private String postContent;

    /**
     * 0=结果失败  1=结果成功
     */
    @TableField("result_code")
    private String resultCode;

    /**
     * 响应时间
     */
    @TableField("post_time")
    private Long postTime;

    /**
     * 尝试次数
     */
    @TableField("retry_count")
    private Integer retryCount;
    /**
     * 操作者userid
     */
    @TableField("create_auth_user_id")
    private String createAuthUserId;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    /**
     * 任务参数
     */
    @TableField("param_body")
    private String paramBody;
    /**
     * 表分区日期格式yyyyMM
     */
    @TableField("partition_date")
    private Integer partitionDate;
    /**
     * 同步结果
     */
    private String result;
    @TableField(exist = false)
    private String startTime;
    @TableField(exist = false)
    private String endTime;
}
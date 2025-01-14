package com.ymd.cloud.authorizeCenter.model.lock;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@TableName("s_resource_config")
public class ResourceConfig {
    /**
     * 资源ID
     */
    @Id
    @TableField("ID")
    private Integer id;

    /**
     * 资源类型ID
     */
    @TableField("RES_TYPE_ID")
    private Integer resTypeId;
    /**
     * MQTT类型
     */
    @TableField("MQTT_TYPE")
    private Integer mqttType;

    /**
     * 资源提供商ID
     */
    @TableField("PRO_ID")
    private Integer proId;

    /**
     * 资源服务IP
     */
    @TableField("RES_IP")
    private String resIp;

    /**
     * 资源服务端口
     */
    @TableField("RES_PORT")
    private String resPort;

    /**
     * 有效标识:0 无效，1 有效
     */
    @TableField("YXBZ")
    private String yxbz;

    /**
     * 创建时间
     */
    @TableField("CJSJ")
    private Date cjsj;

    @TableField("PUSH_ALL")
    private int pushAll;

    @TableField("MQTT_SYNC")
    private int mqttSync;

}
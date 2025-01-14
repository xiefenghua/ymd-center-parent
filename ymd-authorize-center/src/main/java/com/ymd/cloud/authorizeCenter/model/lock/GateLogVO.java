package com.ymd.cloud.authorizeCenter.model.lock;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 〈?〉<br>
 *
 * @author hhd668@163.com
 * @create 2020/8/11
 * @since 1.0
 */
@Data
public class GateLogVO implements Serializable {
    private String id;
    private String gateName;
    private String mac;
    private String gateId;
    private String content;
    private String cmdType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

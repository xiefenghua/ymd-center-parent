package com.ymd.cloud.authorizeCenter.model.lock;

import lombok.Data;

import java.io.Serializable;

/**
 * 〈?〉<br>
 *
 * @author hhd668@163.com
 * @create 2020/8/7
 * @since 1.0
 */
@Data
public class GateLockVO implements Serializable {
    private String lockId;
    private String mac;
    private String lockName;
    private String address;
}

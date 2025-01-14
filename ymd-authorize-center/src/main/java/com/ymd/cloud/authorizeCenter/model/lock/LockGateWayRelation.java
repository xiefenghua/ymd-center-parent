package com.ymd.cloud.authorizeCenter.model.lock;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@TableName(value = "t_hzbit_lock_gate_relation")
@Data
public class LockGateWayRelation implements Serializable {
    @Id
    @TableField("lockId")
    private String lockid;

    @TableField("gateId")
    private String gateid;
}
package com.ymd.cloud.authorizeCenter.mapper.lock;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.lock.LockGateWay;
import com.ymd.cloud.authorizeCenter.model.lock.LockGateWayVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LockGateWayMapper extends BaseMapper<LockGateWay> {
    LockGateWay queryInfoByGateId(@Param("gateId") String gateId);
}
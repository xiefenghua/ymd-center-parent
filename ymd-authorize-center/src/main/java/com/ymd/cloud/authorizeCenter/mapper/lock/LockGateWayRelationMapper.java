package com.ymd.cloud.authorizeCenter.mapper.lock;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.lock.GateLockVO;
import com.ymd.cloud.authorizeCenter.model.lock.GateLogVO;
import com.ymd.cloud.authorizeCenter.model.lock.LockGateWayRelation;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface LockGateWayRelationMapper extends BaseMapper<LockGateWayRelation> {
    LockGateWayRelation getGateRelationInfoByLockId(@Param("lockId") String lockId);

}
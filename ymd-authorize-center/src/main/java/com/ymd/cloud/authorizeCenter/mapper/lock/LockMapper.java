package com.ymd.cloud.authorizeCenter.mapper.lock;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.model.lock.OpenLock;
import org.apache.ibatis.annotations.Param;

public interface LockMapper extends BaseMapper<Lock> {
    Lock selectByLockId(@Param("lockId") String lockId);
    Lock selectByMac(@Param("mac") String mac);
    void insertOpenLock(OpenLock openLock);
}

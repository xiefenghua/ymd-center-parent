package com.ymd.cloud.authorizeCenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeLockChange;
import org.apache.ibatis.annotations.Param;

public interface AuthorizeCenterAuthorizeLockChangeMapper extends BaseMapper<AuthorizeCenterAuthorizeLockChange> {
    AuthorizeCenterAuthorizeLockChange selectChangeByLockId(@Param("lockId")String lockId);
    void updateCardChangeByLockId(@Param("lockId")String lockId);
    void updatePwdChangeByLockId(@Param("lockId")String lockId);
    void updateFaceChangeByLockId(@Param("lockId")String lockId);
    void updateFingerChangeByLockId(@Param("lockId")String lockId);
}
package com.ymd.cloud.authorizeCenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeSync;
import com.ymd.cloud.authorizeCenter.model.entity.AuthListParamQuery;
import com.ymd.cloud.authorizeCenter.model.entity.AuthListReturnEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorizeCenterAuthorizeRecordMapper extends BaseMapper<AuthorizeCenterAuthorizeRecord> {
    List<AuthorizeCenterAuthorizeRecord> selectAuthListPage(AuthListParamQuery authorizeRecordQuery);
    List<AuthorizeCenterAuthorizeRecord> selectAuthListByDeviceUser(AuthListParamQuery authorizeRecordQuery);
    List<AuthorizeCenterAuthorizeRecord> selectEqualAuthListByLockAndUserAndType(AuthListParamQuery authorizeRecordQuery);
    int selectExistByEqualUuid(@Param("authEqualUuid")String authEqualUuid);
    List<AuthorizeCenterAuthorizeRecord> selectIncrementEqualAuthListByLockAndUserAndType(AuthListParamQuery authorizeRecordQuery);
    List<AuthorizeCenterAuthorizeSync> selectAllNoSyncAuthList(AuthListParamQuery authorizeRecordQuery);
}
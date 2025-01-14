package com.ymd.cloud.authorizeCenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeSync;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorizeCenterAuthorizeSyncMapper extends BaseMapper<AuthorizeCenterAuthorizeSync> {
    AuthorizeCenterAuthorizeSync selectSyncByAuthId(@Param("authId")Long authId);
    List<AuthorizeCenterAuthorizeSync> selectSyncByAuthEqualUuid(@Param("authEqualUuid")String authEqualUuid);
}
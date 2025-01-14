package com.ymd.cloud.authorizeCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeSync;
import com.ymd.cloud.authorizeCenter.model.entity.ChannelAuthLockVo;

public interface AuthorizeSyncService extends IService<AuthorizeCenterAuthorizeSync> {
    void retryPushSyncAllFailAuth();
    void userAuthorizeSyncConsumerHandler(ChannelAuthLockVo channelAuthLockVo);
    void saveAuthorizeSyncByEqualUuid(Long authId,String authEqualUuid,String syncName,Integer sync,Integer uploadStatus,Integer pushStatus,String syncOptType,String createBy,String updateBy);
    void saveAuthorizeSyncByAuthId(Long authId,String authEqualUuid,String syncName,Integer sync,Integer uploadStatus,Integer pushStatus,String syncOptType,String createBy,String updateBy);
}

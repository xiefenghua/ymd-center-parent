package com.ymd.cloud.authorizeCenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterGroupUserDeviceAuthorize;
import org.apache.ibatis.annotations.Param;

public interface AuthorizeCenterGroupUserDeviceAuthorizeMapper extends BaseMapper<AuthorizeCenterGroupUserDeviceAuthorize> {
    AuthorizeCenterGroupUserDeviceAuthorize selectInfoByUserDeviceGroup(@Param("orgId")String orgId, @Param("userGroupId")Long userGroupId, @Param("deviceGroupId")Long deviceGroupId);
    int updateSyncTaskNo(@Param("id")Long id,@Param("syncTaskNo")String syncTaskNo);
    AuthorizeCenterGroupUserDeviceAuthorize selectInfoBySyncTaskNo(@Param("syncTaskNo")String syncTaskNo);
}
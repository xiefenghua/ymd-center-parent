package com.ymd.cloud.authorizeCenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterGroupRelationComparison;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorizeCenterGroupRelationComparisonMapper extends BaseMapper<AuthorizeCenterGroupRelationComparison> {
    List<String> selectGroupUserLockIdByGroup(@Param("groupAuthId") Long groupAuthId, @Param("groupType")Integer groupType, @Param("groupId")Long groupId);
    void deleteByGroupAuthId(@Param("groupAuthId") Long groupAuthId);
    void batchInsertRelationComparison(AuthorizeCenterGroupRelationComparison userDeviceGroupRelationComparison);
}
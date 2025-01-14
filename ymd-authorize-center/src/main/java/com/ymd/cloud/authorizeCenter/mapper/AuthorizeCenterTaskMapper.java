package com.ymd.cloud.authorizeCenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.entity.TaskParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorizeCenterTaskMapper extends BaseMapper<AuthorizeCenterTask> {
    List<AuthorizeCenterTask> selectListPage(TaskParam queryParam);

    AuthorizeCenterTask selectByTaskNo(@Param("taskNo") String taskNo);
    List<AuthorizeCenterTask> selectNoFinishTaskByTime(@Param("retryCount") Integer retryCount,@Param("pageSize")int pageSize);
    List<AuthorizeCenterTask> selectTaskAuthCountNull();
    void updateSyncStatusAndTaskAuthCountByTaskNo(@Param("taskNo") String taskNo,@Param("taskStatus") Integer taskStatus,@Param("taskAuthCount") Long taskAuthCount);
    void updateSyncStatusByTaskNo(@Param("taskNo") String taskNo,@Param("taskStatus") Integer taskStatus,@Param("exceptionDesc") String exceptionDesc);
    void updateDescByTaskNo(@Param("taskNo") String taskNo,@Param("exceptionDesc") String exceptionDesc);
    void updateRetryCountByTaskNo(@Param("taskNo") String taskNo);
    void updateAuthSuccessCountByTaskNo(@Param("taskNo") String taskNo);
    void updateAuthFailureCountByTaskNo(@Param("taskNo") String taskNo);
    void updateAuthCountByTaskNo(AuthorizeCenterTask AuthorizeCenterTask);
    void updateUpdateTimeByTaskNo(@Param("taskNo") String taskNo);
    void delTaskBefore1Month();
    void updateExceptionNull();
}
package com.ymd.cloud.authorizeCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.common.utils.PageRequest;

import java.util.List;

public interface AuthorizeCenterTaskService extends IService<AuthorizeCenterTask> {
    String generateTaskNo();
    String generateTaskNo(String taskNo);
    PageInfo<AuthorizeCenterTask> listPage(PageRequest request, TaskParam queryParam);

    String addAuthorizeCenterTask(ChannelAuthorizeEntity channelAuthorizeEntity);
    AuthorizeCenterTask selectByTaskNo(String taskNo);
    void updateSyncStatusAndTaskAuthCountByTaskNo(String taskNo,Integer taskStatus,Long taskAuthCount);
    void updateSyncStatusByTaskNo(String taskNo,Integer taskStatus);
    void updateSyncStatusByTaskNo(String taskNo,Integer taskStatus,String exceptionDesc);
    List<AuthorizeCenterTask> selectNoFinishTaskByTime(Integer retryCount,int pageSize);
    List<AuthorizeCenterTask> selectTaskAuthCountNull();
    void updateRetryCountByTaskNo(String taskNo);
    void updateUpdateTimeByTaskNo(String taskNo);
    void delTaskBefore1Month();
    void updateAuthCountByTaskNo(AuthorizeCenterTask AuthorizeCenterTask);
    void updateExceptionNull();

    void toTaskValiError(ChannelAuthorizeEntity channelAuthorizeEntity, JSONObject result);
    void toTaskValiError(BaseAuthorizeVo baseAuthorizeVo, ChannelAuthTaskVo channelAuthTaskVo,
                         ChannelAuthLockVo channelAuthLockVo, ChannelAuthUserVo channelAuthUserVo,
                         ChannelAuthChannelInfoVo channelAuthChannelInfoVo, JSONObject result);
}

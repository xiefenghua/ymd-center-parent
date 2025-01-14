package com.ymd.cloud.authorizeCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.entity.AuthListParamQuery;
import com.ymd.cloud.authorizeCenter.model.entity.ChannelAuthorizeEntity;
import com.ymd.cloud.common.utils.PageRequest;

public interface AuthorizeService  extends IService<AuthorizeCenterAuthorizeRecord> {
    JSONObject personAuthorize(ChannelAuthorizeEntity channelAuthorizeEntity);
    JSONObject channelAuthorize(ChannelAuthorizeEntity channelAuthorizeEntity);
    JSONObject updateAuth(AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord);
    JSONObject saveAuth(AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord);
    PageInfo<AuthorizeCenterAuthorizeRecord> authorizeList(PageRequest request, AuthListParamQuery authorizeRecordQuery);
    JSONObject delAuthorizeById(String ids);

    JSONObject delAuthorize(String lockId,String userAccount,String channelType,String channelValue);
    JSONObject delayAuthTimeById(String ids,String endTime);
    JSONObject delayAuthTime(String lockId,String userAccount,String channelType,String channelValue,String endTime);

    JSONObject optSyncAuthById(String ids);
    JSONObject optSyncAuth(String lockId,String userAccount,String channelType,String channelValue);
    JSONObject freezeAuthById(String authIds,Integer freezeStatus);
    JSONObject freezeAuth(String lockId,String userAccount,String channelType,String channelValue,Integer freezeStatus);

    JSONObject updateSyncStatus(Long authId,Integer sync,Integer uploadStatus);
    JSONObject syncAuthorize(AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord, String syncOptType,boolean isById);
    void delPersonnelAuthorizeCache(String taskNo);
    //发送授权mqtt
    void addChannelAuthorizeTask(ChannelAuthorizeEntity channelAuthorizeEntity);
    JSONObject authThreadToMqConsumerHandler(String taskNo);
    void processTaskByNoFinish();
    JSONObject processChannelAuthorize(AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord) throws InterruptedException;
    //组授权
    String batchProcessGroupAuth(String taskNo, ChannelAuthorizeEntity channelAuthorizeEntity);

    void insertAuthLog(String lockId,String module,String operateType,String operateName,String operation,String reqParam,String respResult);
}

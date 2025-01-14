package com.ymd.cloud.authorizeCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskResult;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.common.utils.PageRequest;

import java.util.List;

public interface AuthorizeCenterTaskResultService extends IService<AuthorizeCenterTaskResult> {
    String generateAuthNo();
    PageInfo<AuthorizeCenterTaskResult> listPage(PageRequest request, TaskParam queryParam);
    int saveTaskResultLog(TaskResultEntity taskResultEntity);
    int saveErrorTaskResultLog(ChannelAuthorizeEntity channelAuthorizeEntity, JSONObject result);
    int saveErrorTaskResultLog(BaseAuthorizeVo baseAuthorizeVo, ChannelAuthTaskVo channelAuthTaskVo,
                               ChannelAuthLockVo channelAuthLockVo, ChannelAuthUserVo channelAuthUserVo,
                               ChannelAuthChannelInfoVo channelAuthChannelInfoVo, JSONObject result);
    int saveErrorTaskResultLog(BaseAuthorizeVo baseAuthorizeVo, ChannelAuthTaskVo channelAuthTaskVo,
                               ChannelAuthLockVo channelAuthLockVo, ChannelAuthUserVo channelAuthUserVo,
                               ChannelAuthChannelInfoVo channelAuthChannelInfoVo, String msg);
    AuthorizeCenterTask getTaskResultCountInfoLog(AuthorizeCenterTask authorizeCenterTask);
    List<AuthorizeCenterTaskResult> selectByTaskNo(String taskNo);
    AuthorizeCenterTaskResult selectByAuthNo(String authNo);
    void delTaskResultBefore1Month();
    List<AuthorizeCenterTaskResult> listenResult(String taskNo,Long timeStamp);
}

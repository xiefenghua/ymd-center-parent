package com.ymd.cloud.authorizeCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterTaskMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskNotesService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskResultService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskService;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.common.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AuthorizeCenterTaskServiceImpl extends ServiceImpl<AuthorizeCenterTaskMapper, AuthorizeCenterTask> implements AuthorizeCenterTaskService {
    @Autowired
    AuthorizeCenterTaskNotesService authorizeCenterTaskNotesService;
    @Autowired
    AuthorizeCenterTaskResultService authorizeCenterTaskResultService;
    @Override
    public List<AuthorizeCenterTask> selectNoFinishTaskByTime(Integer retryCount,int pageSize) {
        return this.baseMapper.selectNoFinishTaskByTime(retryCount,pageSize) ;
    }

    @Override
    public String generateTaskNo(String taskNo) {
        if(EmptyUtil.isEmpty(taskNo)) {
            taskNo = UUIDUtil.generateNo();
        }
        AuthorizeCenterTask model = selectByTaskNo(taskNo);
        if(EmptyUtil.isNotEmpty(model)) {
            return generateTaskNo(taskNo);
        }
        return taskNo;
    }

    @Override
    public PageInfo<AuthorizeCenterTask> listPage(PageRequest request, TaskParam queryParam) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.getOrderBy());
        List<AuthorizeCenterTask> list=this.baseMapper.selectListPage(queryParam);
        PageInfo<AuthorizeCenterTask> pageInfo = new PageInfo<>(list);//进行分页查询
        return pageInfo;
    }

    @Override
    public String generateTaskNo() {
        return generateTaskNo(null);
    }
    @Override
    public String addAuthorizeCenterTask(ChannelAuthorizeEntity channelAuthorizeEntity) {
        BaseAuthorizeVo baseAuthorizeVo= channelAuthorizeEntity.getBaseAuthorizeVo();
        ChannelAuthTaskVo channelAuthTaskVo = channelAuthorizeEntity.getChannelAuthTaskVo();
        AuthorizeCenterTask authorizeCenterTask=new AuthorizeCenterTask();
        authorizeCenterTask.setTaskStatus(0);//0=提交 1=完成  2=执行中 3=失败 4=异常
        authorizeCenterTask.setTaskType(baseAuthorizeVo.getAuthResource());
        authorizeCenterTask.setAuthStartTime(baseAuthorizeVo.getStartTime());
        authorizeCenterTask.setAuthEndTime(baseAuthorizeVo.getEndTime());
        authorizeCenterTask.setOperator(baseAuthorizeVo.getCreateUserAccount());
        authorizeCenterTask.setOrgId(baseAuthorizeVo.getOrgId());
        authorizeCenterTask.setTaskNo(channelAuthTaskVo.getTaskNo());
        //判断任务是否存在
        AuthorizeCenterTask queryModel=this.baseMapper.selectByTaskNo(channelAuthTaskVo.getTaskNo());
        if(EmptyUtil.isNotEmpty(queryModel)) {
            authorizeCenterTask.setUpdateBy(baseAuthorizeVo.getCreateUserAccount());
            authorizeCenterTask.setUpdateTime(new Date());
            authorizeCenterTask.setId(queryModel.getId());
            this.baseMapper.updateById(authorizeCenterTask);
        }else {
            authorizeCenterTask.setCreateTime(new Date());
            authorizeCenterTask.setCreateBy(baseAuthorizeVo.getCreateUserAccount());
            this.baseMapper.insert(authorizeCenterTask);
        }
        return channelAuthTaskVo.getTaskNo();
    }

    @Override
    public AuthorizeCenterTask selectByTaskNo(String taskNo) {
        return this.baseMapper.selectByTaskNo(taskNo);
    }

    @Override
    public void updateSyncStatusAndTaskAuthCountByTaskNo(String taskNo, Integer taskStatus, Long taskAuthCount) {
        this.baseMapper.updateSyncStatusAndTaskAuthCountByTaskNo(taskNo,taskStatus,taskAuthCount);
    }

    @Override
    public void updateSyncStatusByTaskNo(String taskNo, Integer taskStatus) {
        updateSyncStatusByTaskNo(taskNo,taskStatus,null);
    }

    @Override
    public void updateSyncStatusByTaskNo(String taskNo, Integer taskStatus, String exceptionDesc) {
        this.baseMapper.updateSyncStatusByTaskNo(taskNo,taskStatus,exceptionDesc);
    }

    @Override
    public List<AuthorizeCenterTask> selectTaskAuthCountNull() {
        return this.baseMapper.selectTaskAuthCountNull();
    }

    @Override
    public void updateRetryCountByTaskNo(String taskNo) {
        this.baseMapper.updateRetryCountByTaskNo(taskNo);
    }
    @Override
    public void updateUpdateTimeByTaskNo(String taskNo) {
        this.baseMapper.updateUpdateTimeByTaskNo(taskNo);
    }
    @Override
    public void delTaskBefore1Month() {
        this.baseMapper.delTaskBefore1Month();
    }

    @Override
    public void updateAuthCountByTaskNo(AuthorizeCenterTask authorizeCenterTask) {
        this.baseMapper.updateAuthCountByTaskNo(authorizeCenterTask);
    }
    @Override
    public void updateExceptionNull() {
        this.baseMapper. updateExceptionNull();
    }
    @Override
    public void toTaskValiError(ChannelAuthorizeEntity channelAuthorizeEntity, JSONObject result) {
        BaseAuthorizeVo baseAuthorizeVo= channelAuthorizeEntity.getBaseAuthorizeVo();
        ChannelAuthTaskVo channelAuthTaskVo = channelAuthorizeEntity.getChannelAuthTaskVo();
        toTaskValiError(baseAuthorizeVo, channelAuthTaskVo,null, null, null, result);
    }

    @Override
    public void toTaskValiError(BaseAuthorizeVo baseAuthorizeVo, ChannelAuthTaskVo channelAuthTaskVo,
                                ChannelAuthLockVo channelAuthLockVo, ChannelAuthUserVo channelAuthUserVo,
                                ChannelAuthChannelInfoVo channelAuthChannelInfoVo, JSONObject result) {
        AuthorizeCenterTask queryModel = selectByTaskNo(channelAuthTaskVo.getTaskNo());
        if (EmptyUtil.isNotEmpty(queryModel)) {
            queryModel.setUpdateTime(new Date());
            queryModel.setUpdateBy(baseAuthorizeVo.getCreateUserAccount());
            queryModel.setTaskStatus(0);
            updateById(queryModel);
        }
        authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo,
                channelAuthLockVo, channelAuthUserVo,
                channelAuthChannelInfoVo,result);
    }

}

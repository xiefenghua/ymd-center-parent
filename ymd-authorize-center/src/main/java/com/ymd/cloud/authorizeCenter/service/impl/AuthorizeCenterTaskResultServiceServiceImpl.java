package com.ymd.cloud.authorizeCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterTaskMapper;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterTaskResultMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskNotes;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskResult;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskResultService;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthorizeCenterTaskResultServiceServiceImpl extends ServiceImpl<AuthorizeCenterTaskResultMapper, AuthorizeCenterTaskResult> implements AuthorizeCenterTaskResultService {
    @Resource
    AuthorizeCenterTaskMapper authorizeCenterTaskMapper;
    @Autowired
    RedisService redisService;
    @Override
    public String generateAuthNo() {
        String authNo= UUIDUtil.generateNo();
        AuthorizeCenterTaskResult model =this.baseMapper.selectByAuthNo(authNo);
        if(EmptyUtil.isNotEmpty(model)) {
            return generateAuthNo();
        }
        return authNo;
    }

    @Override
    public PageInfo<AuthorizeCenterTaskResult> listPage(PageRequest request, TaskParam queryParam) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.getOrderBy());
        List<AuthorizeCenterTaskResult> list=this.baseMapper.selectListPage(queryParam);
        PageInfo<AuthorizeCenterTaskResult> pageInfo = new PageInfo<>(list);//进行分页查询
        return pageInfo;
    }

    @Override
    public int saveTaskResultLog(TaskResultEntity taskResultEntity) {
        try {
            AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord =taskResultEntity.getAuthorizeCenterAuthorizeRecord();
            AuthorizeCenterTaskNotes authorizeCenterTaskNotes=taskResultEntity.getAuthorizeCenterTaskNotes();
            JSONObject result=taskResultEntity.getResult();

            AuthorizeCenterTaskResult authorizeCenterTaskResult =new AuthorizeCenterTaskResult();
            authorizeCenterTaskResult.setAuthId(result.getString("authId"));
            authorizeCenterTaskResult.setTaskNo(authorizeCenterTaskNotes.getTaskNo());
            authorizeCenterTaskResult.setAuthNo(EmptyUtil.isEmpty(authorizeCenterTaskNotes.getAuthNo())?generateAuthNo():authorizeCenterTaskNotes.getAuthNo());
            authorizeCenterTaskResult.setLockId(authorizeCenterAuthorizeRecord.getLockId());
            authorizeCenterTaskResult.setUserAccount(authorizeCenterAuthorizeRecord.getUserAccount());
            authorizeCenterTaskResult.setChannelType(authorizeCenterAuthorizeRecord.getAuthChannelType());
            authorizeCenterTaskResult.setCreateBy(authorizeCenterAuthorizeRecord.getCreateBy());
            authorizeCenterTaskResult.setChannelValue(authorizeCenterAuthorizeRecord.getAuthChannelValue());
            authorizeCenterTaskResult.setUpdateBy(authorizeCenterAuthorizeRecord.getUpdateBy());
            authorizeCenterTaskResult.setStatus(result.getIntValue("status"));
            String val=authorizeCenterTaskResult.getChannelValue();
            if (EmptyUtil.isNotEmpty(val)&&val.length()>32) {
                val = val.substring(0,32);
            }
            String returnMsg = "介质:[" + authorizeCenterAuthorizeRecord.getChannelName() +"("+val+")" + "],锁名称：["
                    + authorizeCenterAuthorizeRecord.getLockName()+"("+authorizeCenterAuthorizeRecord.getLockMac()+")" + "] 处理结果：[" +
                    result.getString("message") + "]；";
            authorizeCenterTaskResult.setResult(returnMsg);

            if(authorizeCenterTaskResult.getStatus()== Constants.SUCCESS_CODE){
                authorizeCenterTaskMapper.updateAuthSuccessCountByTaskNo(authorizeCenterTaskResult.getTaskNo());
            }else{
                authorizeCenterTaskMapper.updateAuthFailureCountByTaskNo(authorizeCenterTaskResult.getTaskNo());
            }
            authorizeCenterTaskResult.setCreateTime(new Date());
            authorizeCenterTaskResult.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
            AuthorizeCenterTaskResult taskResult = selectByAuthNo(authorizeCenterTaskResult.getAuthNo());
            if (EmptyUtil.isEmpty(taskResult)) {
                return this.baseMapper.insert(authorizeCenterTaskResult);
            }else{
                return 1;
            }
        }catch(Exception e){
            log.error("saveTaskResultLog===",e);
        }
        return 0;
    }

    /**
     * 授权失败后保存执行记录
     * @param channelAuthorizeEntity
     * @param result
     * @return
     */
    @Override
    public int saveErrorTaskResultLog(ChannelAuthorizeEntity channelAuthorizeEntity, JSONObject result) {
        BaseAuthorizeVo baseAuthorizeVo= channelAuthorizeEntity.getBaseAuthorizeVo();
        ChannelAuthTaskVo channelAuthTaskVo = channelAuthorizeEntity.getChannelAuthTaskVo();
        saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo,null,null, null, result.getString("message") );
        return 1;
    }
    /**
     * 授权失败后保存执行记录
     * @return
     */
    @Override
    public int saveErrorTaskResultLog(BaseAuthorizeVo baseAuthorizeVo, ChannelAuthTaskVo channelAuthTaskVo,
                                      ChannelAuthLockVo channelAuthLockVo,
                                      ChannelAuthUserVo channelAuthUserVo, ChannelAuthChannelInfoVo channelAuthChannelInfoVo, JSONObject result) {
        saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, channelAuthLockVo, channelAuthUserVo, channelAuthChannelInfoVo, result.getString("message") );
        return 1;
    }
    /**
     * 授权失败后保存执行记录
     * @return
     */
    @Override
    public int saveErrorTaskResultLog(BaseAuthorizeVo baseAuthorizeVo, ChannelAuthTaskVo channelAuthTaskVo,
                                      ChannelAuthLockVo channelAuthLockVo,
                                      ChannelAuthUserVo channelAuthUserVo, ChannelAuthChannelInfoVo channelAuthChannelInfoVo, String msg) {
        try {
            String authNo=EmptyUtil.isEmpty(channelAuthTaskVo.getAuthNo())?generateAuthNo(): channelAuthTaskVo.getAuthNo();
            AuthorizeCenterTaskResult authorizeCenterTaskResult =new AuthorizeCenterTaskResult();
            authorizeCenterTaskResult.setTaskNo(channelAuthTaskVo.getTaskNo());
            authorizeCenterTaskResult.setAuthNo(authNo);

            if(EmptyUtil.isNotEmpty(channelAuthLockVo)) {
                authorizeCenterTaskResult.setLockId(channelAuthLockVo.getLockId());
            }else{
                authorizeCenterTaskResult.setLockId("all");
            }
            if(EmptyUtil.isNotEmpty(channelAuthUserVo)) {
                authorizeCenterTaskResult.setUserAccount(channelAuthUserVo.getUserAccount());
            }else{
                authorizeCenterTaskResult.setUserAccount("all");
            }
            if(EmptyUtil.isNotEmpty(channelAuthChannelInfoVo)) {
                authorizeCenterTaskResult.setChannelType(channelAuthChannelInfoVo.getAuthChannelType());
                authorizeCenterTaskResult.setChannelValue(channelAuthChannelInfoVo.getAuthChannelValue());
            }else{
                authorizeCenterTaskResult.setChannelType("all");
                authorizeCenterTaskResult.setChannelValue("all");
            }

            authorizeCenterTaskResult.setCreateBy(baseAuthorizeVo.getCreateUserAccount());
            authorizeCenterTaskResult.setStatus(Constants.FAIL_CODE);//记录失败数据
            String returnMsg = "处理结果：[" +msg + "]；";
            authorizeCenterTaskResult.setResult(returnMsg);
            authorizeCenterTaskResult.setCreateTime(new Date());
            authorizeCenterTaskResult.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
            return this.baseMapper.insert(authorizeCenterTaskResult);
        }catch(Exception e){
        }
        return 0;
    }

    @Override
    public AuthorizeCenterTask getTaskResultCountInfoLog(AuthorizeCenterTask authorizeCenterTask) {
        try {
            List<AuthorizeCenterTaskResult> logList = selectByTaskNo(authorizeCenterTask.getTaskNo());
            int authSuccessCount=0;
            int authFailureCount=0;
            int valiFailureCount=0;
            if(EmptyUtil.isNotEmpty(logList)) {
                for (int i = 0; i < logList.size(); i++) {
                    AuthorizeCenterTaskResult taskResult = logList.get(i);
                    boolean returnIsSuccess = taskResult.getStatus()== Constants.SUCCESS_CODE?true:false;
                    if (returnIsSuccess) {
                        authSuccessCount++;
                    } else {
                        if(EmptyUtil.isEmpty(taskResult.getAuthId())){
                            valiFailureCount++;
                        }else {
                            authFailureCount++;
                        }
                    }
                }
            }
            authorizeCenterTask.setAuthSuccessCount(authSuccessCount);
            authorizeCenterTask.setAuthFailureCount(authFailureCount);
            authorizeCenterTask.setValiFailureCount(valiFailureCount);
            authorizeCenterTask.setTaskCount(authSuccessCount+authFailureCount+valiFailureCount);
        }catch(Exception e){log.error("系统错误:{}",e);}
        return authorizeCenterTask;
    }

    @Override
    public List<AuthorizeCenterTaskResult> selectByTaskNo(String taskNo) {
        return this.baseMapper.selectByTaskNo(taskNo);
    }

    @Override
    public AuthorizeCenterTaskResult selectByAuthNo(String authNo) {
        return this.baseMapper.selectByAuthNo(authNo);
    }

    @Override
    public void delTaskResultBefore1Month() {
        this.baseMapper.delTaskResultBefore1Month();
    }
    /**
     * 更新异步转同步算法方法
     * @param taskNo
     * @param timeStemp
     * @return
     */
    @Override
    public List<AuthorizeCenterTaskResult> listenResult(String taskNo,Long timeStemp) {
        List<AuthorizeCenterTaskResult> results=null;
        try {
            long timeoutMills = 30000;
            final long endTimeMills = timeStemp + timeoutMills;
            while (EmptyUtil.isEmpty(results)) {
                long currTime=System.currentTimeMillis();
                // 超时判断
                if( currTime>= endTimeMills) {
                    log.info("异步转同步结果超时, 开始时间:{} 超时时间点:{} 设置超时{}秒", DateUtil.format(timeStemp),DateUtil.format(endTimeMills),timeoutMills/1000 );
                    return results;
                }else {
                    //判断任务是否完成 0=提交 1=完成  2=执行中 3=失败 4=异常
                    AuthorizeCenterTask authorizeCenterTask=authorizeCenterTaskMapper.selectByTaskNo(taskNo);
                    if(EmptyUtil.isNotEmpty(authorizeCenterTask)&&authorizeCenterTask.getTaskStatus()!=0&&authorizeCenterTask.getTaskStatus()!=2){
                        results= selectByTaskNo(taskNo);
                    }
                    if(EmptyUtil.isEmpty(results)) {
                        //循环等待一下
                        TimeUnit.MILLISECONDS.sleep(200);
                    }
                }
            }
        } catch (InterruptedException e) {
        }
        return results;
    }
}

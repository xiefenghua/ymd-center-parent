package com.ymd.cloud.authorizeCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.enums.AuthConstants;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.mapper.*;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockMapper;
import com.ymd.cloud.authorizeCenter.model.*;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.service.*;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.enumsSupport.EventCenterConstant;
import com.ymd.cloud.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AuthorizeServiceImpl extends ServiceImpl<AuthorizeCenterAuthorizeRecordMapper, AuthorizeCenterAuthorizeRecord> implements AuthorizeService {
    @Autowired
    AuthorizeCenterTaskResultService authorizeCenterTaskResultService;
    @Autowired
    AuthorizeCenterTaskNotesService authorizeCenterTaskNotesService;
    @Autowired
    AuthorizeCenterTaskService authorizeCenterTaskService;
    @Autowired
    RedisService redisService;
    @Autowired
    AuthorizeSyncService authorizeSyncService;
    @Autowired
    ConsumeChannelAuthTaskNotesService consumeChannelAuthTaskNotesService;
    @Resource
    AuthorizeCenterAuthorizeLogMapper authorizeCenterAuthorizeLogMapper;
    @Resource
    AuthorizeCenterAuthorizeLockChangeMapper authorizeCenterAuthorizeLockChangeMapper;
    @Resource
    AuthorizeCenterAuthorizeTemplateDataMapper authorizeCenterAuthorizeTemplateDataMapper;
    @Resource
    AuthorizeCenterGroupAuthorizeRelationMapper authorizeCenterGroupAuthorizeRelationMapper;
    @Resource
    LockMapper lockMapper;
    @Resource
    AuthorizeCenterPersonAuthorizeMapper authorizeCenterPersonAuthorizeMapper;
    EventCenterServiceClientApi eventCenterServiceClientApi;
    @Override
    public PageInfo<AuthorizeCenterAuthorizeRecord> authorizeList(PageRequest request, AuthListParamQuery authorizeRecordQuery) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.getOrderBy());
        List<AuthorizeCenterAuthorizeRecord> authList=this.baseMapper.selectAuthListPage(authorizeRecordQuery);
        PageInfo<AuthorizeCenterAuthorizeRecord> pageInfo = new PageInfo<>(authList);//进行分页查询
        return pageInfo;
    }
    @Override
    public JSONObject delAuthorizeById(String ids) {
        JSONObject result = new JSONObject();
        try {
            if (EmptyUtil.isEmpty(ids)) {
                result.put("status", ErrorCodeEnum.FAIL.code());
                result.put("message", "请上传删除授权id");
            } else {
                for (String id:ids.split(",")) {
                    AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord=this.baseMapper.selectById(Long.valueOf(id));
                    if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord)) {
                        //删除数据同步到锁上
                        syncAuthorize(authorizeCenterAuthorizeRecord,"删除",false);
                        //删除数据
                        authorizeCenterAuthorizeRecord.setStatus(Constants.STATUS_DEL_CODE);
                        authorizeCenterAuthorizeRecord.setFlag(Constants.NOT_FLAG);
                        authorizeCenterAuthorizeRecord.setUpdateTime(new Date());
                        authorizeCenterAuthorizeRecord.setUpdateBy(null);
                        updateAuth(authorizeCenterAuthorizeRecord);
                        insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),authorizeCenterAuthorizeRecord.getAuthResource(),"删除","根据id删除授权数据",authorizeCenterAuthorizeRecord
                                .getUpdateBy(),"{\"id\":"+id+"}",result.toJSONString());
                    }
                }
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
            }
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject delAuthorize(String lockId,String userAccount,String channelType,String channelValue) {
        JSONObject result = new JSONObject();
        try {
            AuthListParamQuery authorizeRecordQuery=new AuthListParamQuery();
            authorizeRecordQuery.setAuthChannelType(channelType);
            authorizeRecordQuery.setLockId(lockId);
            authorizeRecordQuery.setUserAccount(userAccount);
            authorizeRecordQuery.setAuthChannelValue(channelValue);
            List<AuthorizeCenterAuthorizeRecord> authList=this.baseMapper.selectAuthListByDeviceUser(authorizeRecordQuery);
            if(EmptyUtil.isNotEmpty(authList)){
                for (AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord: authList) {
                    delAuthorizeById(authorizeCenterAuthorizeRecord.getId()+"");
                }
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
            }else{
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "查询条件未查找需要删除的数据");
            }
            insertAuthLog(lockId,"授权","删除","根据锁|人员|介质，删除授权数据",userAccount
                    ,JSONObject.toJSONString(authorizeRecordQuery),result.toJSONString());
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject delayAuthTimeById(String ids,String endTime) {
        JSONObject result = new JSONObject();
        try {
            if (EmptyUtil.isEmpty(ids)) {
                result.put("status", ErrorCodeEnum.FAIL.code());
                result.put("message", "请上传延期授权id");
            } else {
                for (String id:ids.split(",")) {
                    AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord=this.baseMapper.selectById(Long.valueOf(id));
                    if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord)) {
                        authorizeCenterAuthorizeRecord.setStatus(Constants.STATUS_CODE);
                        authorizeCenterAuthorizeRecord.setFlag(Constants.FAIL_CODE);
                        authorizeCenterAuthorizeRecord.setFreezeStatus(Constants.FREEZE_STATUS_DONE);
                        authorizeCenterAuthorizeRecord.setEndTime(DateUtil.parse(endTime,DateUtil.yyyy_MM_ddHH_mm_ss));
                        JSONObject jobResult =updateAuth(authorizeCenterAuthorizeRecord);
                        syncAuthorize(authorizeCenterAuthorizeRecord,"延期",true);
                        insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),authorizeCenterAuthorizeRecord.getAuthResource(),"延期","根据id延期授权数据",authorizeCenterAuthorizeRecord
                                .getUpdateBy(),"{\"id\":"+id+"}",jobResult.toJSONString());
                    }
                }
                optSyncAuthById(ids);
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
            }
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject delayAuthTime(String lockId, String userAccount, String channelType, String channelValue,String endTime) {
        JSONObject result = new JSONObject();
        try {
            AuthListParamQuery authorizeRecordQuery=new AuthListParamQuery();
            authorizeRecordQuery.setAuthChannelType(channelType);
            authorizeRecordQuery.setLockId(lockId);
            authorizeRecordQuery.setUserAccount(userAccount);
            authorizeRecordQuery.setAuthChannelValue(channelValue);
            List<AuthorizeCenterAuthorizeRecord> authList=this.baseMapper.selectAuthListByDeviceUser(authorizeRecordQuery);
            if(EmptyUtil.isNotEmpty(authList)){
                for (AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord: authList) {
                    delayAuthTimeById(authorizeCenterAuthorizeRecord.getId()+"",endTime);
                }
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
            }else{
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "查询条件未查找需要延期的数据");
            }
            insertAuthLog(lockId,"授权","延期","根据锁|人员|介质，延期授权数据",userAccount
                    ,JSONObject.toJSONString(authorizeRecordQuery),result.toJSONString());
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject updateAuth(AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord) {
        JSONObject result = new JSONObject();
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "成功");
        try {
            String authResource=EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord.getAuthResource())?authorizeCenterAuthorizeRecord.getAuthResource():AuthConstants.channelAuthResource;
            authorizeCenterAuthorizeRecord.setUpdateTime(new Date());
            authorizeCenterAuthorizeRecord.setUpdateBy(authorizeCenterAuthorizeRecord.getCreateBy());
            this.baseMapper.updateById(authorizeCenterAuthorizeRecord);
            insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),authResource,"更新","更新授权数据",authorizeCenterAuthorizeRecord.getUserAccount()
                    ,JSONObject.toJSONString(authorizeCenterAuthorizeRecord),result.toJSONString());
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject saveAuth(AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord) {
        JSONObject result = new JSONObject();
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "授权数据入库成功");
        try {
            String authResource=EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord.getAuthResource())?authorizeCenterAuthorizeRecord.getAuthResource():AuthConstants.channelAuthResource;

            AuthListParamQuery authorizeRecordQuery=new AuthListParamQuery();
            authorizeRecordQuery.setAuthChannelType(authorizeCenterAuthorizeRecord.getAuthChannelType());
            authorizeRecordQuery.setLockId(authorizeCenterAuthorizeRecord.getLockId());
            authorizeRecordQuery.setUserAccount(authorizeCenterAuthorizeRecord.getUserAccount());
            authorizeRecordQuery.setAuthChannelValue(authorizeCenterAuthorizeRecord.getAuthChannelValue());
            authorizeRecordQuery.setStartDate(authorizeCenterAuthorizeRecord.getStartTime());
            authorizeRecordQuery.setEndDate(authorizeCenterAuthorizeRecord.getEndTime());
            List<AuthorizeCenterAuthorizeRecord> equalAuthList=this.baseMapper.selectEqualAuthListByLockAndUserAndType(authorizeRecordQuery);
            if(EmptyUtil.isNotEmpty(equalAuthList)&&equalAuthList.size()!=0) {
                AuthorizeCenterAuthorizeRecord queryModel=equalAuthList.get(0);
                authorizeCenterAuthorizeRecord.setAuthEqualUuid(queryModel.getAuthEqualUuid());
                authorizeCenterAuthorizeRecord.setId(queryModel.getId());
                result=updateAuth(authorizeCenterAuthorizeRecord);
            }else {
                authorizeCenterAuthorizeRecord.setAuthEqualUuid(generateEqualUuid());
                authorizeCenterAuthorizeRecord.setCreateTime(new Date());
                //保存授权记录t_authorize_center_authorize_record
                this.baseMapper.insert(authorizeCenterAuthorizeRecord);
                insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),authResource,"添加","添加授权数据",authorizeCenterAuthorizeRecord.getUserAccount()
                        ,JSONObject.toJSONString(authorizeCenterAuthorizeRecord),result.toJSONString());
            }
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject optSyncAuthById(String ids) {
        JSONObject result = new JSONObject();
        try {
            if (EmptyUtil.isEmpty(ids)) {
                result.put("status", ErrorCodeEnum.FAIL.code());
                result.put("message", "请上传同步授权id");
            } else {
                for (String id:ids.split(",")) {
                    AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord=this.baseMapper.selectById(Long.valueOf(id));
                    if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord)) {
                        JSONObject syncResult = syncAuthorize(authorizeCenterAuthorizeRecord,"同步",true);
                        insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),authorizeCenterAuthorizeRecord.getAuthResource(),"同步","根据id立即同步授权数据",authorizeCenterAuthorizeRecord
                                .getUpdateBy(),"{\"id\":"+id+"}",syncResult.toJSONString());
                    }
                }
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
            }
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject optSyncAuth(String lockId, String userAccount, String channelType, String channelValue) {
        JSONObject result = new JSONObject();
        try {
            AuthListParamQuery authorizeRecordQuery=new AuthListParamQuery();
            authorizeRecordQuery.setAuthChannelType(channelType);
            authorizeRecordQuery.setLockId(lockId);
            authorizeRecordQuery.setUserAccount(userAccount);
            authorizeRecordQuery.setAuthChannelValue(channelValue);
            List<AuthorizeCenterAuthorizeRecord> authList=this.baseMapper.selectAuthListByDeviceUser(authorizeRecordQuery);
            if(EmptyUtil.isNotEmpty(authList)){
                for (AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord: authList) {
                    optSyncAuthById(authorizeCenterAuthorizeRecord.getId()+"");
                }
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
            }else{
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "查询条件未查找需要同步的数据");
            }
            insertAuthLog(lockId,"授权","同步","根据锁|人员|介质，同步授权数据",userAccount
                    ,JSONObject.toJSONString(authorizeRecordQuery),result.toJSONString());
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject syncAuthorize(AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord, String syncOptType,boolean isById) {
        JSONObject result = new JSONObject();
        try {
            Date startTime=authorizeCenterAuthorizeRecord.getStartTime();
            long curr=System.currentTimeMillis();
            Lock lock=lockMapper.selectByLockId(authorizeCenterAuthorizeRecord.getLockId());
            if(lock!=null){
                authorizeCenterAuthorizeRecord.setLockMac(lock.getMac());
            }
            String cmd=syncOptType;
            String syncName=authorizeCenterAuthorizeRecord.getUserAccount()+"账号对 "+authorizeCenterAuthorizeRecord.getLockMac()+"锁进行"+cmd
                    +ChannelTypeEnums.getModuleTypeMapName(authorizeCenterAuthorizeRecord.getAuthChannelModuleType())+"授权同步";
            if(isById){
                authorizeSyncService.saveAuthorizeSyncByAuthId(authorizeCenterAuthorizeRecord.getId(), authorizeCenterAuthorizeRecord.getAuthEqualUuid(),
                        syncName, Constants.NOT_SYNC, Constants.NOT_UPLOAD_STATUS, Constants.NOT_PUSH_STATUS
                        , syncOptType, authorizeCenterAuthorizeRecord.getCreateBy(), authorizeCenterAuthorizeRecord.getUpdateBy());
            }else {
                authorizeSyncService.saveAuthorizeSyncByEqualUuid(authorizeCenterAuthorizeRecord.getId(), authorizeCenterAuthorizeRecord.getAuthEqualUuid(),
                        syncName, Constants.NOT_SYNC, Constants.NOT_UPLOAD_STATUS, Constants.NOT_PUSH_STATUS
                        , syncOptType, authorizeCenterAuthorizeRecord.getCreateBy(), authorizeCenterAuthorizeRecord.getUpdateBy());
            }
            //判断当前授权开始时间是否达到前10分钟标准值，是否添加到同步
            if(startTime.getTime()-curr<=AuthConstants.authTimeDiff){
                //发送同步下发事件中心，触发同步机制，事件同步结果执行以下代码
                ChannelAuthLockVo channelAuthLockVo =new ChannelAuthLockVo();
                channelAuthLockVo.setLockId(authorizeCenterAuthorizeRecord.getLockId());
                channelAuthLockVo.setLockMac(authorizeCenterAuthorizeRecord.getLockMac());
                channelAuthLockVo.setLockName(authorizeCenterAuthorizeRecord.getLockName());
                channelAuthLockVo.setLockType(authorizeCenterAuthorizeRecord.getLockType());
                //TOPIC:authorize_center_channel_3_sync_auth_down_topic
                String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.user_authorize_sync.name());
                EventCenterPush eventCenterPush=new EventCenterPush();
                eventCenterPush.setCreateAuthUserId(authorizeCenterAuthorizeRecord.getCreateBy());
                Integer authDelayTime=getDelayTime(startTime,-10*60);
                eventCenterPush.setDelayTime(authDelayTime);
                eventCenterPush.setMsgBody(JSONObject.toJSONString(channelAuthLockVo));
                eventCenterPush.setRemark("该["+syncName+"]会在"+(DateUtil.second2Time(Long.valueOf(authDelayTime)))+"后,将自动创建同步任务，授权下发到锁中");
                eventCenterPush.setTopic(topic);
                eventCenterServiceClientApi.push(eventCenterPush);
            }
            result.put("status", ErrorCodeEnum.SUCCESS.code());
            result.put("message", "成功");
            insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),"授权","同步","根据锁|人员|介质，同步授权数据",authorizeCenterAuthorizeRecord.getUpdateBy()
                    ,JSONObject.toJSONString(authorizeCenterAuthorizeRecord),result.toJSONString());
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    private Integer getDelayTime(Date endTime,Integer diff){
        Long second=DateUtil.curr_between_days(DateUtil.format(endTime.getTime()))/1000;
        Integer delayTime=Integer.valueOf((second+diff)+"");
        return delayTime;
    }

    @Override
    public JSONObject updateSyncStatus(Long authId,Integer sync,Integer uploadStatus) {
        JSONObject result = new JSONObject();
        try {
            if (EmptyUtil.isEmpty(authId)) {
                result.put("status", ErrorCodeEnum.FAIL.code());
                result.put("message", "请上传授权id");
            } else {
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
                AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord=this.baseMapper.selectById(authId);
                if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord)) {
                    Lock lock=lockMapper.selectByLockId(authorizeCenterAuthorizeRecord.getLockId());
                    if(lock!=null){
                        authorizeCenterAuthorizeRecord.setLockMac(lock.getMac());
                    }
                    String cmd="";
                    String syncName=authorizeCenterAuthorizeRecord.getUserAccount()+"账号对"+authorizeCenterAuthorizeRecord.getLockMac()+"锁进行"+cmd
                            +ChannelTypeEnums.getModuleTypeMapName(authorizeCenterAuthorizeRecord.getAuthChannelModuleType())+"授权同步";
                    authorizeSyncService.saveAuthorizeSyncByEqualUuid(authorizeCenterAuthorizeRecord.getId(),authorizeCenterAuthorizeRecord.getAuthEqualUuid(),syncName
                            ,sync,uploadStatus,Constants.PUSH_STATUS
                            ,"授权处理时,更新同步状态",authorizeCenterAuthorizeRecord.getCreateBy(),authorizeCenterAuthorizeRecord.getUpdateBy());
                    insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),authorizeCenterAuthorizeRecord.getAuthResource(),"授权处理时,更新同步状态","根据id更新同步状态授权数据",authorizeCenterAuthorizeRecord
                            .getUpdateBy(),"{\"id\":"+authId+"}",result.toJSONString());
                }
            }
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject freezeAuth(String lockId, String userAccount, String channelType, String channelValue, Integer freezeStatus) {
        JSONObject result = new JSONObject();
        try {
            AuthListParamQuery authorizeRecordQuery=new AuthListParamQuery();
            authorizeRecordQuery.setAuthChannelType(channelType);
            authorizeRecordQuery.setLockId(lockId);
            authorizeRecordQuery.setUserAccount(userAccount);
            authorizeRecordQuery.setAuthChannelValue(channelValue);
            List<AuthorizeCenterAuthorizeRecord> authList=this.baseMapper.selectAuthListByDeviceUser(authorizeRecordQuery);
            if(EmptyUtil.isNotEmpty(authList)){
                for (AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord: authList) {
                    freezeAuthById(authorizeCenterAuthorizeRecord.getId()+"",freezeStatus);
                }
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
            }else{
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "查询条件未查找需要冻结的数据");
            }
            insertAuthLog(lockId,"授权","冻结","根据锁|人员|介质，冻结授权数据",userAccount
                    ,JSONObject.toJSONString(authorizeRecordQuery),result.toJSONString());
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    /**
     * 冻结授权
     * @param authIds
     * @return
     */
    @Override
    public JSONObject freezeAuthById(String authIds,Integer freezeStatus){
        JSONObject result = new JSONObject();
        try {
            if (EmptyUtil.isEmpty(authIds)) {
                result.put("status", ErrorCodeEnum.FAIL.code());
                result.put("message", "请上传同步授权id");
            } else {
                for (String id:authIds.split(",")) {
                    AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord=this.baseMapper.selectById(Long.valueOf(id));
                    if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord)&&
                            authorizeCenterAuthorizeRecord.getFreezeStatus()!=freezeStatus) {
                        authorizeCenterAuthorizeRecord.setStatus(Constants.STATUS_CODE);
                        authorizeCenterAuthorizeRecord.setFlag(Constants.FAIL_CODE);
                        authorizeCenterAuthorizeRecord.setFreezeStatus(freezeStatus);
                        updateAuth(authorizeCenterAuthorizeRecord);
                        JSONObject syncResult = syncAuthorize(authorizeCenterAuthorizeRecord,"冻结",true);
                        insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),authorizeCenterAuthorizeRecord.getAuthResource(),"冻结","根据id冻结授权数据",authorizeCenterAuthorizeRecord
                                .getUpdateBy(),"{\"id\":"+id+"}",syncResult.toJSONString());
                    }
                }
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
            }
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    //#########################发送授权任务mqtt##########################################
    //发送授权任务mqtt 调用task添加后调用此方法
    @Override
    public void addChannelAuthorizeTask(ChannelAuthorizeEntity channelAuthorizeEntity){
        authorizeCenterTaskService.addAuthorizeCenterTask(channelAuthorizeEntity);
        String key= channelAuthorizeEntity.getChannelAuthTaskVo().getTaskNo()+"-authObj";
        redisService.set(key,JSONObject.toJSONString(channelAuthorizeEntity),3600*24*7);
        //TODO TEST
        if(eventCenterServiceClientApi==null){
            authThreadToMqConsumerHandler( channelAuthorizeEntity.getChannelAuthTaskVo().getTaskNo());
            return;
        }

        //需要调用mqtt 发送授权任务mqtt  通过事件中心发送mqtt  authorize_center_channel_5_auth_thread_to_mq_push_topic
        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.auth_thread_to_mq.name());
        EventCenterPush eventCenterPush=new EventCenterPush();
        eventCenterPush.setCreateAuthUserId(channelAuthorizeEntity.getBaseAuthorizeVo().getCreateUserAccount());
        JSONObject msgBody=new JSONObject();
        msgBody.put("taskNo", channelAuthorizeEntity.getChannelAuthTaskVo().getTaskNo());
        eventCenterPush.setMsgBody(msgBody.toJSONString());
        eventCenterPush.setRemark("该任务号：["+ channelAuthorizeEntity.getChannelAuthTaskVo().getTaskNo()+"]发送授权任务mqtt");
        eventCenterPush.setTopic(topic);
        eventCenterServiceClientApi.push(eventCenterPush);
    }
    //消费授权任务，检验授权参数
    //TODO authorize_center_channel_5_auth_thread_to_mq_push_topic消费
    @Override
    public JSONObject authThreadToMqConsumerHandler(String taskNo) {
        JSONObject result = new JSONObject();
        try {
            if(!redisService.exists(taskNo+"-obj")) {
                redisService.set(taskNo+"-obj","true",3600*24*7);
                String key=taskNo+"-authObj";
                String objStr =  redisService.get(key);
                if(StringUtils.isNotBlank(objStr)) {
                    ChannelAuthorizeEntity channelAuthorizeEntity = JSONObject.parseObject(objStr, ChannelAuthorizeEntity.class);
                    ChannelAuthTaskVo channelAuthTaskVo = channelAuthorizeEntity.getChannelAuthTaskVo();
                    channelAuthTaskVo.setTaskNo(taskNo);
                    channelAuthorizeEntity.setChannelAuthTaskVo(channelAuthTaskVo);
                    //判断是否为人员授权还是介质授权
                    BaseAuthorizeVo baseAuthorizeVo= channelAuthorizeEntity.getBaseAuthorizeVo();
                    if(AuthConstants.personAuthResource.equals(baseAuthorizeVo.getAuthResource())){
                        personAuthorize(channelAuthorizeEntity);
                    }else{
                        channelAuthorize(channelAuthorizeEntity);
                    }
                }
            }
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    //#########################所有合格检验后的授权数据推送mqtt等待消费授权生成##########################################
    public void pushChannelAuthTaskNotes(String taskNo) {
        //TODO TEST
        if(eventCenterServiceClientApi==null){
            consumeChannelAuthTaskNotesService.channelAuthConsumerHandler(taskNo);
            return;
        }
        //需要调用mqtt 发送授权任务notes的数据  通过事件中心发送mqtt authorize_center_channel_4_channel_auth_push_topic
        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.channel_auth.name());
        EventCenterPush eventCenterPush=new EventCenterPush();
        JSONObject msgBody=new JSONObject();
        msgBody.put("taskNo",taskNo);
        eventCenterPush.setMsgBody(msgBody.toJSONString());
        eventCenterPush.setRemark("该任务号：["+taskNo+"]发送授权任务notes授权处理，等待消费生成授权");
        eventCenterPush.setTopic(topic);
        eventCenterServiceClientApi.push(eventCenterPush);
    }

    @Override
    public void delPersonnelAuthorizeCache(String taskNo) {
        redisService.remove(taskNo+"-obj");
        redisService.remove(taskNo+"-authObj");
    }

    @Override
    public JSONObject personAuthorize(ChannelAuthorizeEntity channelAuthorizeEntity) {
        List<ChannelAuthLockVo> channelAuthLockVoList = channelAuthorizeEntity.getChannelAuthLockVoList();
        List<ChannelAuthUserVo> channelAuthUserVoList = channelAuthorizeEntity.getChannelAuthUserVoList();
        BaseAuthorizeVo baseAuthorizeVo= channelAuthorizeEntity.getBaseAuthorizeVo();
        if(EmptyUtil.isNotEmpty(channelAuthUserVoList)){
            for (int i = 0; i <channelAuthUserVoList.size() ; i++) {
                ChannelAuthUserVo channelAuthUserVo=channelAuthUserVoList.get(i);
                //TODO 根据人员信息获取相关介质
                ChannelAuthChannelInfoVo channelAuthChannelInfoVo =new ChannelAuthChannelInfoVo();


                List<ChannelAuthChannelInfoVo> channelAuthChannelInfoVoList =new ArrayList<>();
                channelAuthChannelInfoVoList.add(channelAuthChannelInfoVo);
                channelAuthUserVo.setChannelAuthChannelInfoVoList(channelAuthChannelInfoVoList);
                channelAuthUserVoList.set(i,channelAuthUserVo);
            }
        }
        channelAuthorizeEntity.setChannelAuthUserVoList(channelAuthUserVoList);
        //添加人员与锁的关系人员授权关系表
        for (ChannelAuthLockVo channelAuthLockVo:channelAuthLockVoList) {
            for (ChannelAuthUserVo channelAuthUserVo:channelAuthUserVoList) {
                addPersonAuthorize(baseAuthorizeVo,channelAuthUserVo,channelAuthLockVo);
            }
        }
        return channelAuthorize(channelAuthorizeEntity);
    }
    private Long addPersonAuthorize(BaseAuthorizeVo baseAuthorizeVo,ChannelAuthUserVo channelAuthUserVo,ChannelAuthLockVo channelAuthLockVo){
        AuthorizeCenterPersonAuthorize authorizeCenterPersonAuthorize=new AuthorizeCenterPersonAuthorize();
        authorizeCenterPersonAuthorize.setOrgId(baseAuthorizeVo.getOrgId());
        authorizeCenterPersonAuthorize.setLockId(channelAuthLockVo.getLockId());
        authorizeCenterPersonAuthorize.setUserAccount(channelAuthUserVo.getUserAccount());
        authorizeCenterPersonAuthorize.setUserName(channelAuthUserVo.getUserName());
        authorizeCenterPersonAuthorize.setUserIdentity(channelAuthUserVo.getUserIdentity());
        authorizeCenterPersonAuthorize.setStartTime(baseAuthorizeVo.getStartTime());
        authorizeCenterPersonAuthorize.setEndTime(baseAuthorizeVo.getEndTime());
        authorizeCenterPersonAuthorize.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
        authorizeCenterPersonAuthorize.setStatus(1);
        AuthorizeCenterPersonAuthorize queryModel=authorizeCenterPersonAuthorizeMapper.selectPersonAuthorizeListByDeviceUser(authorizeCenterPersonAuthorize);
        if(EmptyUtil.isNotEmpty(queryModel)){
            authorizeCenterPersonAuthorize.setId(queryModel.getId());
            authorizeCenterPersonAuthorize.setUpdateTime(new Date());
            authorizeCenterPersonAuthorize.setUpdateBy(baseAuthorizeVo.getCreateUserAccount());
            authorizeCenterPersonAuthorizeMapper.updateById(authorizeCenterPersonAuthorize);
        }else {
            authorizeCenterPersonAuthorize.setCreateTime(new Date());
            authorizeCenterPersonAuthorize.setCreateBy(baseAuthorizeVo.getCreateUserAccount());
            authorizeCenterPersonAuthorizeMapper.insert(authorizeCenterPersonAuthorize);
        }
        return authorizeCenterPersonAuthorize.getId();
    }
    /**
     *  介质授权
     * @return
     */
    @Override
    public JSONObject channelAuthorize(ChannelAuthorizeEntity channelAuthorizeEntity) {
        JSONObject result = new JSONObject();
        try {
            BaseAuthorizeVo baseAuthorizeVo= channelAuthorizeEntity.getBaseAuthorizeVo();
            String authResource=EmptyUtil.isNotEmpty(baseAuthorizeVo.getAuthResource())?baseAuthorizeVo.getAuthResource():AuthConstants.channelAuthResource;
            Date startTime=baseAuthorizeVo.getStartTime();
            Date endTime=baseAuthorizeVo.getEndTime();
            ChannelAuthGroupVo channelAuthGroupVo = channelAuthorizeEntity.getChannelAuthGroupVo();
            List<ChannelAuthLockVo> channelAuthLockVoList = channelAuthorizeEntity.getChannelAuthLockVoList();
            List<ChannelAuthUserVo> channelAuthUserVoList = channelAuthorizeEntity.getChannelAuthUserVoList();
            ChannelAuthTaskVo channelAuthTaskVo = channelAuthorizeEntity.getChannelAuthTaskVo();
            if(EmptyUtil.isEmpty(channelAuthTaskVo.getTaskNo())){
                result.put("status", ErrorCodeEnum.PARAM_NEED.code());
                result.put("message", "任务号不能为空");
                authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
                return result;
            }else if(startTime==null){
                result.put("status", ErrorCodeEnum.PARAM_NEED.code());
                result.put("message", "开始时间不能为空");
                authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
                return result;
            }else if(endTime==null){
                result.put("status", ErrorCodeEnum.PARAM_NEED.code());
                result.put("message", "结束时间不能为空");
                authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
                return result;
            }else if(EmptyUtil.isEmpty(baseAuthorizeVo.getCreateUserAccount())){
                result.put("status", ErrorCodeEnum.PARAM_NEED.code());
                result.put("message", "操作人不能为空");
                authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
                return result;
            } else if(endTime.getTime()<=startTime.getTime()){
                result.put("status", ErrorCodeEnum.PARAM_NEED.code());
                result.put("message", "开始时间不能超过结束时间");
                authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
                return result;
            }else if(endTime.getTime()>AuthConstants.maxEndTime) {
                result.put("status", ErrorCodeEnum.PARAM_NEED.code());
                result.put("message", "结束时间不能大于2038-01-01");
                authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
                return result;
            }else if(EmptyUtil.isEmpty(channelAuthLockVoList)){
                result.put("status", ErrorCodeEnum.PARAM_NEED.code());
                result.put("message", authResource+"请携带设备信息，在做授权！");
                authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
                return result;
            }else if(EmptyUtil.isEmpty(channelAuthUserVoList)){
                result.put("status", ErrorCodeEnum.PARAM_NEED.code());
                result.put("message", authResource+"请携带人员信息，在做授权！");
                authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
                return result;
            }
            //判断当前授权是否为组织授权
            if(EmptyUtil.isNotEmpty(channelAuthGroupVo)){
                if(channelAuthGroupVo.getDeviceGroupId()!=null|| channelAuthGroupVo.getUserGroupId()!=null) {
                    baseAuthorizeVo.setIsGroup(1);
                }
            }
            List<ChannelAuthLockVo> checkSuccessLockVoList=new ArrayList<>();
            List<ChannelAuthUserVo> checkSuccessUserVoList=new ArrayList<>();
            Integer taskCount= channelAuthLockVoList.size()* channelAuthUserVoList.size();
            Integer valiSuccessCount=1;
            StringBuffer lockIds=new StringBuffer();
            StringBuffer userAccounts=new StringBuffer();
            //----------------------------authResource+"数据检验区-------------------------------------------
            for (ChannelAuthLockVo channelAuthLockVo : channelAuthLockVoList) {
                if(EmptyUtil.isEmpty(channelAuthLockVo.getLockId())){
                    //记录到task 日志中
                    authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, channelAuthLockVo, null,
                             null,authResource+"存在设备lockId为空,视为无效数据丢弃此授权");
                    continue;
                }
                //检验lock是否真实存在
                Lock lock=lockMapper.selectByLockId(channelAuthLockVo.getLockId());
                if(EmptyUtil.isEmpty(lock)){
                    //记录到task 日志中
                    authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, channelAuthLockVo,
                            null,null,authResource+"系统检测查询lockId"+ channelAuthLockVo.getLockId()+
                                    "设备不存在,视为无效数据丢弃此授权");
                    continue;
                }
                channelAuthLockVo.setLockMac(lock.getMac());
                channelAuthLockVo.setLockType(lock.getLocktype());
                channelAuthLockVo.setLockName(EmptyUtil.isNotEmpty(lock.getName())?lock.getName():lock.getMac());
                lockIds.append(channelAuthLockVo.getLockId()).append(",");
                checkSuccessLockVoList.add(channelAuthLockVo);
            }

            for (ChannelAuthUserVo channelAuthUserVo : channelAuthUserVoList) {
                if(EmptyUtil.isEmpty(channelAuthUserVo.getUserAccount())){
                    //记录到task 日志中
                    authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, null, channelAuthUserVo,
                            null,authResource+"存在被授权账号为空,视为无效数据丢弃此授权");
                    continue;
                }
                if(EmptyUtil.isEmpty(channelAuthUserVo.getChannelAuthChannelInfoVoList())){
                    //记录到task 日志中
                    authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, null, channelAuthUserVo,
                            null,"用户:" + channelAuthUserVo.getUserName() + "(" + channelAuthUserVo.getUserAccount() + ") 没有绑定任何介质");
                    continue;
                }
                if(EmptyUtil.isEmpty(channelAuthUserVo.getUserName())) {
                    channelAuthUserVo.setUserName(channelAuthUserVo.getUserAccount());
                }
                List<ChannelAuthChannelInfoVo> channelAuthChannelInfoVoList = channelAuthUserVo.getChannelAuthChannelInfoVoList();
                taskCount=taskCount* channelAuthChannelInfoVoList.size();

                List<ChannelAuthChannelInfoVo> checkSuccessChannelVoList=new ArrayList<>();

                for (ChannelAuthChannelInfoVo channelAuthChannelInfoVo : channelAuthChannelInfoVoList) {
                    if(EmptyUtil.isEmpty(channelAuthChannelInfoVo.getAuthChannelValue())){
                        //记录到task 日志中
                        authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, null, channelAuthUserVo,
                                channelAuthChannelInfoVo,authResource+"("+ channelAuthUserVo.getUserAccount()+")介质值为空,视为无效数据丢弃此授权");
                        continue;
                    }
                    if(EmptyUtil.isEmpty(channelAuthChannelInfoVo.getAuthChannelType())){
                        //记录到task 日志中
                        authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, null, channelAuthUserVo,
                                channelAuthChannelInfoVo,authResource+"("+ channelAuthUserVo.getUserAccount()+")介质类型为空,视为无效数据丢弃此授权");
                        continue;
                    }
                    if(EmptyUtil.isEmpty(channelAuthChannelInfoVo.getAuthChannelModuleType())){
                        //记录到task 日志中
                        authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, null, channelAuthUserVo,
                                channelAuthChannelInfoVo,authResource+"("+ channelAuthUserVo.getUserAccount()+")介质模块类型为空,视为无效数据丢弃此授权");
                        continue;
                    }
                    //TODO 判断钥匙的等级与锁的等级检验

                    if(EmptyUtil.isEmpty(channelAuthChannelInfoVo.getAuthChannelAlias())) {
                        channelAuthChannelInfoVo.setAuthChannelAlias(channelAuthUserVo.getUserName());
                    }
                    if(EmptyUtil.isEmpty(channelAuthChannelInfoVo.getAuthChannelName())) {
                        channelAuthChannelInfoVo.setAuthChannelName(channelAuthUserVo.getUserName());
                    }
                    checkSuccessChannelVoList.add(channelAuthChannelInfoVo);
                }

                channelAuthUserVo.setChannelAuthChannelInfoVoList(checkSuccessChannelVoList);
                valiSuccessCount=valiSuccessCount*checkSuccessChannelVoList.size();
                userAccounts.append(channelAuthUserVo.getUserAccount()).append(",");
                checkSuccessUserVoList.add(channelAuthUserVo);
            }
            valiSuccessCount=checkSuccessUserVoList.size()*checkSuccessLockVoList.size()*valiSuccessCount;
            Integer valiFailureCount=taskCount-valiSuccessCount;
            channelAuthorizeEntity.setChannelAuthLockVoList(checkSuccessLockVoList);
            channelAuthorizeEntity.setChannelAuthUserVoList(checkSuccessUserVoList);
            try {
                AuthorizeCenterTask queryModel = authorizeCenterTaskService.selectByTaskNo(channelAuthTaskVo.getTaskNo());
                if (EmptyUtil.isNotEmpty(queryModel)) {
                    queryModel.setUserTarget(userAccounts.toString());
                    queryModel.setUpdateTime(new Date());
                    queryModel.setUpdateBy(baseAuthorizeVo.getCreateUserAccount());
                    queryModel.setTaskStatus(0);
                    queryModel.setLockId(lockIds.toString());
                    queryModel.setValiFailureCount(valiFailureCount);
                    queryModel.setTaskCount(taskCount);
                    queryModel.setAuthStartTime(baseAuthorizeVo.getStartTime());
                    queryModel.setAuthEndTime(baseAuthorizeVo.getEndTime());
                    authorizeCenterTaskService.updateById(queryModel);
                } else {
                    queryModel=new AuthorizeCenterTask();
                    queryModel.setUserTarget(userAccounts.toString());
                    queryModel.setCreateTime(new Date());
                    queryModel.setLockId(lockIds.toString());
                    queryModel.setCreateBy(baseAuthorizeVo.getCreateUserAccount());
                    queryModel.setTaskStatus(0);//0=提交 1=完成  2=执行中 3=失败 4=异常
                    queryModel.setTaskType(baseAuthorizeVo.getAuthResource());
                    queryModel.setValiFailureCount(valiFailureCount);
                    queryModel.setTaskCount(taskCount);
                    queryModel.setAuthStartTime(baseAuthorizeVo.getStartTime());
                    queryModel.setAuthEndTime(baseAuthorizeVo.getEndTime());
                    queryModel.setOperator(baseAuthorizeVo.getCreateUserAccount());
                    queryModel.setTaskNo(channelAuthTaskVo.getTaskNo());
                    queryModel.setOrgId(baseAuthorizeVo.getOrgId());
                    authorizeCenterTaskService.save(queryModel);
                }
            }catch (Exception e){log.error(authResource+"添加任务失败 error",e);}


            //授权记录保存到任务执行记录中
            for (ChannelAuthLockVo channelAuthLockVo :checkSuccessLockVoList) {
                for (ChannelAuthUserVo channelAuthUserVo :checkSuccessUserVoList) {
                    List<ChannelAuthChannelInfoVo> channelAuthChannelInfoVoList = channelAuthUserVo.getChannelAuthChannelInfoVoList();
                    channelAuthorizeTaskNotes(baseAuthorizeVo, channelAuthLockVo, channelAuthUserVo, channelAuthChannelInfoVoList,
                            channelAuthorizeEntity.getChannelAuthTaskVo());
                }
            }
            Long allAuthListSize = authorizeCenterTaskNotesService.selectTaskNotesCountByTaskNo(channelAuthTaskVo.getTaskNo());
            if(allAuthListSize != null && allAuthListSize != 0) {
                if(allAuthListSize>taskCount){
                    authorizeCenterTaskService.updateSyncStatusAndTaskAuthCountByTaskNo(channelAuthTaskVo.getTaskNo(), 2, allAuthListSize);//更新任务状态 有效数据处理完成，进入授权生成过程中
                }
                pushChannelAuthTaskNotes(channelAuthTaskVo.getTaskNo());
            }else {
                delPersonnelAuthorizeCache(channelAuthTaskVo.getTaskNo());
                log.info("异步授权任务taskNo{}缓存删除成功", channelAuthTaskVo.getTaskNo());
                authorizeCenterTaskService.updateSyncStatusByTaskNo(channelAuthTaskVo.getTaskNo(), 1);
                log.info("异步授权任务taskNo{}状态【1】更新成功", channelAuthTaskVo.getTaskNo());
            }
            result.put("taskNo", channelAuthTaskVo.getTaskNo());
            result.put("status", ErrorCodeEnum.SUCCESS.code());
            result.put("message", "授权任务校验处理成功，进入授权数据处理阶段！");
            authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            authorizeCenterTaskResultService.saveErrorTaskResultLog(channelAuthorizeEntity,result);
        }
        return result;
    }

    /**
     * 授权执行方法
     */
    public JSONObject channelAuthorizeTaskNotes(BaseAuthorizeVo baseAuthorizeVo, ChannelAuthLockVo channelAuthLockVo,
                                                ChannelAuthUserVo channelAuthUserVo, List<ChannelAuthChannelInfoVo> channelAuthChannelInfoVoList,
                                                ChannelAuthTaskVo channelAuthTaskVo) {
        JSONObject result = new JSONObject();
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "授权成功");
        try {
            //查询人员授权关系表id
            Long personAuthId=null;
            if(AuthConstants.personAuthResource.equals(baseAuthorizeVo.getAuthResource())) {
                personAuthId = addPersonAuthorize(baseAuthorizeVo, channelAuthUserVo, channelAuthLockVo);
            }
            log.info("[processChannelAuthorize-auth]lock:{},userAccount:{},getChannelType:{}进入"+baseAuthorizeVo.getAuthResource()
                    , channelAuthLockVo.getLockName(), channelAuthUserVo.getUserAccount(),JSONObject.toJSONString(channelAuthChannelInfoVoList));
            for (ChannelAuthChannelInfoVo channelAuthChannelInfoVo : channelAuthChannelInfoVoList) {
                AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord=new AuthorizeCenterAuthorizeRecord();
                authorizeCenterAuthorizeRecord.setLockId(channelAuthLockVo.getLockId());
                authorizeCenterAuthorizeRecord.setUserAccount(channelAuthUserVo.getUserAccount());
                authorizeCenterAuthorizeRecord.setUserName(channelAuthUserVo.getUserName());
                authorizeCenterAuthorizeRecord.setAuthChannelModuleType(channelAuthChannelInfoVo.getAuthChannelModuleType());
                authorizeCenterAuthorizeRecord.setAuthChannelType(channelAuthChannelInfoVo.getAuthChannelType());
                authorizeCenterAuthorizeRecord.setAuthChannelAlias(channelAuthChannelInfoVo.getAuthChannelAlias());
                authorizeCenterAuthorizeRecord.setStartTime(baseAuthorizeVo.getStartTime());
                authorizeCenterAuthorizeRecord.setEndTime(baseAuthorizeVo.getEndTime());
                authorizeCenterAuthorizeRecord.setAuthDesc(baseAuthorizeVo.getAuthDesc());
                authorizeCenterAuthorizeRecord.setAuthResource(baseAuthorizeVo.getAuthResource());
                authorizeCenterAuthorizeRecord.setCreateBy(baseAuthorizeVo.getCreateUserAccount());
                authorizeCenterAuthorizeRecord.setIsGroup(baseAuthorizeVo.getIsGroup());
                authorizeCenterAuthorizeRecord.setAuthChannelName(ChannelTypeEnums.getChannelTypeName(channelAuthChannelInfoVo.getAuthChannelType()));
                authorizeCenterAuthorizeRecord.setAuthExtend(baseAuthorizeVo.getAuthExtend());
                authorizeCenterAuthorizeRecord.setAuthChannelValue(channelAuthChannelInfoVo.getAuthChannelValue());
                authorizeCenterAuthorizeRecord.setLockName(channelAuthLockVo.getLockName());
                authorizeCenterAuthorizeRecord.setLockMac(channelAuthLockVo.getLockMac());
                authorizeCenterAuthorizeRecord.setChannelName(channelAuthChannelInfoVo.getAuthChannelName());
                authorizeCenterAuthorizeRecord.setTemplateFeatureId(channelAuthChannelInfoVo.getTemplateFeatureId());
                authorizeCenterAuthorizeRecord.setLockType(channelAuthLockVo.getLockType());
                authorizeCenterAuthorizeRecord.setGroupAuthId(baseAuthorizeVo.getGroupAuthId());
                authorizeCenterAuthorizeRecord.setPersonAuthId(personAuthId);
                AuthorizeCenterTaskNotes authorizeCenterTaskNotes=new AuthorizeCenterTaskNotes();
                authorizeCenterTaskNotes.setAuthContent(JSONObject.toJSONString(authorizeCenterAuthorizeRecord));
                authorizeCenterTaskNotes.setTaskNo(channelAuthTaskVo.getTaskNo());
                authorizeCenterTaskNotes.setCreateBy(baseAuthorizeVo.getCreateUserAccount());
                authorizeCenterTaskNotesService.addTaskNotes(authorizeCenterTaskNotes);
            }
            log.info("[userChannelAuthorize-auth]lock:{},userAccount:{},授权条数:{}最后返回结果：{}", channelAuthLockVo.getLockName(), channelAuthUserVo.getUserAccount()
                   , channelAuthChannelInfoVoList.size() ,result.toJSONString());
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            authorizeCenterTaskResultService.saveErrorTaskResultLog(baseAuthorizeVo, channelAuthTaskVo, null, channelAuthUserVo,
                    null,result);
        }
        return result;
    }

    //######################################授权任务消费执行###############################################################
    /**
     * 授权处理
     *  授权任务处理完后，执行此方法
     * @param authorizeCenterAuthorizeRecord
     * @return
     */
    @Override
    @Transactional
    public JSONObject processChannelAuthorize(AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord) throws InterruptedException {
        SpinLock.lock();
        JSONObject result = new JSONObject();
        String authResource=EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord.getAuthResource())?authorizeCenterAuthorizeRecord.getAuthResource():AuthConstants.channelAuthResource;
        try{
            Long authId;
            //授权处理
            authorizeCenterAuthorizeRecord.setCreateTime(new Date());
            authorizeCenterAuthorizeRecord.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
            authorizeCenterAuthorizeRecord.setStatus(Constants.STATUS_CODE);
            authorizeCenterAuthorizeRecord.setFlag(Constants.FAIL_CODE);
            authorizeCenterAuthorizeRecord.setFreezeStatus(Constants.FREEZE_STATUS_DONE);
            String authChannelModuleType=authorizeCenterAuthorizeRecord.getAuthChannelModuleType();
            String uid=UUIDUtil.getUid(authorizeCenterAuthorizeRecord.getUserAccount());
            String lockId=authorizeCenterAuthorizeRecord.getLockId();
            String authChannelValue=authorizeCenterAuthorizeRecord.getAuthChannelValue().trim();

            if(ChannelTypeEnums.MODULETYPE_02.equals(authChannelModuleType)){//密码
                if(authChannelValue.length()!=16){
                    log.info("卡uid:{}长度异常！！ 真实长度{} 标准长度16位",authChannelValue, authChannelValue.length());
                    result.put("message", "nfcId:"+authChannelValue+"长度不等于16位 ,");
                    result.put("status", ErrorCodeEnum.FAIL.code());
                    return result;
                }
            } else if (ChannelTypeEnums.MODULETYPE_04.equals(authChannelModuleType)) {//指纹
                //TODO 最后一位标定手指
                uid = uid.substring(0, 15) + Integer.toHexString(1);
            } else if(EmptyUtil.isEmpty(ChannelTypeEnums.getModuleTypeMapName(authChannelModuleType))){
                result.put("message", AuthConstants.channelAuthResource+"下发还不能支持此类型:" + authChannelModuleType);
                result.put("status", ErrorCodeEnum.FAIL.code());
                return result;
            }else if(EmptyUtil.isEmpty(ChannelTypeEnums.getChannelTypeName(authorizeCenterAuthorizeRecord.getAuthChannelType()))){
                result.put("message", AuthConstants.channelAuthResource+"下发还不能支持此类型:" + authorizeCenterAuthorizeRecord.getAuthChannelType());
                result.put("status", ErrorCodeEnum.FAIL.code());
                return result;
            }
            authorizeCenterAuthorizeRecord.setUid(uid);
            authorizeCenterAuthorizeRecord.setAuthDesc("授权生成成功");
            saveAuth(authorizeCenterAuthorizeRecord);

            //更改t_authorize_center_authorize_lock_change 数据
            authId=authorizeCenterAuthorizeRecord.getId();
            AuthorizeCenterAuthorizeLockChange lockChange=new AuthorizeCenterAuthorizeLockChange();
            AuthorizeCenterAuthorizeLockChange authorizeCenterAuthorizeLockChange=authorizeCenterAuthorizeLockChangeMapper.selectChangeByLockId(lockId);
            lockChange.setLockId(lockId);
            int change=1;
            String featureType=null;
            if(ChannelTypeEnums.MODULETYPE_01.equals(authChannelModuleType)){//密码
                if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeLockChange)){
                    authorizeCenterAuthorizeLockChangeMapper.updatePwdChangeByLockId(lockId);
                }else{
                    lockChange.setPwdHasChangeVersion(change);
                    lockChange.setCreateTime(new Date());
                    lockChange.setCreateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                    lockChange.setUpdateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                    authorizeCenterAuthorizeLockChangeMapper.insert(lockChange);
                }
                featureType="pwd";
            } else if (ChannelTypeEnums.MODULETYPE_02.equals(authChannelModuleType)) {//刷卡
                if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeLockChange)){
                    authorizeCenterAuthorizeLockChangeMapper.updateCardChangeByLockId(lockId);
                }else{
                    lockChange.setCardHasChangeVersion(change);
                    lockChange.setCreateTime(new Date());
                    lockChange.setCreateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                    lockChange.setUpdateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                    authorizeCenterAuthorizeLockChangeMapper.insert(lockChange);
                }
                featureType="card";
            } else if (ChannelTypeEnums.MODULETYPE_04.equals(authChannelModuleType)) {//指纹
                if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeLockChange)){
                    authorizeCenterAuthorizeLockChangeMapper.updatePwdChangeByLockId(lockId);
                }else{
                    lockChange.setFaceHasChangeVersion(change);
                    lockChange.setCreateTime(new Date());
                    lockChange.setCreateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                    lockChange.setUpdateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                    authorizeCenterAuthorizeLockChangeMapper.insert(lockChange);
                }
                featureType="finger";
            }else if (ChannelTypeEnums.MODULETYPE_06.equals(authChannelModuleType)) {//人脸
                if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeLockChange)){
                    authorizeCenterAuthorizeLockChangeMapper.updatePwdChangeByLockId(lockId);
                }else{
                    lockChange.setFingerHasChangeVersion(change);
                    lockChange.setCreateTime(new Date());
                    lockChange.setCreateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                    lockChange.setUpdateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                    authorizeCenterAuthorizeLockChangeMapper.insert(lockChange);
                }
                featureType="face";
            }
            if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord.getTemplateFeatureId())&&
                    ("face".equals(featureType)||"finger".equals(featureType))) {
                AuthorizeCenterAuthorizeTemplateData authorizeCenterAuthorizeTemplateData = new AuthorizeCenterAuthorizeTemplateData();
                authorizeCenterAuthorizeTemplateData.setAuthId(authId);
                authorizeCenterAuthorizeTemplateData.setTemplateFeatureId(authorizeCenterAuthorizeRecord.getTemplateFeatureId());
                authorizeCenterAuthorizeTemplateData.setCreateTime(new Date());
                authorizeCenterAuthorizeTemplateData.setFeatureType(featureType);
                authorizeCenterAuthorizeTemplateDataMapper.insert(authorizeCenterAuthorizeTemplateData);
            }
            //添加组授权id与授权id关系数据
            if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeRecord.getIsGroup())&&
                    authorizeCenterAuthorizeRecord.getIsGroup()==1) {
                AuthorizeCenterGroupAuthorizeRelation authorizeCenterGroupAuthorizeRelation=new AuthorizeCenterGroupAuthorizeRelation();
                authorizeCenterGroupAuthorizeRelation.setAuthId(authId);
                authorizeCenterGroupAuthorizeRelation.setGroupAuthId(authorizeCenterAuthorizeRecord.getGroupAuthId());
                authorizeCenterGroupAuthorizeRelation.setCreateTime(new Date());
                authorizeCenterGroupAuthorizeRelation.setCreateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                authorizeCenterGroupAuthorizeRelation.setUpdateBy(authorizeCenterAuthorizeRecord.getCreateBy());
                authorizeCenterGroupAuthorizeRelationMapper.insert(authorizeCenterGroupAuthorizeRelation);
            }
            //t_authorize_center_authorize_sync
            syncAuthorize(authorizeCenterAuthorizeRecord,"添加",false);
            List<String> authIds=new ArrayList<>();
            authIds.add(authId+"");
            result.put("authId",String.join(",", authIds));
            result.put("status", ErrorCodeEnum.SUCCESS.code());
            result.put("message", "授权生成流程,系统处理成功！！！");
        }catch (Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", "授权失败"+e.getMessage());
        }finally {
            SpinLock.unLock();
            insertAuthLog(authorizeCenterAuthorizeRecord.getLockId(),authResource,"添加","添加授权数据",authorizeCenterAuthorizeRecord.getCreateBy()
                    ,JSONObject.toJSONString(authorizeCenterAuthorizeRecord),result.toJSONString());
        }
        return result;
    }

    @Override
    public void processTaskByNoFinish() {
        List<AuthorizeCenterTask> taskNoFinishList= authorizeCenterTaskService.selectNoFinishTaskByTime(EventCenterConstant.retryCount, AuthConstants.pageSize);
        if(EmptyUtil.isNotEmpty(taskNoFinishList)) {
            for (AuthorizeCenterTask authorizeCenterTask:taskNoFinishList) {
                String taskNo = authorizeCenterTask.getTaskNo();
                long total = authorizeCenterTaskNotesService.selectTaskNotesCountByTaskNo(taskNo);
                if(total==0) {
                    delPersonnelAuthorizeCache(taskNo);
                    authorizeCenterTaskService.updateSyncStatusByTaskNo(taskNo, 1, "处理成功");
                    authorizeCenterTask=authorizeCenterTaskResultService.getTaskResultCountInfoLog(authorizeCenterTask);
                    authorizeCenterTaskService.updateAuthCountByTaskNo(authorizeCenterTask);
                }
            }
        }
        pushChannelAuthTaskNotes("retryTaskFailAction");
    }
    public void insertAuthLog(String lockId,String module,String operateType,String operateName,String operation,String reqParam,String respResult){
        AuthorizeCenterAuthorizeLog authorizeCenterAuthorizeLog=new AuthorizeCenterAuthorizeLog();
        authorizeCenterAuthorizeLog.setLockId(lockId);
        authorizeCenterAuthorizeLog.setModule(module);
        authorizeCenterAuthorizeLog.setOperateType(operateType);
        authorizeCenterAuthorizeLog.setOperateName(operateName);
        authorizeCenterAuthorizeLog.setOperation(operation);
        authorizeCenterAuthorizeLog.setReqParam(reqParam);
        authorizeCenterAuthorizeLog.setRespResult(respResult);
        authorizeCenterAuthorizeLog.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
        authorizeCenterAuthorizeLog.setCreateTime(new Date());
        authorizeCenterAuthorizeLog.setCreateBy(null);
        authorizeCenterAuthorizeLogMapper.insert(authorizeCenterAuthorizeLog);
    }

    /**
     * //组授权处理
     * @return
     */
    @Override
    public String batchProcessGroupAuth(String taskNo, ChannelAuthorizeEntity channelAuthorizeEntity) {
        String syncTaskNo=taskNo;
        try{
            List<ChannelAuthLockVo> channelAuthLockVoList = channelAuthorizeEntity.getChannelAuthLockVoList();
            List<ChannelAuthUserVo> channelAuthUserVoList = channelAuthorizeEntity.getChannelAuthUserVoList();
            if (EmptyUtil.isNotEmpty(channelAuthLockVoList)&&EmptyUtil.isNotEmpty(channelAuthUserVoList)) {
                List<List<ChannelAuthLockVo>> batchLockVoList = Lists.partition(channelAuthLockVoList, AuthConstants.batchLockSize);
                List<List<ChannelAuthUserVo>> batchAuthUserVoList = Lists.partition(channelAuthUserVoList, AuthConstants.batchUserSize);
                for (List<ChannelAuthUserVo> authUserVos : batchAuthUserVoList) {
                    for (List<ChannelAuthLockVo> lockVos : batchLockVoList) {
                        ChannelAuthorizeEntity authorizeEntity=new ChannelAuthorizeEntity();
                        authorizeEntity.setBaseAuthorizeVo(channelAuthorizeEntity.getBaseAuthorizeVo());
                        authorizeEntity.setChannelAuthGroupVo(channelAuthorizeEntity.getChannelAuthGroupVo());
                        authorizeEntity.setChannelAuthUserVoList(authUserVos);
                        authorizeEntity.setChannelAuthLockVoList(lockVos);
                        ChannelAuthTaskVo channelAuthTaskVo =new ChannelAuthTaskVo();
                        channelAuthTaskVo.setTaskNo(taskNo);
                        channelAuthTaskVo.setTaskType("人员组与设备组授权");
                        channelAuthorizeEntity.setChannelAuthTaskVo(channelAuthTaskVo);
                        addChannelAuthorizeTask(channelAuthorizeEntity);
                    }
                }
            }
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
        }finally {
        }
        return syncTaskNo;
    }


    private String generateEqualUuid(){
        String equalUuid= UUIDUtil.generateNo();
        int count=this.baseMapper.selectExistByEqualUuid(equalUuid);
        if(count!=0) {
            return generateEqualUuid();
        }
        return equalUuid;
    }


}

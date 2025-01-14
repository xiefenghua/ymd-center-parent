package com.ymd.cloud.authorizeCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.authorizeCenter.enums.AuthConstants;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.enums.LockTypeDeviceAndCommModeEnum;
import com.ymd.cloud.authorizeCenter.job.authSync.AuthChangeSyncTask;
import com.ymd.cloud.authorizeCenter.job.third.ThirdSupportAuthChangeSyncTask;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeLogMapper;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeRecordMapper;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeSyncMapper;
import com.ymd.cloud.authorizeCenter.mapper.lock.EDeviceBaseInfoMapper;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockGateWayMapper;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockGateWayRelationMapper;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeLog;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeSync;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.entity.AuthListParamQuery;
import com.ymd.cloud.authorizeCenter.model.entity.ChannelAuthLockVo;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.model.lock.LockGateWay;
import com.ymd.cloud.authorizeCenter.model.lock.LockGateWayRelation;
import com.ymd.cloud.authorizeCenter.model.lock.ResourceConfig;
import com.ymd.cloud.authorizeCenter.service.AuthorizeSyncService;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.enumsSupport.EventCenterConstant;
import com.ymd.cloud.common.utils.ClzUtil;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class AuthorizeSyncServiceImpl extends ServiceImpl<AuthorizeCenterAuthorizeSyncMapper, AuthorizeCenterAuthorizeSync> implements AuthorizeSyncService {
    @Value("${ymd.iot.productId}")
    private String productId;
    @Resource
    private LockGateWayMapper lockGateWayMapper;
    @Resource
    LockGateWayRelationMapper lockGateWayRelationMapper;
    @Resource
    EDeviceBaseInfoMapper eDeviceBaseInfoMapper;
    @Resource
    LockMapper lockMapper;
    @Resource
    AuthorizeCenterAuthorizeRecordMapper authorizeCenterAuthorizeRecordMapper;
    @Resource
    AuthorizeCenterAuthorizeLogMapper authorizeCenterAuthorizeLogMapper;
    @Autowired
    AuthChangeSyncTask authChangeSyncTask;
    @Autowired
    ThirdSupportAuthChangeSyncTask thirdSupportAuthChangeSyncTask;
    @Autowired
    RedisService redisService;
    @Override
    public void retryPushSyncAllFailAuth() {
        //获取所有未同步的锁
        AuthListParamQuery incrementAuthRecordQuery=new AuthListParamQuery();
        incrementAuthRecordQuery.setStartDate(new Date());
        incrementAuthRecordQuery.setEndDate(new Date(DateUtil.delayTime(AuthConstants.authTimeDiff)));
        List<AuthorizeCenterAuthorizeSync> authSyncList=authorizeCenterAuthorizeRecordMapper.selectAllNoSyncAuthList(incrementAuthRecordQuery);
        if (EmptyUtil.isNotEmpty(authSyncList)) {
            for (AuthorizeCenterAuthorizeSync authorizeSync:authSyncList) {
                processSyncTask(authorizeSync.getLockId());
            }
        }
    }
    //TODO authorize_center_channel_2_user_authorize_sync_topic消费
    @Override
    public void userAuthorizeSyncConsumerHandler(ChannelAuthLockVo channelAuthLockVo) {
        processSyncTask(channelAuthLockVo.getLockId());
    }
    private void processSyncTask(String lockId){
        String key=Constants.MQ_AUTHORIZE_SYNC_TOPIC_REDIS_KEY+lockId;
        try {
            boolean redisLock=redisService.lock(key,DateUtil.format(System.currentTimeMillis(),DateUtil.yyyy_MM_ddHH_mm_ss),Constants.redisTimeOutTime);
            if (!redisLock) {
                String time=redisService.get(key);
                log.info("==============同步中心key:"+key+"("+time+")同步任务已进行中,请"+Constants.redisTimeOutTime+"秒后再试");
                return;
            }
            if(redisLock) {
                Lock lock = lockMapper.selectByLockId(lockId);
                if (EmptyUtil.isNotEmpty(lock)) {
                    String mac = lock.getMac();
                    if (LockTypeDeviceAndCommModeEnum.DOOR_FACE.getLockType().equals(lock.getLocktype()) &&
                            LockTypeDeviceAndCommModeEnum.DOOR_FACE.getDeviceMode().equals(lock.getDeviceMode()) &&
                            LockTypeDeviceAndCommModeEnum.DOOR_FACE.getCommMode().equals(lock.getCommMode())) {
                        String vendor = lock.getVentor();
                        thirdSupportAuthChangeSyncTask.pushAuth(lockId, mac, vendor);
                    } else {
                        pushAuthorizeChangeSyncConsumer(lockId, mac);
                    }
                }
            }
        }catch(Exception e) {
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(), e);
        }finally {
//            redisService.unLock(key);
        }
    }

    private void pushAuthorizeChangeSyncConsumer(String lockId,String mac){
        LockGateWayRelation relation = lockGateWayRelationMapper.getGateRelationInfoByLockId(lockId);
        if(relation!=null){
            String gateId=relation.getGateid();
            LockGateWay lockGateWay=lockGateWayMapper.queryInfoByGateId(gateId);
            String gateMac = lockGateWay !=null?lockGateWay.getMac():"";
            int mqttType=eDeviceBaseInfoMapper.selectMqttTypeByGateMac(gateMac);
            ResourceConfig resourceConfig =new ResourceConfig();
            resourceConfig.setMqttType(mqttType);
            resourceConfig.setResTypeId(3);
            resourceConfig.setYxbz("1");
            resourceConfig= eDeviceBaseInfoMapper.selectResourceConfig(resourceConfig);
            //判断是否开启mqtt同步标识 和开启全量下发标识 0=不开启 1=开启
            if(resourceConfig!=null&&resourceConfig.getMqttSync()==1) {
                // 全量下发
                if (resourceConfig.getPushAll()==1) {
                    pushAuthorizeAll(lockId, gateMac,mac);
                }
                pushAuthorizeIncrement(lockId, gateMac,mac);
            }else{
                log.info("【授权同步消息推送-gateMac：{} mqttType：{} 未找到s_resource_config表中是否开启mqtt同步标识！！",
                        gateMac, mqttType);
            }
        }
    }

    private void pushAuthorizeAll(String lockId,String gateMac,String mac){
        List<AuthorizeCenterAuthorizeRecord> authSyncList=authChangeSyncTask.pushAuthorizeAll(lockId,mac,gateMac);
        //保存到同步表中，现有阶段不同步的数据 定时在同步到锁
        if(EmptyUtil.isNotEmpty(authSyncList)){
            for (AuthorizeCenterAuthorizeRecord syncModel:authSyncList) {
                syncModel.setRemark("全量");
                syncModel.setLockMac(mac);
                setSyncModel(syncModel);
            }
        }
    }
    private void pushAuthorizeIncrement(String lockId,String gateMac,String mac){
        List<AuthorizeCenterAuthorizeRecord> authIncrementSyncList= authChangeSyncTask.pushAuthorizeIncrement(lockId,mac,gateMac);
        //保存到同步表中，现有阶段不同步的数据 定时在同步到锁
        if(EmptyUtil.isNotEmpty(authIncrementSyncList)){
            for (AuthorizeCenterAuthorizeRecord incrementSyncModel:authIncrementSyncList) {
                incrementSyncModel.setRemark("增量");
                incrementSyncModel.setLockMac(mac);
                setSyncModel(incrementSyncModel);
            }
        }
    }
    private void setSyncModel(AuthorizeCenterAuthorizeRecord syncModel){
        String cmd=syncModel.getStatus()==1?"删除":"添加";
        String syncName=syncModel.getUserAccount()+"账号对"+syncModel.getLockMac()+"锁进行"+cmd+ChannelTypeEnums.getModuleTypeMapName(syncModel.getAuthChannelModuleType())
                +"授权"+syncModel.getRemark()+"同步";
        saveAuthorizeSyncByEqualUuid(syncModel.getId(),syncModel.getAuthEqualUuid(),syncName,Constants.NOT_SYNC,Constants.NOT_UPLOAD_STATUS,Constants.PUSH_STATUS
                ,cmd,syncModel.getCreateBy(),syncModel.getUpdateBy());
        AuthorizeCenterAuthorizeLog authorizeCenterAuthorizeLog=new AuthorizeCenterAuthorizeLog();
        authorizeCenterAuthorizeLog.setModule("授权同步");
        authorizeCenterAuthorizeLog.setLockId(syncModel.getLockId());
        authorizeCenterAuthorizeLog.setOperateType("系统更新同步状态");
        authorizeCenterAuthorizeLog.setOperateName(syncName);
        authorizeCenterAuthorizeLog.setOperation(syncModel.getCreateBy());
        authorizeCenterAuthorizeLog.setReqParam(JSONObject.toJSONString(syncModel));
        authorizeCenterAuthorizeLog.setRespResult("更新状态成功");
        authorizeCenterAuthorizeLog.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
        authorizeCenterAuthorizeLog.setCreateTime(new Date());
        authorizeCenterAuthorizeLog.setCreateBy(syncModel.getCreateBy());
        authorizeCenterAuthorizeLogMapper.insert(authorizeCenterAuthorizeLog);
    }
    @Override
    public void saveAuthorizeSyncByEqualUuid(Long authId,String authEqualUuid,String syncName,Integer sync,Integer uploadStatus,Integer pushStatus,String syncOptType,String createBy,String updateBy) {
        //添加同步表中
        List<AuthorizeCenterAuthorizeSync> authorizeCenterAuthorizeSyncList=this.baseMapper.selectSyncByAuthEqualUuid(authEqualUuid);
        if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeSyncList)){
            for (AuthorizeCenterAuthorizeSync authorizeCenterAuthorizeSync:authorizeCenterAuthorizeSyncList) {
                authorizeCenterAuthorizeSync.setSync(sync);
                authorizeCenterAuthorizeSync.setUploadStatus(uploadStatus);
                authorizeCenterAuthorizeSync.setPushStatus(pushStatus);
                authorizeCenterAuthorizeSync.setUpdateBy(updateBy);
                authorizeCenterAuthorizeSync.setUpdateTime(new Date());
                this.baseMapper.updateById(authorizeCenterAuthorizeSync);
            }
        }else{
            addAuthorizeSync(authId,authEqualUuid,syncName,sync,uploadStatus,pushStatus,syncOptType,createBy,updateBy);
        }
    }

    @Override
    public void saveAuthorizeSyncByAuthId(Long authId,String authEqualUuid,String syncName,Integer sync,Integer uploadStatus,Integer pushStatus,String syncOptType,String createBy,String updateBy) {
        AuthorizeCenterAuthorizeSync authorizeCenterAuthorizeSync=this.baseMapper.selectSyncByAuthId(authId);
        if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeSync)) {
            authorizeCenterAuthorizeSync.setSync(sync);
            authorizeCenterAuthorizeSync.setUploadStatus(uploadStatus);
            authorizeCenterAuthorizeSync.setPushStatus(pushStatus);
            authorizeCenterAuthorizeSync.setUpdateBy(updateBy);
            authorizeCenterAuthorizeSync.setUpdateTime(new Date());
            this.baseMapper.updateById(authorizeCenterAuthorizeSync);
        }else{
            addAuthorizeSync(authId,authEqualUuid,syncName,sync,uploadStatus,pushStatus,syncOptType,createBy,updateBy);
        }
    }


    private void addAuthorizeSync(Long authId,String authEqualUuid,String syncName,Integer sync,Integer uploadStatus,Integer pushStatus,String syncOptType,String createBy,String updateBy) {
        AuthorizeCenterAuthorizeSync authorizeCenterAuthorizeSync=new AuthorizeCenterAuthorizeSync();
        authorizeCenterAuthorizeSync.setAuthId(authId);
        authorizeCenterAuthorizeSync.setAuthEqualUuid(authEqualUuid);
        authorizeCenterAuthorizeSync.setSyncOptType(syncOptType);
        authorizeCenterAuthorizeSync.setSyncName(syncName);
        authorizeCenterAuthorizeSync.setSync(sync);
        authorizeCenterAuthorizeSync.setUploadStatus(uploadStatus);
        authorizeCenterAuthorizeSync.setPushStatus(pushStatus);
        authorizeCenterAuthorizeSync.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
        authorizeCenterAuthorizeSync.setUpdateBy(updateBy);
        authorizeCenterAuthorizeSync.setCreateBy(createBy);
        authorizeCenterAuthorizeSync.setCreateTime(new Date());
        this.baseMapper.insert(authorizeCenterAuthorizeSync);
    }

}

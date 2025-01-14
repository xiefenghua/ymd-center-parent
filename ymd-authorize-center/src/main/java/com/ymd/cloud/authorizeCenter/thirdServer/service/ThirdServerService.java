package com.ymd.cloud.authorizeCenter.thirdServer.service;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.enums.DeviceWarnTypeToNoticeEnum;
import com.ymd.cloud.authorizeCenter.enums.LockTypeDeviceAndCommModeEnum;
import com.ymd.cloud.authorizeCenter.job.third.ThirdSupportAuthChangeSyncTask;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeRecordMapper;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeSyncMapper;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockMapper;
import com.ymd.cloud.authorizeCenter.mapper.third.ThirdServerAuthReturnLogMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeSync;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.model.third.ThirdServerAuthReturnLog;
import com.ymd.cloud.authorizeCenter.thirdServer.sever.WebSocketServerService;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class ThirdServerService {
    private EventCenterServiceClientApi eventCenterServiceClientApi;
    @Resource
    ThirdServerAuthReturnLogMapper thirdServerAuthReturnLogMapper;
    @Resource
    AuthorizeCenterAuthorizeRecordMapper authorizeCenterAuthorizeRecordMapper;
    @Resource
    AuthorizeCenterAuthorizeSyncMapper authorizeCenterAuthorizeSyncMapper;
    @Autowired
    private RedisService redisService;
    @Resource
    LockMapper lockMapper;
    @Autowired
    ThirdSupportAuthChangeSyncTask thirdSupportAuthChangeSyncTask;

    public void syncThirdAllAuth(String serialNo){
        try {
            //获取未同步的所有授权
            Lock lock=lockMapper.selectByMac(serialNo);
            if(EmptyUtil.isNotEmpty(lock)){
                if (LockTypeDeviceAndCommModeEnum.DOOR_FACE.getLockType().equals(lock.getLocktype()) &&
                        LockTypeDeviceAndCommModeEnum.DOOR_FACE.getDeviceMode().equals(lock.getDeviceMode()) &&
                        LockTypeDeviceAndCommModeEnum.DOOR_FACE.getCommMode().equals(lock.getCommMode())) {
                    String vendor = lock.getVentor();
                    thirdSupportAuthChangeSyncTask.pushAuth(lock.getLockid(),serialNo,vendor);
                }
            }
        }catch (Exception e){
            log.error("锐颖设备设备序列号:{}>>>>启动异步同步所有未授权数据失败:{}",serialNo,e);
        }finally {
            redisService.remove(serialNo+"third_all_auth_sync_mq_topic");
        }
    }

    public AuthorizeCenterAuthorizeRecord getAuthorizeById(Long authId) {
        return authorizeCenterAuthorizeRecordMapper.selectById(authId);
    }
    public void updateSyncById(Long authId) {
        AuthorizeCenterAuthorizeSync authorizeCenterAuthorizeSync=authorizeCenterAuthorizeSyncMapper.selectSyncByAuthId(authId);
        if(EmptyUtil.isNotEmpty(authorizeCenterAuthorizeSync)) {
            authorizeCenterAuthorizeSync.setSync(Constants.SYNC);
            authorizeCenterAuthorizeSync.setUploadStatus(Constants.PUSH_STATUS);
            authorizeCenterAuthorizeSync.setUpdateTime(new Date());
            authorizeCenterAuthorizeSyncMapper.updateById(authorizeCenterAuthorizeSync);
        }
    }
    public void saveThirdPartyReturnLog(ThirdServerAuthReturnLog model) {
        ThirdServerAuthReturnLog temp= thirdServerAuthReturnLogMapper.selectReturnLogBySerialNo(model);
        model.setCreateTime(new Date());
        if(EmptyUtil.isNotEmpty(temp)){
            model.setId(temp.getId());
            thirdServerAuthReturnLogMapper.updateById(model);
        }else {
            thirdServerAuthReturnLogMapper.insert(model);
        }
    }
    public void delThirdPartyReturnLog(ThirdServerAuthReturnLog model) {
        ThirdServerAuthReturnLog temp= thirdServerAuthReturnLogMapper.selectReturnLogBySerialNo(model);
        if(EmptyUtil.isNotEmpty(temp)){
            thirdServerAuthReturnLogMapper.deleteById(temp.getId());
        }
    }
    public void saveThird(JSONObject notifyJson,String serialNo,String userAccount){
        try {
            ThirdServerAuthReturnLog model=new ThirdServerAuthReturnLog();
            model.setMsgId(notifyJson.getLong("id"));
            model.setSerialNo(serialNo);
            model.setMethod(notifyJson.getString("method"));
            if(userAccount!=null) {
                model.setUserAccount(userAccount);
            }
            model.setMsgJson(notifyJson.toString());
            model.setCreateTime(new Date());
            delThirdPartyReturnLog(model);
            saveThirdPartyReturnLog(model);
        }catch (Exception e){ log.error("tblThirdPartyReturnLogService.save报错:{}",e); }
    }
    public Long getId(Long id){
        if(EmptyUtil.isNotEmpty(id)){
            if(id.longValue()==0){
                return IdGen.randomLong();
            }else {
                return id;
            }
        }else{
            return IdGen.randomLong();
        }
    }
    public void pushThirdAuthReturn(String serialNo,String authId,String type,String userAccount,String opType){
        JSONObject msgBody=new JSONObject();
        msgBody.put("serialNo",serialNo);
        msgBody.put("authId",authId);
        msgBody.put("type",type);
        msgBody.put("userAccount",userAccount);
        msgBody.put("opType",opType);
        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.third_sync_auth_return.name());
        EventCenterPush eventCenterPush=new EventCenterPush();
        eventCenterPush.setTopic(topic);
        eventCenterPush.setMsgBody(msgBody.toJSONString());
        eventCenterPush.setCreateAuthUserId(userAccount);
        eventCenterPush.setRemark("第三方授权数据推送");
        eventCenterServiceClientApi.push(eventCenterPush);
    }
    public void syncThirdAllAuthMq(String serialNo){
        if(!redisService.exists(serialNo+"third_all_auth_sync_mq_topic")) {
            redisService.set(serialNo+"third_all_auth_sync_mq_topic","true",3600*12);
            syncThirdAllAuth(serialNo);
            redisService.remove(serialNo+"third_all_auth_sync_mq_topic");
        }
    }
    public void heartOnline(String serialNo,boolean onlineStatus){
        Date onlineTime=new Date();
        //TODO 更新设备在线时间

        redisService.hdel(WebSocketServerService.heartKey,serialNo);
        redisService.hdel(WebSocketServerService.connectCountKey,serialNo);
        if(onlineStatus) {
            Long timeStamp=System.currentTimeMillis();
            redisService.hset(WebSocketServerService.heartKey,serialNo,WebSocketServerService.PONG+"_"+timeStamp);
            syncThirdAllAuthMq(serialNo);
        }else {
            updateWarm(serialNo,onlineStatus?DeviceWarnTypeToNoticeEnum.ONLINE: DeviceWarnTypeToNoticeEnum.OFFLINE);
        }
    }
    public void updateWarm(String serialNo,DeviceWarnTypeToNoticeEnum  warnTypeToNoticeEnum){
        Lock lock=lockMapper.selectByMac(serialNo);
        if(EmptyUtil.isNotEmpty(lock)){
            //TODO 在离线设备 触发告警
            if(warnTypeToNoticeEnum.warnType==DeviceWarnTypeToNoticeEnum.ONLINE.warnType) {

            }else{

            }
        }
    }
}

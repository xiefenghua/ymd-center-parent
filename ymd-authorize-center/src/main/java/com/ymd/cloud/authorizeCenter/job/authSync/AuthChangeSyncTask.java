package com.ymd.cloud.authorizeCenter.job.authSync;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ymd.cloud.authorizeCenter.enums.AuthConstants;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.job.pushGateWay.PushMsgGateWayDevice;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeLockChangeMapper;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeLogMapper;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeRecordMapper;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeLockChange;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeLog;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeTemplateData;
import com.ymd.cloud.authorizeCenter.model.entity.AuthListParamQuery;
import com.ymd.cloud.authorizeCenter.model.entity.AuthorizeDataVo;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.util.AuthorizeUtil;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * @description 授权同步
 */
@Slf4j
@Component
public class AuthChangeSyncTask {
    @Autowired
    PushMsgGateWayDevice pushMsgGateWayDevice;
    @Autowired
    AuthDataCompare authDataCompare;
    @Resource
    AuthorizeCenterAuthorizeRecordMapper authorizeCenterAuthorizeRecordMapper;
    @Resource
    LockMapper lockMapper;
    @Resource
    AuthorizeCenterAuthorizeLockChangeMapper authorizeCenterAuthorizeLockChangeMapper;
    @Resource
    AuthorizeCenterAuthorizeLogMapper authorizeCenterAuthorizeLogMapper;
    //授权全量同步(新版)
    public List<AuthorizeCenterAuthorizeRecord> pushAuthorizeAll(String lockId,String lockMac,String gateMac) {
        List<AuthorizeCenterAuthorizeRecord> authAllResultList=new ArrayList<>();
        try {
            AuthListParamQuery allAuthRecordQuery=new AuthListParamQuery();
            allAuthRecordQuery.setLockId(lockId);
            allAuthRecordQuery.setStartDate(new Date());
            allAuthRecordQuery.setEndDate(new Date(DateUtil.delayTime(AuthConstants.authTimeDiff)));
            List<AuthorizeCenterAuthorizeRecord> allAuthList=authorizeCenterAuthorizeRecordMapper.selectEqualAuthListByLockAndUserAndType(allAuthRecordQuery);
            if (EmptyUtil.isNotEmpty(allAuthList)) {
                allAuthList=authDataCompare.compareAuthList(allAuthList);
                for (AuthorizeCenterAuthorizeRecord allAuthorizeRecord : allAuthList) {
                    Integer state = allAuthorizeRecord.getStatus();
                    Integer flag = allAuthorizeRecord.getFlag();
                    Integer freezeStatus = allAuthorizeRecord.getFreezeStatus();
                    if(state==1&&flag==1&freezeStatus==1) {
                        authAllResultList.add(allAuthorizeRecord);
                    }
                }
                pushAuthClassifyProcess(authAllResultList,lockId,lockMac,gateMac,true);
            }
        } catch (Exception e) {
            log.error("授权全量推送异常",e);
        }
        return authAllResultList;
    }

    // 授权增量同步(新版)
    public List<AuthorizeCenterAuthorizeRecord> pushAuthorizeIncrement(String lockId,String lockMac,String gateMac) {
        List<AuthorizeCenterAuthorizeRecord> authIncrementResultList=new ArrayList<>();
        try {
            AuthListParamQuery incrementAuthRecordQuery=new AuthListParamQuery();
            incrementAuthRecordQuery.setLockId(lockId);
            incrementAuthRecordQuery.setStartDate(new Date());
            incrementAuthRecordQuery.setEndDate(new Date(DateUtil.delayTime(AuthConstants.authTimeDiff)));
            List<AuthorizeCenterAuthorizeRecord> authIncrementList=authorizeCenterAuthorizeRecordMapper.selectIncrementEqualAuthListByLockAndUserAndType(incrementAuthRecordQuery);
            if (EmptyUtil.isNotEmpty(authIncrementList)) {
                authIncrementList = authDataCompare.compareAuthList(authIncrementList);
                for (AuthorizeCenterAuthorizeRecord incrementAuthRecord : authIncrementList) {
                    if (incrementAuthRecord.getSync() == 0) {
                        authIncrementResultList.add(incrementAuthRecord);
                    }
                }
                pushAuthClassifyProcess(authIncrementResultList,lockId,lockMac,gateMac,false);
            }
        } catch (Exception e) {
            log.error("授权增量同步异常(新版)",e);
        }
        return authIncrementResultList;
    }

    private void pushAuthClassifyProcess(List<AuthorizeCenterAuthorizeRecord> authIncrementList,String lockId,String lockMac,String gateMac,boolean isAll) {
        //==========================卡片=====================================
        StringBuilder cardAuthIncrementCrcBuff = new StringBuilder();
        List<AuthorizeDataVo> cardAuthSyncIncrementList = new ArrayList<>();
        //==========================密码=====================================
        StringBuilder pwdAuthIncrementCrcBuff = new StringBuilder();
        List<AuthorizeDataVo> pwdAuthSyncIncrementList = new ArrayList<>();
        //==========================指纹=====================================
        StringBuilder fingerAuthIncrementCrcBuff = new StringBuilder();
        List<AuthorizeDataVo> fingerAuthSyncIncrementList = new ArrayList<>();
        //==========================人脸=====================================
        StringBuilder faceAuthIncrementCrcBuff = new StringBuilder();
        List<AuthorizeDataVo> faceAuthSyncIncrementList = new ArrayList<>();
        int packageNum=0;
        Integer cmd=null;
        //处理废数据
        for (AuthorizeCenterAuthorizeRecord authorizeDataVo : authIncrementList) {
            //TODO 根据模版id查询模版特征值  dncode 指纹id等数据
            AuthorizeCenterAuthorizeTemplateData templateData=new AuthorizeCenterAuthorizeTemplateData();


            String channelModuleType = authorizeDataVo.getAuthChannelModuleType();
            if (ChannelTypeEnums.MODULETYPE_01.equals(channelModuleType)) {//密码
                packageNum=PwdSyncDataJson.PACKAGE_NUM;
                if(isAll){
                    cmd = PwdSyncDataJson.CMD_FOR_PUSH_PWD_ALL;
                }else {
                    cmd = PwdSyncDataJson.CMD_FOR_PUSH_PWD_INCREMENT;
                }
                Lock lock=lockMapper.selectByLockId(lockId);
                AuthorizeDataVo vo = PwdSyncDataJson.createAuthorizeDataVo(authorizeDataVo,lock.getActivedays(),templateData);
                pwdAuthIncrementCrcBuff.append(PwdSyncDataJson.createCrcBuff(vo));
                pwdAuthSyncIncrementList.add(vo);
            } else if (ChannelTypeEnums.MODULETYPE_02.equals(channelModuleType)) {//刷卡
                packageNum=CardSyncDataJson.PACKAGE_NUM;
                if(isAll) {
                    cmd = CardSyncDataJson.CMD_FOR_PUSH_AUTHORIZE_ALL;
                }else {
                    cmd = CardSyncDataJson.CMD_FOR_PUSH_AUTHORIZE_INCREMENT;
                }
                AuthorizeDataVo vo = CardSyncDataJson.createAuthorizeDataVo(authorizeDataVo,templateData);
                cardAuthIncrementCrcBuff.append(CardSyncDataJson.createCrcBuff(vo));
                cardAuthSyncIncrementList.add(vo);
            } else if (ChannelTypeEnums.MODULETYPE_04.equals(channelModuleType)) {//指纹
                packageNum=FingerSyncDataJson.PACKAGE_NUM;
                cmd=FingerSyncDataJson.CMD_FOR_PUSH_FINGER_AUTHORIZE;
                AuthorizeDataVo vo = FingerSyncDataJson.createAuthorizeDataVo(authorizeDataVo,templateData);
                fingerAuthIncrementCrcBuff.append(FingerSyncDataJson.createCrcBuff(vo));
                fingerAuthSyncIncrementList.add(vo);
            } else if (ChannelTypeEnums.MODULETYPE_06.equals(channelModuleType)) {//人脸
                packageNum=FaceSyncDataJson.PACKAGE_NUM;
                cmd=FaceSyncDataJson.CMD_FOR_PUSH_FACE_AUTHORIZE;
                AuthorizeDataVo vo = FaceSyncDataJson.createAuthorizeDataVo(authorizeDataVo,templateData);
                faceAuthIncrementCrcBuff.append(FaceSyncDataJson.createCrcBuff(vo));
                faceAuthSyncIncrementList.add(vo);
            }
        }
        if (EmptyUtil.isNotEmpty(cardAuthSyncIncrementList)){
            batchPushAuthBody(cardAuthSyncIncrementList, cardAuthIncrementCrcBuff.toString(), ChannelTypeEnums.MODULETYPE_02, lockId,lockMac,gateMac, false,packageNum,cmd);
        }
        if (EmptyUtil.isNotEmpty(pwdAuthSyncIncrementList)){
            batchPushAuthBody(pwdAuthSyncIncrementList, pwdAuthIncrementCrcBuff.toString(), ChannelTypeEnums.MODULETYPE_01, lockId,lockMac, gateMac, false,packageNum,cmd);
        }
        if (EmptyUtil.isNotEmpty(fingerAuthSyncIncrementList)){
            batchPushAuthBody(fingerAuthSyncIncrementList, fingerAuthIncrementCrcBuff.toString(), ChannelTypeEnums.MODULETYPE_04, lockId,lockMac, gateMac, false,packageNum,cmd);
        }
        if (EmptyUtil.isNotEmpty(faceAuthSyncIncrementList)){
            batchPushAuthBody(faceAuthSyncIncrementList, faceAuthIncrementCrcBuff.toString(), ChannelTypeEnums.MODULETYPE_06, lockId,lockMac, gateMac, false,packageNum,cmd);
        }
    }

    private void batchPushAuthBody(List<AuthorizeDataVo> authList,String crcBuff,String channelModuleType,String lockId,String lockMac,
                                   String gateMac,boolean isAll,int packageNum,Integer cmd) {
        Integer packageIndex=0;
        int authListSize=authList.size();
        if (authListSize<= packageNum ){
            if(authListSize>0||(isAll)) {
                doPushChannelTypeAuthorizeBody(authList,crcBuff,authList,crcBuff,channelModuleType,lockId,lockMac,
                gateMac,packageIndex,cmd);
            }
        }else{
            List<List<AuthorizeDataVo>> packageLists = Lists.partition(authList,packageNum);
            for (int i = 0; i < packageLists.size(); i++) {
                List<AuthorizeDataVo> packageList=packageLists.get(i);
                StringBuilder packageCrcBuff = new StringBuilder();
                for (AuthorizeDataVo vo:packageList) {
                    if (ChannelTypeEnums.MODULETYPE_01.equals(channelModuleType)) {//密码
                        packageCrcBuff.append(PwdSyncDataJson.createCrcBuff(vo));
                    } else if (ChannelTypeEnums.MODULETYPE_02.equals(channelModuleType)) {//刷卡
                        packageCrcBuff.append(CardSyncDataJson.createCrcBuff(vo));
                    } else if (ChannelTypeEnums.MODULETYPE_04.equals(channelModuleType)) {//指纹
                        packageCrcBuff.append(FingerSyncDataJson.createCrcBuff(vo));
                    } else if (ChannelTypeEnums.MODULETYPE_06.equals(channelModuleType)) {//人脸
                        packageCrcBuff.append(FaceSyncDataJson.createCrcBuff(vo));
                    }
                }
                doPushChannelTypeAuthorizeBody(authList,crcBuff,packageList,packageCrcBuff.toString(),channelModuleType,lockId,lockMac,
                        gateMac,packageIndex,cmd);
            }
        }
    }
    private void doPushChannelTypeAuthorizeBody(List<AuthorizeDataVo> authList, String crcBuff, List<AuthorizeDataVo> packageList, String packageCrcBuff
                                                , String channelModuleType, String lockId, String lockMac, String gateMac,Integer packageIndex,Integer cmd){
        JSONObject data =null;
        JSONArray array =null;
        AuthorizeCenterAuthorizeLockChange change=authorizeCenterAuthorizeLockChangeMapper.selectChangeByLockId(lockId);
        if(ChannelTypeEnums.MODULETYPE_01.equals(channelModuleType)){//密码
            int changeNo=change.getPwdHasChangeVersion();
            data = PwdSyncDataJson.createHeaderJson(lockMac, packageIndex,changeNo , authList.size(), AuthorizeUtil.getCRC(crcBuff),
                    packageList.size(), AuthorizeUtil.getCRC(packageCrcBuff), authList.size());
            array = PwdSyncDataJson.createDataJsonList(packageList, cmd);
        }else if(ChannelTypeEnums.MODULETYPE_02.equals(channelModuleType)){//刷卡
            int changeNo=change.getCardHasChangeVersion();
            data = CardSyncDataJson.createHeaderJson(lockMac, packageIndex, changeNo, authList.size(), AuthorizeUtil.getCRC(crcBuff),
                    packageList.size(), AuthorizeUtil.getCRC(packageCrcBuff), authList.size());
            array = CardSyncDataJson.createDataJsonList(packageList, cmd);
        }else if(ChannelTypeEnums.MODULETYPE_04.equals(channelModuleType)){//指纹
            int changeNo=change.getFingerHasChangeVersion();
            data = FingerSyncDataJson.createHeaderJson(lockMac, packageIndex,changeNo , authList.size(), AuthorizeUtil.getCRC(crcBuff),
                    packageList.size(), AuthorizeUtil.getCRC(packageCrcBuff), authList.size());
            array = FingerSyncDataJson.createDataJsonList(packageList, cmd);
        }else if(ChannelTypeEnums.MODULETYPE_06.equals(channelModuleType)){//人脸
            int changeNo=change.getFaceHasChangeVersion();
            data = FaceSyncDataJson.createHeaderJson(lockMac, packageIndex,changeNo , authList.size(), AuthorizeUtil.getCRC(crcBuff),
                    packageList.size(), AuthorizeUtil.getCRC(packageCrcBuff), authList.size());
            array = FaceSyncDataJson.createDataJsonList(packageList, cmd);
        }
        if(data!=null) {
            data.put("list", array==null?new JSONArray():array);
            String res=pushMsgGateWayDevice.pushMsgEncryptedToDevice(data.toString(), cmd, gateMac);
            AuthorizeCenterAuthorizeLog authorizeCenterAuthorizeLog=new AuthorizeCenterAuthorizeLog();
            authorizeCenterAuthorizeLog.setModule("授权同步");
            authorizeCenterAuthorizeLog.setLockId(lockId);
            authorizeCenterAuthorizeLog.setOperateType("网关层mqtt同步");
            authorizeCenterAuthorizeLog.setOperateName(lockMac+"使用cmd="+cmd+"进行"+ChannelTypeEnums.getModuleTypeMapName(channelModuleType)+"授权同步");
            authorizeCenterAuthorizeLog.setOperation(null);
            authorizeCenterAuthorizeLog.setReqParam(data.toString());
            authorizeCenterAuthorizeLog.setRespResult(res);
            authorizeCenterAuthorizeLog.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
            authorizeCenterAuthorizeLog.setCreateTime(new Date());
            authorizeCenterAuthorizeLog.setCreateBy(null);
            authorizeCenterAuthorizeLogMapper.insert(authorizeCenterAuthorizeLog);
        }
    }







}

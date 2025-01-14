package com.ymd.cloud.authorizeCenter.job.third;

import com.google.common.collect.Lists;
import com.ymd.cloud.authorizeCenter.enums.AuthConstants;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.job.authSync.AuthDataCompare;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeRecordMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeTemplateData;
import com.ymd.cloud.authorizeCenter.model.entity.AuthListParamQuery;
import com.ymd.cloud.authorizeCenter.model.entity.AuthorizeDataVo;
import com.ymd.cloud.authorizeCenter.model.third.PersonInfo;
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
 * @description 第三方设备授权同步
 */
@Slf4j
@Component
public class ThirdSupportAuthChangeSyncTask {
    public final static Integer packageNum=1000;
    private String lockId;
    private String serialNo;
    private String vendor;
    @Autowired
    private AuthDataCompare authDataCompare;
    @Autowired
    private PersonAuthSupportTask personAuthSupportTask;
    @Autowired
    private CardAuthSupportTask cardAuthSupportTask;
    @Autowired
    private PwdAuthSupportTask pwdAuthSupportTask;
    @Autowired
    private FaceAuthSupportTask faceAuthSupportTask;
    @Resource
    AuthorizeCenterAuthorizeRecordMapper authorizeCenterAuthorizeRecordMapper;

    public List<AuthorizeCenterAuthorizeRecord> pushAuth(String lockId,String serialNo,String vendor) {
        List<AuthorizeCenterAuthorizeRecord> authResultList=new ArrayList<>();
        try {
            this.lockId=lockId;
            this.serialNo=serialNo;
            this.vendor=vendor;
            AuthListParamQuery thirdAuthRecordQuery=new AuthListParamQuery();
            thirdAuthRecordQuery.setLockId(lockId);
            thirdAuthRecordQuery.setStartDate(new Date());
            thirdAuthRecordQuery.setEndDate(new Date(DateUtil.delayTime(AuthConstants.authTimeDiff)));
            List<AuthorizeCenterAuthorizeRecord> authList=authorizeCenterAuthorizeRecordMapper.selectIncrementEqualAuthListByLockAndUserAndType(thirdAuthRecordQuery);
            if (EmptyUtil.isNotEmpty(authList)) {
                authList = authDataCompare.compareAuthList(authList);
                for (AuthorizeCenterAuthorizeRecord record : authList) {
                    if (record.getSync() == 0) {
                        authResultList.add(record);
                    }
                }
                pushAuthClassifyProcess(authResultList);
            }
        } catch (Exception e) {
            log.error("third授权同步异常(新版)",e);
        }
        return authResultList;
    }

    private void pushAuthClassifyProcess(List<AuthorizeCenterAuthorizeRecord> authList) {
        //==========================卡片=====================================
        List<AuthorizeCenterAuthorizeRecord> cardAuthSyncList = new ArrayList<>();
        //==========================密码=====================================
        List<AuthorizeCenterAuthorizeRecord> pwdAuthSyncList = new ArrayList<>();
        //==========================指纹=====================================
        List<AuthorizeCenterAuthorizeRecord> fingerAuthSyncList = new ArrayList<>();
        //==========================人脸=====================================
        List<AuthorizeCenterAuthorizeRecord> faceAuthSyncList = new ArrayList<>();
        //处理废数据
        for (AuthorizeCenterAuthorizeRecord record : authList) {
            String channelModuleType = record.getAuthChannelModuleType();
            if (ChannelTypeEnums.MODULETYPE_01.equals(channelModuleType)) {//密码
                pwdAuthSyncList.add(record);
            } else if (ChannelTypeEnums.MODULETYPE_02.equals(channelModuleType)) {//刷卡
                cardAuthSyncList.add(record);
            } else if (ChannelTypeEnums.MODULETYPE_04.equals(channelModuleType)) {//指纹
                fingerAuthSyncList.add(record);
            } else if (ChannelTypeEnums.MODULETYPE_06.equals(channelModuleType)) {//人脸
                faceAuthSyncList.add(record);
            }
        }
        if (EmptyUtil.isNotEmpty(cardAuthSyncList)){
            batchPushAuthBody(cardAuthSyncList,ChannelTypeEnums.MODULETYPE_02);
        }
        if (EmptyUtil.isNotEmpty(pwdAuthSyncList)){
            batchPushAuthBody(pwdAuthSyncList,ChannelTypeEnums.MODULETYPE_01);
        }
        if (EmptyUtil.isNotEmpty(fingerAuthSyncList)){
            batchPushAuthBody(fingerAuthSyncList, ChannelTypeEnums.MODULETYPE_04);
        }
        if (EmptyUtil.isNotEmpty(faceAuthSyncList)){
            batchPushAuthBody(faceAuthSyncList, ChannelTypeEnums.MODULETYPE_06);
        }
    }

    private void batchPushAuthBody(List<AuthorizeCenterAuthorizeRecord> authList,String channelModuleType) {
        Integer packageIndex=0;
        int authListSize=authList.size();
        if (authListSize<= packageNum ){
            if(authListSize>0) {
                doPushChannelTypeAuthorizeBody(authList,channelModuleType,packageIndex);
            }
        }else{
            List<List<AuthorizeCenterAuthorizeRecord>> packageLists = Lists.partition(authList,packageNum);
            for (int i = 0; i < packageLists.size(); i++) {
                List<AuthorizeCenterAuthorizeRecord> packageList=packageLists.get(i);
                doPushChannelTypeAuthorizeBody(packageList,channelModuleType,i);
            }
        }
    }
    private void doPushChannelTypeAuthorizeBody(List<AuthorizeCenterAuthorizeRecord> authList,String channelModuleType,Integer packageIndex){
        for (AuthorizeCenterAuthorizeRecord authorizeRecord:authList) {
            AuthorizeDataVo vo=new AuthorizeDataVo();
            vo=AuthDataCompare.convertAuthorizeDataVoCommon(authorizeRecord, vo);
            Long authId=authorizeRecord.getId();
            Date startTime=authorizeRecord.getStartTime();
            Date endTime=authorizeRecord.getEndTime();
            String optType=vo.getCode()==1?"d":"a";

            String userAccount=authorizeRecord.getUserAccount();
            String realName=authorizeRecord.getUserName();
            if (EmptyUtil.isEmpty(userAccount)) {
                if (EmptyUtil.isNotEmpty(realName)) {
                    if (!realName.matches("[\u4e00-\u9fa5]")) {
                        userAccount = "temp_person_" + authId;
                    } else {
                        userAccount = "temp_person_" + realName;
                    }
                } else {
                    userAccount = "temp_person_" + authId;
                }
            }
            realName=EmptyUtil.isEmpty(realName)?userAccount:realName;

            PersonInfo personInfo=new PersonInfo();
            personInfo.setUserAccount(userAccount);
            personInfo.setRealName(realName);
            personInfo.setCreateUser(EmptyUtil.isEmpty(authorizeRecord.getCreateBy())?userAccount:authorizeRecord.getCreateBy());
            //TODO 根据账号获取用户 身份证 性别 生日 手机号

            //TODO 根据模版id查询 cardNo  dncode  等数据
            AuthorizeCenterAuthorizeTemplateData templateData=new AuthorizeCenterAuthorizeTemplateData();

            personAuthSupportTask.pushData(0l,vendor,optType,serialNo,personInfo);
            if (ChannelTypeEnums.MODULETYPE_01.equals(channelModuleType)) {//密码
                pwdAuthSupportTask.pushData(authId, startTime, endTime, authorizeRecord.getAuthChannelValue(),
                        vendor, optType, serialNo, personInfo);
            } else if (ChannelTypeEnums.MODULETYPE_02.equals(channelModuleType)) {//刷卡
                int cardType= ChannelTypeEnums.CHANNELTYPE_201.equals(authorizeRecord.getAuthChannelType())?1:2;

                String cardNo=null;
                String desc=null;
                cardAuthSupportTask.pushData(authId,startTime,endTime,cardType,
                        vendor,optType,serialNo,cardNo,desc,
                        personInfo);
            } else if (ChannelTypeEnums.MODULETYPE_04.equals(channelModuleType)) {//指纹

            } else if (ChannelTypeEnums.MODULETYPE_06.equals(channelModuleType)) {//人脸
                faceAuthSupportTask.pushData(authId,startTime,endTime,templateData.getFeature()
                        ,authorizeRecord.getAuthChannelValue(),vendor, optType,lockId,serialNo,personInfo);
            }
        }
    }

}

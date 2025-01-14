package com.ymd.cloud.authorizeCenter.thirdServer.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeRecordMapper;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.entity.AuthListParamQuery;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.thirdServer.job.pushAuth.BtPushMsgUtil;
import com.ymd.cloud.authorizeCenter.thirdServer.service.ThirdServerService;
import com.ymd.cloud.authorizeCenter.thirdServer.sever.WebSocketServerService;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BtHandlerService implements HandlerService{
    @Autowired
    WebSocketServerService webSocketServer;
    @Autowired
    ThirdServerService thirdServerService;
    @Autowired
    BtPushMsgUtil btPushMsgUtil;
    @Resource
    LockMapper lockMapper;
    @Resource
    AuthorizeCenterAuthorizeRecordMapper authorizeCenterAuthorizeRecordMapper;
    public void savePersonsAndFaceImg(JSONObject body,String serialNo,Long id){
        String job=body.getString("job");
        JSONArray userList=body.getJSONArray("data");
        String userAccount=null;
        if(EmptyUtil.isNotEmpty(userList)&&userList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", thirdServerService.getId(id));
            notifyJson.put("method", "bt.savePersonsAndFaceImg");
            notifyJson.put("userAuthFeatureId",id);
            if(EmptyUtil.isNotEmpty(job)) {
                if("人脸授权".equals(job)) {
                    Lock lock=lockMapper.selectByMac(serialNo);
                    if(EmptyUtil.isNotEmpty(lock)) {
                        AuthListParamQuery authorizeRecordQuery = new AuthListParamQuery();
                        authorizeRecordQuery.setAuthChannelModuleType(ChannelTypeEnums.MODULETYPE_06);
                        authorizeRecordQuery.setLockId(lock.getLockid());
                        List<AuthorizeCenterAuthorizeRecord> authList = authorizeCenterAuthorizeRecordMapper.selectAuthListByDeviceUser(authorizeRecordQuery);
                        if(EmptyUtil.isNotEmpty(authList)) {
                            int total =authList.size();
                            long version = System.currentTimeMillis();
                            for (int i = 0; i < userList.size(); i++) {
                                JSONObject faceAuthJson = userList.getJSONObject(i);
                                String phone = faceAuthJson.getString("phone");
                                userAccount = faceAuthJson.getString("userAccount");
                                String realName=EmptyUtil.isNotEmpty(faceAuthJson.getString("realName"))?faceAuthJson.getString("realName"):userAccount;
                                String faceImgUrl = faceAuthJson.getString("faceImgUrl");
                                String startTime=faceAuthJson.getString("startTime");
                                String endTime=faceAuthJson.getString("endTime");
                                String feature=faceAuthJson.getString("feature");

                                btPushMsgUtil.addFacePush(id.toString(), userAccount, total, version,phone, realName,
                                        DateUtil.convertTimeLongToStr(startTime,DateUtil.yyyy_MM_ddHH_mm_ss), DateUtil.convertTimeLongToStr(endTime,DateUtil.yyyy_MM_ddHH_mm_ss),
                                        version, Constants.FLAG, serialNo,
                                        feature, faceImgUrl);
                            }
                        }
                    }
                }else{
                    thirdServerService.updateSyncById(id);
                }
            }
            notifyJson.put("params", userList);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
        }
    }
    public void delPersonsAndFaceImg(JSONObject body,String serialNo,Long id){
        String job=body.getString("job");
        JSONArray userList=body.getJSONArray("data");
        if(EmptyUtil.isNotEmpty(userList)&&userList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", thirdServerService.getId(id));
            notifyJson.put("method", "bt.delPersonsAndFaceImg");
            notifyJson.put("userAuthFeatureId",id);
            if(EmptyUtil.isNotEmpty(job)) {
                if ("人脸授权".equals(job)) {
                    long version = System.currentTimeMillis();
                    if (EmptyUtil.isNotEmpty(userList)) {
                        String phone = null;
                        String userAccount = null;
                        for (int i = 0; i < userList.size(); i++) {
                            JSONObject object = userList.getJSONObject(i);
                            phone = object.getString("phone");
                            userAccount = object.getString("userAccount");
                        }
                        List<String> arr = new ArrayList<>();
                        arr.add(phone);
                        Lock lock=lockMapper.selectByMac(serialNo);
                        if(EmptyUtil.isNotEmpty(lock)) {
                            AuthListParamQuery authorizeRecordQuery = new AuthListParamQuery();
                            authorizeRecordQuery.setAuthChannelModuleType(ChannelTypeEnums.MODULETYPE_06);
                            authorizeRecordQuery.setLockId(lock.getLockid());
                            List<AuthorizeCenterAuthorizeRecord> authList = authorizeCenterAuthorizeRecordMapper.selectAuthListByDeviceUser(authorizeRecordQuery);
                            if (EmptyUtil.isNotEmpty(authList)) {
                                int total = authList.size();
                                btPushMsgUtil.delFacePush(id.toString(), userAccount, version, arr, serialNo, total);
                            }
                        }
                    }
                } else {
                    thirdServerService.updateSyncById(id);
                }
            }
            notifyJson.put("params", userList);
            thirdServerService.saveThird(notifyJson,serialNo,null);
        }
    }

    @Override
    public void updateCard(JSONObject body, String serialNo, Long id) {
        JSONArray cardList=body.getJSONArray("data");
        btPushMsgUtil.addCardPush(body, serialNo);
    }

    @Override
    public void removeCard(JSONObject body, String serialNo, Long id) {
        JSONArray cardList=body.getJSONArray("data");
        btPushMsgUtil.delCardPush(body, serialNo);
    }

    @Override
    public void syncPwd(JSONObject body, int enable, String serialNo, Long id) {
        JSONArray pwdList=body.getJSONArray("data");
        if(enable==1) {
            btPushMsgUtil.addPwdPush(body, serialNo);
        }else if(enable==0) {
            btPushMsgUtil.delPwdPush(body, serialNo);
        }
    }

    @Override
    public void setVisitorCode(JSONObject body, String serialNo,Long id) {
        JSONArray dataList=body.getJSONArray("data");
        btPushMsgUtil.setVisitorCode(body, serialNo);
    }

    @Override
    public void clearAllGroup(JSONObject body, String serialNo) {
        JSONArray configList=body.getJSONArray("data");
        btPushMsgUtil.restart(body, serialNo);
    }

    @Override
    public void remoteOpenDoor(JSONObject body,String serialNo) {
        JSONArray dataList=body.getJSONArray("data");
        btPushMsgUtil.remoteOpenDoor(body, serialNo);
    }

    @Override
    public void update(JSONObject body, String serialNo) {
        JSONArray configList=body.getJSONArray("data");
        for (int i = 0; i < configList.size(); i++) {
            JSONObject params = configList.getJSONObject(i);
        }
    }

    @Override
    public void updateTime(JSONObject body, String serialNo) {
        JSONArray configList=body.getJSONArray("data");
        btPushMsgUtil.updateTime(body, serialNo);
    }

}

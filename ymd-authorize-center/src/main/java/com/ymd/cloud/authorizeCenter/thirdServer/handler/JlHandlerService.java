package com.ymd.cloud.authorizeCenter.thirdServer.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockMapper;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.thirdServer.job.pushAuth.JlPushMsgUtil;
import com.ymd.cloud.authorizeCenter.thirdServer.service.ThirdServerService;
import com.ymd.cloud.authorizeCenter.util.ImgToBase64;
import com.ymd.cloud.authorizeCenter.util.WebSocketPushMsgJson;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JlHandlerService implements HandlerService{
    private EventCenterServiceClientApi eventCenterServiceClientApi;
    @Autowired
    ThirdServerService thirdServerService;
    @Autowired
    JlPushMsgUtil jlPushMsgUtil;
    @Resource
    LockMapper lockMapper;
    @Override
    public void savePersonsAndFaceImg(JSONObject body, String serialNo, Long id) {
        JSONArray userList=body.getJSONArray("data");
        id=thirdServerService.getId(id);
        if(EmptyUtil.isNotEmpty(userList)&&userList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "jl.savePersonsAndFaceImg");
            notifyJson.put("userAuthFeatureId",id);

            String ossImgUrl = null;
            String realName=null;
            String userAccount=null;
            for (int i = 0; i < userList.size(); i++) {
                JSONObject userJsonObject = userList.getJSONObject(i);
                userAccount = userJsonObject.getString("userAccount");
                ossImgUrl=userJsonObject.getString("faceImgUrl");
                realName=EmptyUtil.isNotEmpty(userJsonObject.getString("realName"))?userJsonObject.getString("realName"):userAccount;
            }
            if(EmptyUtil.isNotEmpty(ossImgUrl)) {
                String imgBase64 = null;
                try {
                    // 巨龙需要人脸照片的base64
                    imgBase64 = ImgToBase64.getBase64(ossImgUrl);
                } catch (Exception e) {
                    log.error("【巨龙人脸下发解析ossimgUrl:{}成为base64时出错：{}】", ossImgUrl, e);
                }
                if (imgBase64 != null) {
                    Lock lock=lockMapper.selectByMac(serialNo);
                    if(EmptyUtil.isNotEmpty(lock)) {
                        boolean b = jlPushMsgUtil.addPerson(lock.getLockid(), serialNo,
                                jlPushMsgUtil.PERSON_TYPE_FOR_WHITE_LIST,
                                userAccount, realName, imgBase64);
                        if (b) {
                            //更新同步标识
                            thirdServerService.updateSyncById(id);
                        }
                    }
                }
            }
            notifyJson.put("params", userList);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
        }
    }

    @Override
    public void delPersonsAndFaceImg(JSONObject body, String serialNo, Long id) {
        id=thirdServerService.getId(id);
        JSONArray userList=body.getJSONArray("data");
        if(EmptyUtil.isNotEmpty(userList)&&userList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "jl.delPersonsAndFaceImg");
            notifyJson.put("userAuthFeatureId",id);

            String userAccount=null;
            for (int i = 0; i < userList.size(); i++) {
                JSONObject userJsonObject = userList.getJSONObject(i);
                userAccount=userJsonObject.getString("userAccount");
            }
            Lock lock=lockMapper.selectByMac(serialNo);
            if(EmptyUtil.isNotEmpty(lock)) {
                boolean b = jlPushMsgUtil.delPerson(lock.getLockid(), serialNo,
                        jlPushMsgUtil.PERSON_TYPE_FOR_WHITE_LIST,
                        userAccount);
                if (b) {
                    //更新同步标识
                    thirdServerService.updateSyncById(id);
                }
            }
            notifyJson.put("params", userList);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
        }
    }

    @Override
    public void updateCard(JSONObject body, String serialNo, Long id) {
        JSONArray cardList=body.getJSONArray("data");
    }

    @Override
    public void removeCard(JSONObject body, String serialNo, Long id) {
        JSONArray cardList=body.getJSONArray("data");
    }

    @Override
    public void syncPwd(JSONObject body, int enable, String serialNo, Long id) {
        JSONArray pwdList=body.getJSONArray("data");
        id=thirdServerService.getId(id);
        if(EmptyUtil.isNotEmpty(pwdList)&&pwdList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "jl.syncPwd");

            Lock lock=lockMapper.selectByMac(serialNo);
            if(EmptyUtil.isNotEmpty(lock)) {
                List pwdListTemp = new ArrayList();
                for (int i = 0; i < pwdList.size(); i++) {
                    JSONObject bean = pwdList.getJSONObject(i);
                    JSONObject passwordInfo = new JSONObject();
                    passwordInfo.put("PasswordId", id);
                    if (enable == 1) {
                        passwordInfo.put("PasswordType", 2);
                        passwordInfo.put("Password", EmptyUtil.isNotEmpty(bean.getString("password"))
                                ? bean.getString("password") : "123456");
                        passwordInfo.put("StartTime", bean.getString("startTime"));
                        passwordInfo.put("EndTime", bean.getString("endTime"));
                        passwordInfo.put("UserName", EmptyUtil.isNotEmpty(bean.getString("phone"))
                                ? bean.getString("phone") : Constants.commonPhone);
                        pwdListTemp.add(passwordInfo);
                        jlPushMsgUtil.addPwd(lock.getLockid(),serialNo, pwdListTemp);
                    } else {
                        pwdListTemp.add(passwordInfo);
                        jlPushMsgUtil.delPwd(lock.getLockid(),serialNo,pwdListTemp);
                    }
                }
            }
            notifyJson.put("params", pwdList);
            thirdServerService.saveThird(notifyJson,serialNo,null);
        }
    }

    @Override
    public void setVisitorCode(JSONObject body, String serialNo,Long id) {
        JSONArray dataList=body.getJSONArray("data");
        id=thirdServerService.getId(id);
        if(EmptyUtil.isNotEmpty(dataList)&&dataList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "jl.setVisitorCode");

            Lock lock=lockMapper.selectByMac(serialNo);
            if(EmptyUtil.isNotEmpty(lock)) {
                List pwdListTemp = new ArrayList();
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject user = dataList.getJSONObject(i);
                    String phone = EmptyUtil.isNotEmpty(user.getString("phone")) ?user.getString("phone"):Constants.commonPhone;
                    String password = user.getString("password");
                    String startTime = user.getString("startTime");
                    String endTime = user.getString("endTime");
                    String passwordId = id+"";
                    JSONObject passwordInfo=new JSONObject();
                    passwordInfo.put("PasswordId",passwordId);
                    passwordInfo.put("PasswordType",1);
                    passwordInfo.put("Password",password);
                    passwordInfo.put("StartTime",startTime);
                    passwordInfo.put("EndTime",endTime);
                    passwordInfo.put("TempUserName",phone);
                    passwordInfo.put("UserName",phone);
                    pwdListTemp.add(passwordInfo);
                }
                jlPushMsgUtil.addPwd(lock.getLockid(),serialNo, pwdListTemp);
            }
            notifyJson.put("params", dataList);
            thirdServerService.saveThird(notifyJson,serialNo,null);
        }
    }

    @Override
    public void clearAllGroup(JSONObject body, String serialNo) {
        JSONArray configList=body.getJSONArray("data");
        Lock lock=lockMapper.selectByMac(serialNo);
        if(EmptyUtil.isNotEmpty(lock)) {
            jlPushMsgUtil.clearAllGroup(lock.getLockid(),serialNo);
        }
    }

    @Override
    public void remoteOpenDoor(JSONObject body, String serialNo) {
        JSONArray dataList=body.getJSONArray("data");
        Lock lock=lockMapper.selectByMac(serialNo);
        if (EmptyUtil.isNotEmpty(lock)) {
            boolean a=jlPushMsgUtil.remoteOpenDoor(lock.getLockid(),serialNo,"0");
            if(a){
                log.info("远程开门  成功:{} dataLists:{]",dataList.toString());
                if(EmptyUtil.isNotEmpty(dataList)&&dataList.size()!=0) {
                    for (int i = 0; i < dataList.size(); i++) {
                        JSONObject user = dataList.getJSONObject(i);
                        JSONArray list = new JSONArray();
                        JSONObject beanJson = new JSONObject();
                        beanJson.put("serialNo", serialNo);
                        beanJson.put("address", lock.getAddress());
                        beanJson.put("userAccount", user.getString("userAccount"));
                        beanJson.put("openType", "Face");
                        list.add(beanJson);
                        JSONObject msgBody = WebSocketPushMsgJson.receiveJson("openDoor", list);

                        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.third_sync_open_door_record.name());
                        EventCenterPush eventCenterPush=new EventCenterPush();
                        eventCenterPush.setTopic(topic);
                        eventCenterPush.setMsgBody(msgBody.toJSONString());
                        eventCenterPush.setCreateAuthUserId(user.getString("userAccount"));
                        eventCenterPush.setRemark("第三方设备开门上报数据推送");
                        eventCenterServiceClientApi.push(eventCenterPush);
                    }
                }
            }
        }
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
        for (int i = 0; i < configList.size(); i++) {
            JSONObject params = configList.getJSONObject(i);
        }
    }
}

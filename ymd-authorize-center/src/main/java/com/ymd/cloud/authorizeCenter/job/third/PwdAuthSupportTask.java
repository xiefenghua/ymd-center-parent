package com.ymd.cloud.authorizeCenter.job.third;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.model.third.PersonInfo;
import com.ymd.cloud.authorizeCenter.util.WebSocketPushMsgJson;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author xfh
 * @version 1.0
 * @description 密码授权(第三方设备数据推送)
 */
@Slf4j
@Component
public class PwdAuthSupportTask {
    private EventCenterServiceClientApi eventCenterServiceClientApi;
    @Autowired
    private PersonAuthSupportTask personAuthSupportTask;
    @Autowired
    private DeviceConfigSupportTask deviceConfigSupportTask;

    public void pushData(long authId, Date startTime, Date endTime, String password,
                                 String vendor, String addOrDelType, String serialNo, PersonInfo personInfo){
        String userAccount=personInfo.getUserAccount();
        String phone = personInfo.getPhone();
        String realName=personInfo.getRealName();

        if (EmptyUtil.isEmpty(userAccount)) {
            if("a".equals(addOrDelType)) {
                //下发到访客中
                JSONObject config = new JSONObject();
                config.put("phone", phone);
                config.put("realName", realName);
                config.put("userAccount", userAccount);
                config.put("password", password);
                config.put("startTime", startTime.getTime()/1000);
                config.put("endTime", endTime.getTime()/1000);
                deviceConfigSupportTask.pushData(authId,personInfo.getCreateUser(),vendor,"6", serialNo, config);
            } else {
                if("a".equals(addOrDelType)) {
                    personInfo.setRealName(realName);
                    personInfo.setUserAccount(userAccount);
                    personInfo.setPhone(phone);
                    personAuthSupportTask.pushData(authId, vendor,addOrDelType,serialNo,personInfo);
                }
                pushPwdData(authId,startTime,endTime,password, vendor,addOrDelType, serialNo,personInfo);
            }
        } else {
            if("a".equals(addOrDelType)) {
                personAuthSupportTask.pushData(authId, vendor,addOrDelType,serialNo,personInfo);
            }
            pushPwdData(authId,startTime,endTime,password, vendor,addOrDelType, serialNo,personInfo);
        }
    }
    private void pushPwdData(long authId, Date startTime, Date endTime, String password,
                             String vendor, String addOrDelType, String serialNo, PersonInfo personInfo) {
        JSONArray dataList = new JSONArray();
        JSONObject auth = new JSONObject();
        auth.put("userAccount", personInfo.getUserAccount());
        auth.put("phone", EmptyUtil.isNotEmpty(personInfo.getPhone())?personInfo.getPhone(): Constants.commonPhone);
        auth.put("password", password);
        auth.put("startTime", startTime.getTime()/1000);
        auth.put("endTime", endTime.getTime()/1000);
        auth.put("realName", personInfo.getRealName());
        dataList.add(auth);
        JSONObject msgBody = WebSocketPushMsgJson.pushJson
                (authId,addOrDelType, "4", serialNo, dataList);
        msgBody.put("job","密码授权");

        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.third_sync_auth.name());
        EventCenterPush eventCenterPush=new EventCenterPush();
        eventCenterPush.setTopic(topic+ "/" + vendor + "/");
        eventCenterPush.setMsgBody(msgBody.toJSONString());
        eventCenterPush.setCreateAuthUserId(personInfo.getCreateUser());
        eventCenterPush.setRemark("第三方密码授权数据推送");
        eventCenterServiceClientApi.push(eventCenterPush);

    }
}

package com.ymd.cloud.authorizeCenter.job.third;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.model.third.PersonInfo;
import com.ymd.cloud.authorizeCenter.util.WebSocketPushMsgJson;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xfh
 * @version 1.0
 * @description app授权(第三方设备数据推送)
 */
@Slf4j
@Component
public class PersonAuthSupportTask {
    private EventCenterServiceClientApi eventCenterServiceClientApi;
    public void pushData(Long authId, String vendor, String addOrDelType, String serialNo, PersonInfo personInfo) {
        String userAccount=personInfo.getUserAccount();
        String phone = personInfo.getPhone();
        String realName=personInfo.getRealName();

        JSONArray dataList = new JSONArray();
        JSONObject userJson = new JSONObject();
        userJson.put("userAccount", userAccount);
        userJson.put("phone", phone);
        userJson.put("realName", realName);
        userJson.put("idCardNo", personInfo.getIdCardNo());
        userJson.put("sex", personInfo.getSex());
        userJson.put("birthday",personInfo.getBirthday());
        dataList.add(userJson);

        JSONObject msgBody = WebSocketPushMsgJson.pushJson(authId, addOrDelType, "2", serialNo, dataList);
        msgBody.put("job","app授权");
        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.third_sync_auth.name());
        EventCenterPush eventCenterPush=new EventCenterPush();
        eventCenterPush.setTopic(topic+ "/" + vendor + "/");
        eventCenterPush.setMsgBody(msgBody.toJSONString());
        eventCenterPush.setCreateAuthUserId(personInfo.getCreateUser());
        eventCenterPush.setRemark("第三方app授权数据推送");
        eventCenterServiceClientApi.push(eventCenterPush);
    }
}

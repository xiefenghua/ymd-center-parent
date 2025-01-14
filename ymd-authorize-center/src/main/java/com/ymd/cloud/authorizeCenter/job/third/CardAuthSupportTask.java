package com.ymd.cloud.authorizeCenter.job.third;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.model.third.PersonInfo;
import com.ymd.cloud.authorizeCenter.util.WebSocketPushMsgJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author xfh
 * @version 1.0
 * @description 卡片授权(第三方设备数据推送)
 */
@Slf4j
@Component
public class CardAuthSupportTask {
    private EventCenterServiceClientApi eventCenterServiceClientApi;
    public void pushData(long authId, Date startTime,Date endTime,int cardType,
                          String vendor, String addOrDelType, String serialNo,String cardNo,String desc,
                          PersonInfo personInfo) {
        JSONArray dataList = new JSONArray();
        JSONObject auth = new JSONObject();
        auth.put("cardNo", cardNo);
        if ("a".equals(addOrDelType)) {
            auth.put("userAccount", personInfo.getUserAccount());
            auth.put("cardType", cardType);
            auth.put("startTime", startTime.getTime()/1000);
            auth.put("endTime", endTime.getTime()/1000);
            auth.put("desc", desc);
            auth.put("realName", personInfo.getRealName());
        }
        dataList.add(auth);
        JSONObject msgBody = WebSocketPushMsgJson.pushJson
                (authId,addOrDelType, "3", serialNo, dataList);
        msgBody.put("job","卡片授权");

        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.third_sync_auth.name());
        EventCenterPush eventCenterPush=new EventCenterPush();
        eventCenterPush.setTopic(topic+ "/" + vendor + "/");
        eventCenterPush.setMsgBody(msgBody.toJSONString());
        eventCenterPush.setCreateAuthUserId(personInfo.getCreateUser());
        eventCenterPush.setRemark("第三方卡片授权数据推送");
        eventCenterServiceClientApi.push(eventCenterPush);

    }

}

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
 * @description 人脸授权(第三方设备数据推送)
 */
@Slf4j
@Component
public class FaceAuthSupportTask{
    private EventCenterServiceClientApi eventCenterServiceClientApi;
    public void pushData(Long authId, Date startTime, Date endTime,String feature,
                         String faceImgUrl, String vendor, String addOrDelType,String lockId,String serialNo, PersonInfo personInfo) {
        String[] addOrDelTypeArr = addOrDelType.split(",");
        for (int i = 0; i < addOrDelTypeArr.length; i++) {
            JSONArray dataList = new JSONArray();
            JSONObject user = new JSONObject();
            user.put("userAccount", personInfo.getUserAccount());
            user.put("phone", personInfo.getPhone());
            String addOrDel = addOrDelTypeArr[i];
            if ("a".equals(addOrDel)) {
                user.put("idCardNo", personInfo.getIdCardNo());
                user.put("realName", personInfo.getRealName());
                user.put("sex", personInfo.getSex());
                user.put("birthday", "1970-01-01");
                user.put("faceImgUrl", faceImgUrl);
                user.put("startTime", startTime.getTime()/1000);
                user.put("endTime", endTime.getTime()/1000);
                user.put("lockId",lockId);
                user.put("feature",feature);
            }
            dataList.add(user);
            JSONObject msgBody = WebSocketPushMsgJson.pushJson
                    (authId, addOrDel, "2", serialNo, dataList);
            msgBody.put("job", "人脸授权");

            String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.third_sync_auth.name());
            EventCenterPush eventCenterPush=new EventCenterPush();
            eventCenterPush.setTopic(topic+ "/" + vendor + "/");
            eventCenterPush.setMsgBody(msgBody.toJSONString());
            eventCenterPush.setCreateAuthUserId(personInfo.getCreateUser());
            eventCenterPush.setRemark("第三方人脸授权数据推送");
            eventCenterServiceClientApi.push(eventCenterPush);

        }

    }
}

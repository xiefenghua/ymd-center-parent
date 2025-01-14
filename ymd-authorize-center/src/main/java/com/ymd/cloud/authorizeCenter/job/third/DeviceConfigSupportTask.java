package com.ymd.cloud.authorizeCenter.job.third;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.util.WebSocketPushMsgJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xfh
 * @version 1.0
 * @description 设备配置(第三方设备数据推送)
 */
@Slf4j
@Component
public class DeviceConfigSupportTask{
    private EventCenterServiceClientApi eventCenterServiceClientApi;
    public void pushData(Long authId,String createUser,String vendor,String cmd,String serialNo,JSONObject config) {
        JSONArray dataList = new JSONArray();
        dataList.add(config);
        JSONObject msgBody = WebSocketPushMsgJson.pushJson
                (authId, cmd, serialNo, dataList);

        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.third_sync_config.name());
        EventCenterPush eventCenterPush=new EventCenterPush();
        eventCenterPush.setTopic(topic+ "/" + vendor + "/");
        eventCenterPush.setMsgBody(msgBody.toJSONString());
        eventCenterPush.setCreateAuthUserId(createUser);
        eventCenterPush.setRemark("第三方设备配置数据推送");
        eventCenterServiceClientApi.push(eventCenterPush);
    }
}

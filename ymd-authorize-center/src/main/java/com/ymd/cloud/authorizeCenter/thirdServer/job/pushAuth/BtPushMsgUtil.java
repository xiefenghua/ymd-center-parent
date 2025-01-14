package com.ymd.cloud.authorizeCenter.thirdServer.job.pushAuth;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.enums.MqMsg;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

/**
 * @author xfh
 * @version 1.0
 * @description
 * @date 2022/9/20 13:09
 */
@Component
@Slf4j
public class BtPushMsgUtil {
    private EventCenterServiceClientApi eventCenterServiceClientApi;
    private  String getTopic(String mac){
        String topic=eventCenterServiceClientApi.getTopicByTopicType(TopicType.bt_face_auth_event.name());
        StringBuilder builder=new StringBuilder();
        builder.append(topic, 0, topic.lastIndexOf("/"));
        builder.append("/");
        builder.append(mac);
        return builder.toString();
    }

    /**
     *新增或者修改授权
     */
    public void addFacePush(String authId,String userAccount,int total,long maxVersion,String idCardNo,String name,String startTime,String endTime,
                                    long version,int state,String mac,String featureData,String path){
        JSONObject json = new JSONObject(true);
        json.put("cmd", MqMsg.ADD_USER_AUTH.getCmd());
        json.put("total",String.valueOf(total));
        json.put("maxVersion",String.valueOf(maxVersion));
        JSONArray array=new JSONArray();
        JSONObject param=new JSONObject();
        param.put("userAccount",userAccount);
        param.put("idCardNo",idCardNo);
        param.put("name",name);
        param.put("allTime",false);
        param.put("startTime", startTime);
        param.put("endTime",endTime);
        param.put("times",-1);
        param.put("status",1);
        param.put("path",path);
        param.put("remark",null);
        param.put("feature",featureData);
        if(EmptyUtil.isNotEmpty(featureData)){
            param.put("featureType",1);
        }else {
            param.put("featureType", null);
        }
        param.put("version",String.valueOf(version));
        param.put("type",String.valueOf(state));

        param.put("serialNo",mac);
        param.put("authId",authId);

        array.add(param);
        json.put("data",array);
        log.info("【比特】topic:{} 【新增授权】:{}",getTopic(mac),json.toJSONString(json, SerializerFeature.WriteMapNullValue));

        EventCenterPush eventCenterPush=new EventCenterPush();
        eventCenterPush.setTopic(getTopic(mac));
        eventCenterPush.setMsgBody(json.toJSONString(json, SerializerFeature.WriteMapNullValue));
        eventCenterPush.setCreateAuthUserId(userAccount);
        eventCenterPush.setRemark("第三方设备开门上报数据推送");
        eventCenterServiceClientApi.push(eventCenterPush);
    }

    /**
     * 删除授权
     */
    public void delFacePush(String authId,String userAccount,long maxVersion,List<String > arr,String mac,int total){
        JSONObject json = new JSONObject();
        json.put("cmd", MqMsg.DEL_USER_AUTH.getCmd());
        json.put("total",total);
        json.put("maxVersion", String.valueOf(maxVersion));
        json.put("time", DateUtil.format(new Date()));
        JSONArray array = new JSONArray();
        if(arr!=null && arr.size() >0) {
            for (int i = 0; i < arr.size(); i++) {
                JSONObject temp = new JSONObject();
                temp.put("idCardNo", arr.get(i));
                temp.put("userAccount",userAccount);
                temp.put("authId",authId);
                array.add(temp);
            }
            json.put("data", array);
            log.info("【比特】topic:{}【删除授权】:{}",getTopic(mac),json.toString());
            EventCenterPush eventCenterPush=new EventCenterPush();
            eventCenterPush.setTopic(getTopic(mac));
            eventCenterPush.setMsgBody(json.toJSONString(json, SerializerFeature.WriteMapNullValue));
            eventCenterPush.setCreateAuthUserId(userAccount);
            eventCenterPush.setRemark("第三方设备开门上报数据推送");
            eventCenterServiceClientApi.push(eventCenterPush);
        }
    }

    public void addCardPush(JSONObject body,String mac) {
        pushCommon(body,mac,MqMsg.ADD_CARD_CMD);
    }

    public void delCardPush(JSONObject body,String mac) {
        pushCommon(body,mac,MqMsg.DEL_CARD_CMD);
    }

    public void addPwdPush(JSONObject body,String mac) {
        pushCommon(body,mac,MqMsg.ADD_PWD_CMD);
    }

    public void delPwdPush(JSONObject body,String mac) {
        pushCommon(body,mac,MqMsg.DEL_PWD_CMD);
    }

    public void remoteOpenDoor(JSONObject body,String mac) {
        pushCommon(body,mac,MqMsg.REMOTE_OPEN_DOOR_CMD);
    }

    public void setVisitorCode(JSONObject body,String mac) {
        pushCommon(body,mac,MqMsg.SET_VISITOR_CODE_CMD);
    }

    public void updateTime(JSONObject body,String mac) {
        pushCommon(body,mac,MqMsg.SET_TIME_CODE_CMD);
    }
    public void restart(JSONObject body,String mac) {
        pushCommon(body,mac,MqMsg.RESTART_CMD);
    }

    public void pushCommon(JSONObject body,String mac,MqMsg mqMsg) {
        body.put("cmd", mqMsg.getCmd());
        EventCenterPush eventCenterPush=new EventCenterPush();
        eventCenterPush.setTopic(getTopic(mac));
        eventCenterPush.setMsgBody(body.toJSONString(body, SerializerFeature.WriteMapNullValue));
        eventCenterPush.setRemark("第三方设备开门上报数据推送");
        eventCenterServiceClientApi.push(eventCenterPush);
    }
}


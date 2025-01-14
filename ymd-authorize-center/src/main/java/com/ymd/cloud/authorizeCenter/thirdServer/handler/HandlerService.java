package com.ymd.cloud.authorizeCenter.thirdServer.handler;

import com.alibaba.fastjson.JSONObject;

public interface HandlerService {
     void savePersonsAndFaceImg(JSONObject notifyJson, String serialNo, Long id);
     void delPersonsAndFaceImg(JSONObject notifyJson, String serialNo, Long id);
     void updateCard(JSONObject notifyJson, String serialNo, Long id) ;
     void removeCard(JSONObject notifyJson, String serialNo, Long id);
     void syncPwd(JSONObject notifyJson, int enable, String serialNo, Long id);
     void setVisitorCode(JSONObject notifyJson, String serialNo, Long id);
     void clearAllGroup(JSONObject notifyJson, String serialNo);
     void remoteOpenDoor(JSONObject notifyJson, String serialNo);
     void update(JSONObject notifyJson, String serialNo);
     void updateTime(JSONObject notifyJson, String serialNo);
}

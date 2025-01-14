package com.ymd.cloud.authorizeCenter.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class WebSocketPushMsgJson {

    public static JSONObject pushJson(Long authId,String addOrDelType,String cmd,String serialNo,JSONArray data){
        JSONObject notifyJson=pushJson(authId,cmd,serialNo,data);
        notifyJson.put("addOrDelType",addOrDelType);
        return notifyJson;
    }
    public static JSONObject pushJson(Long authId,String cmd,String serialNo,JSONArray data){
        JSONObject notifyJson=new JSONObject();
        notifyJson.put("authId",authId==null?0:authId);
        notifyJson.put("cmd",cmd);
        notifyJson.put("serialNo",serialNo);
        notifyJson.put("data",data);
        return notifyJson;
    }
    public static JSONObject receiveJson(String cmd,JSONArray data){
        JSONObject notifyJson=new JSONObject();
        notifyJson.put("cmd",cmd);
        notifyJson.put("data",data);
        return notifyJson;
    }

}

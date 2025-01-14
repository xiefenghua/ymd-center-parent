package com.ymd.cloud.authorizeCenter.thirdServer.job.pushAuth;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.common.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 巨龙人脸机操作工具类
 */
@Component
@Slf4j
public class JlPushMsgUtil {
    public static Logger logger= LoggerFactory.getLogger(JlPushMsgUtil.class);
    public static final Integer PERSON_TYPE_FOR_WHITE_LIST=2;
    @Value("${JLPushMiddleServer}")
    String JLPushMiddleServer;
    @Autowired
    RedisService redisService;
    //----------------------------------设备上报信息（设备请求平台）-------------------------------------
    public  static final String registerRequest="registerRequest";//注册信息
    public  static final String heartbeatRequest="heartbeatRequest";//心跳信息 设备上下线
    public  static final String resultRequest="resultRequest";//任务结果上报
    public  static final String captureInfoRequest="captureInfoRequest";//抓拍信息（或密码门禁）事件推送上报--开门记录
    public  static final String noticeRequest="noticeRequest";//通知信息 设备告警


    //----------------------------------设备接收信息（平台请求设备）-------------------------------------
    public static final String personListRequest="personListRequest";
    public static final String passwordListRequest="passwordListRequest";
    public static final String IOControlRequest="IOControlRequest";
    public static final String faceSearchRequest="faceSearchRequest";
    public static final String restartRequest="restartRequest";
    public static final String deviceInfoRequest="deviceInfoRequest";
    public static final String timeSynchronizationRequest="timeSynchronizationRequest";


    //----------------------------------JL_session_key-------------------------------------
    public static final String JL_SESSION_FIX="JL_SESSION_";
    private JSONObject createJson(String requestName,String deviceUUID,String deviceMac){
        JSONObject json=new JSONObject();
        json.put("Name",requestName);
        json.put("UUID",deviceUUID);
        json.put("TimeStamp",System.currentTimeMillis()/1000);
        json.put("Session",getSession(deviceUUID,deviceMac));
        return json;
    }
    public String getSession(String deviceUUID,String deviceMac){
        String session=redisService.get(JL_SESSION_FIX+deviceUUID+"_"+deviceMac);
        return session;
    }
    public String setSession(String deviceUUID,String deviceMac,String session){
        redisService.set(JL_SESSION_FIX+deviceUUID+"_"+deviceMac,session);
        return session;
    }
    public String setSession(String deviceUUID,String deviceMac){
        Long timeStamp=Long.valueOf(System.currentTimeMillis()/1000);
        String respUuid = UUID.randomUUID().toString();
        respUuid = respUuid.replaceAll("-", "").toUpperCase();
        respUuid=respUuid.substring(0, 32);
        String session = respUuid + "_" + timeStamp;
        return setSession(deviceUUID, deviceMac,session);
    }
    public boolean getDeviceInfo(String deviceUUID,String deviceMac) {
        JSONObject param = createJson(deviceInfoRequest,deviceUUID,deviceMac);
        logger.info("【巨龙】getDeviceInfo param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】getDeviceInfo result:" + result);
        JSONObject temp= JSONObject.parseObject(result);
        return  temp.getIntValue("Code")==1;
    }

    public JSONObject timeSynchronizationRequest(String deviceUUID,String deviceMac) {
        JSONObject param = createJson(timeSynchronizationRequest,deviceUUID,deviceMac);
        String result = postRequest(param,deviceUUID,deviceMac);
        JSONObject temp= JSONObject.parseObject(result);
        return  temp;
    }
    /**
     *图片下发到人脸闸机(需要加锁)
     */
    public boolean  addPerson(String deviceUUID,String deviceMac, int personType, String personId, String personName,
                                                     String personPhoto) {
        JSONObject param = createJson(personListRequest,deviceUUID,deviceMac);
        JSONObject json=new JSONObject();
        json.put("Action", "addPerson");
        json.put("PersonType", personType);
        JSONObject personInfo=new JSONObject();
        personInfo.put("PersonId", personId);
        personInfo.put("PersonName", personName);
        personInfo.put("PersonPhoto", personPhoto);
        json.put("PersonInfo", personInfo);
        param.put("Data", json);
        logger.info("【巨龙】addPerson param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】addPerson result:" + result);
        JSONObject temp= JSONObject.parseObject(result);
        return  temp.getIntValue("Code")==1;
    }

    /**
     *获取人员信息
     */
    public String getPerson(String deviceUUID,String deviceMac, int personType, String personId) {
        JSONObject param = createJson(personListRequest,deviceUUID,deviceMac);
        JSONObject json = new JSONObject();
        json.put("Action", "getPerson");
        json.put("PersonType", personType);
        json.put("PersonId", personId);
        json.put("GetPhoto", 1);
        param.put("Data", json);
        logger.info("【巨龙】getPerson param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】getPerson result:" + result);
        return result;
    }

    /**
     *删除人员信息(批量)
     */
    public boolean delPersonBatch(String deviceUUID,String deviceMac, int personType){
        JSONObject param = createJson(personListRequest,deviceUUID,deviceMac);
        JSONObject json = new JSONObject();
        json.put("Action","deletePersonList");
        json.put("PersonType",personType);
        param.put("Data", json);
        logger.info("【巨龙】delPersonBatch param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】delPersonBatch result:"+result);
        JSONObject temp= JSONObject.parseObject(result);
        return  temp.getIntValue("Code")==1;
    }


    /**
     *删除人员信息
     */
    public boolean delPerson(String deviceUUID,String deviceMac, int personType,String personId){
        JSONObject param = createJson(personListRequest,deviceUUID,deviceMac);
        JSONObject json = new JSONObject();
        json.put("Action","deletePerson");
        json.put("PersonType",personType);
        json.put("PersonId", personId);
        param.put("Data", json);
        logger.info("【巨龙】delPerson param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】delPerson result:{}",result);
        JSONObject temp= JSONObject.parseObject(result);
        return  temp.getIntValue("Code")==1;
    }


    /**
     *获取人员名单
     */
    public void getPersonList(String deviceUUID,String deviceMac, int personType){
        JSONObject param = createJson(personListRequest,deviceUUID,deviceMac);
        JSONObject json = new JSONObject();
        json.put("Action","getPersonList");
        json.put("PersonType",personType);
        param.put("Data", json);
        logger.info("【巨龙】getPersonList param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】getPersonList result:"+result);

    }


    /**
     *人脸搜索名单库
     */
    public void personFaceSearch(String deviceUUID,String deviceMac,int personType,String image){
        JSONObject param = createJson(faceSearchRequest,deviceUUID,deviceMac);
        JSONObject json = new JSONObject();
        json.put("PersonType",personType);
        json.put("FacePicture",image);
        //是否获取照片
        json.put("GetPhoto",1);
        param.put("Data", json);
        logger.info("【巨龙】personFaceSearch param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】personFaceSearch result:"+result);
    }

    /**
     *人脸特征值获取
     */
    public void getFaceFeature(String deviceUUID,String deviceMac,String image){
        JSONObject param = createJson(faceSearchRequest,deviceUUID,deviceMac);
        JSONObject json = new JSONObject();
        json.put("FacePicture",image);
        param.put("Data", json);
        logger.info("【巨龙】getFaceFeature param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】getFaceFeature result:"+result);
    }


    public boolean addPwd(String deviceUUID,String deviceMac,List pwdList) {
        JSONObject param = createJson(passwordListRequest,deviceUUID,deviceMac);
        JSONObject data=new JSONObject();
        data.put("Action", "addPasswords");
        data.put("List", pwdList);
        param.put("Data", data);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】addPwd param:"+param);
        JSONObject temp= JSONObject.parseObject(result);
        logger.info("【巨龙】addPwd result:"+result);
        return  temp.getIntValue("Code")==1;
    }
    public boolean delPwd(String deviceUUID,String deviceMac,List pwdList) {
        JSONObject param = createJson(passwordListRequest,deviceUUID,deviceMac);
        JSONObject data=new JSONObject();
        data.put("Action", "deletePasswords");
        data.put("Type", 1);
        data.put("List", pwdList);
        param.put("Data", data);
        logger.info("【巨龙】delPwd param:"+param);
        String result = postRequest(param,deviceUUID,deviceMac);
        JSONObject temp= JSONObject.parseObject(result);
        logger.info("【巨龙】delPwd result:"+result);
        return  temp.getIntValue("Code")==1;
    }

    public boolean clearAllGroup(String deviceUUID,String deviceMac) {
        JSONObject param = createJson(restartRequest,deviceUUID,deviceMac);
        String result = postRequest(param,deviceUUID,deviceMac);
        JSONObject temp= JSONObject.parseObject(result);
        return  temp.getIntValue("Code")==1;
    }


    public boolean remoteOpenDoor(String deviceUUID,String deviceMac,String channelNo) {
        JSONObject param = createJson(IOControlRequest,deviceUUID,deviceMac);
        param.put("ChannelNo",channelNo);
        String result = postRequest(param,deviceUUID,deviceMac);
        logger.info("【巨龙】remoteOpenDoor param:"+param);
        JSONObject temp= JSONObject.parseObject(result);
        logger.info("【巨龙】remoteOpenDoor result:"+result);
        return  temp.getIntValue("Code")==1;
    }


    private String postRequest(JSONObject json, String deviceUUID,String deviceMac) {
        Map<String,String> headMap=new HashMap<>();
        headMap.put("UUID",deviceUUID);
        JSONObject result=new JSONObject();
        try {
            log.info("【巨龙】http post URL:{} 参数:{}",(JLPushMiddleServer+"/Request"),json.toString());
            result= HttpClientUtils.doPost(JLPushMiddleServer+"/Request", json,headMap);
            if(result!=null) {
                log.info("【巨龙】http post返回:{}", result.toString());
            }
        } catch (Exception e) { }
        return result.toJSONString();
    }



}

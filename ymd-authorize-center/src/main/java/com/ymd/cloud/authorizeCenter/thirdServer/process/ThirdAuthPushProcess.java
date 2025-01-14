package com.ymd.cloud.authorizeCenter.thirdServer.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.thirdServer.factory.HandlerFactory;
import com.ymd.cloud.authorizeCenter.thirdServer.handler.HandlerService;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThirdAuthPushProcess {
    @Autowired
    HandlerFactory handlerFactory;
    public void process(String topic,String msgBody){
        try {
            if(EmptyUtil.isNotEmpty(msgBody)){
                JSONObject notifyJson=JSONObject.parseObject(msgBody);
                notifyJson.put("vendor",(topic.split("/")[1]));
                update(notifyJson);
            }
        } catch (Exception e) {
            log.error("解析处理设备上报消息异常,", e);
        }
    }
    public void update(JSONObject notifyJson) {
        Long authId=notifyJson.getLong("authId");
        String addOrDelType=notifyJson.getString("addOrDelType");
        String job=notifyJson.getString("job");
        String cmd=notifyJson.getString("cmd");
        String vendor=notifyJson.getString("vendor");
        String serialNo=notifyJson.getString("serialNo");
        JSONArray data=notifyJson.getJSONArray("data");
        try {
            if(EmptyUtil.isEmpty(job)){
                job="2".equals(cmd)?"人脸授权":"3".equals(cmd)?"卡片授权":"4".equals(cmd)?"密码授权":"app授权";
                notifyJson.put("job",job);
            }
            HandlerService handlerService =handlerFactory.getInstall(vendor);
            if(EmptyUtil.isNotEmpty(serialNo)) {
                if ("2".equals(cmd)) {//添加人员和人脸照片
                    if ("a".equals(addOrDelType)) {
                        handlerService.savePersonsAndFaceImg(notifyJson, serialNo, authId);
                    } else if ("d".equals(addOrDelType)) {
                        handlerService.delPersonsAndFaceImg(notifyJson,serialNo, authId);
                    }
                } else if ("3".equals(cmd)) {//卡片
                    if ("a".equals(addOrDelType)) {
                        handlerService.updateCard(notifyJson, serialNo, authId);
                    } else if ("d".equals(addOrDelType)) {
                        handlerService.removeCard(notifyJson, serialNo, authId);
                    }
                } else if ("4".equals(cmd)) {//密码
                    if ("a".equals(addOrDelType)) {
                        handlerService.syncPwd(notifyJson, 1, serialNo, authId);
                    } else if ("d".equals(addOrDelType)) {
                        handlerService.syncPwd(notifyJson, 0, serialNo, authId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("第三方mq处理授权下发失败：{}",e);
        }
    }
}
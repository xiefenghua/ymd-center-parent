package com.ymd.cloud.authorizeCenter.thirdServer.process;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.thirdServer.service.ThirdServerService;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThirdAuthReturnReceiveProcess {
    @Autowired
    ThirdServerService thirdServerService;
    public void process(String topic,String msgBody){
        try {
            if(EmptyUtil.isNotEmpty(msgBody)){
                update(JSONObject.parseObject(msgBody));
            }
        } catch (Exception e) {
            log.error("解析处理设备上报消息异常,", e);
        }
    }
    public void update(JSONObject notifyJson) {
        try {
            Long authId = notifyJson.getLong("authId");
            if(EmptyUtil.isNotEmpty(authId)) {
                thirdServerService.updateSyncById(authId);
            }
        } catch (Exception e) {
            log.error("第三方mq处理授权结果同步失败：{}",e);
        }
    }
}
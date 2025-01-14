package com.ymd.cloud.authorizeCenter.thirdServer.process;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.thirdServer.service.ThirdServerService;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SyncThirdAllAuthMqProcess {
    @Autowired
    ThirdServerService thirdServerService;
    public void process(String topic,String msgBody){
        if(EmptyUtil.isNotEmpty(msgBody)) {
            JSONObject notifyJson = JSONObject.parseObject(msgBody);
            String mac=notifyJson.getString("mac");
            thirdServerService.syncThirdAllAuth(mac);
        }
    }
}
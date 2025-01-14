package com.ymd.cloud.authorizeCenter.thirdServer.process;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThirdDeviceReceiveProcess {
    public void process(String topic,String msgBody){
        try {
            if(EmptyUtil.isNotEmpty(msgBody)){
                JSONObject notifyJson=JSONObject.parseObject(msgBody);
                update(notifyJson);
            }
        } catch (Exception e) {
            log.error("解析处理设备上报消息异常,", e);
        }
    }
    private void update(JSONObject notifyJson) {
        notifyJson.put("mac",notifyJson.getString("serialNo"));
    }
}
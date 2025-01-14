package com.ymd.cloud.authorizeCenter.thirdServer.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.thirdServer.service.ThirdServerService;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThirdHeartReceiveProcess {
    @Autowired
    ThirdServerService thirdServerService;
    public void process(String topic,String msgBody){
        try {
            if(EmptyUtil.isNotEmpty(msgBody)){
                JSONObject notifyJson=JSONObject.parseObject(msgBody);
                update(notifyJson);
            }
        } catch (Exception e) {
            log.error("解析处理第三方心跳上报消息异常,", e);
        }
    }
    public void update(JSONObject notifyJson) {
        JSONArray dataList = notifyJson.getJSONArray("data");
        if(EmptyUtil.isNotEmpty(dataList)&&dataList.size()!=0) {
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject data = dataList.getJSONObject(i);
                String serialNo=data.getString("serialNo");
                boolean online=true;
                if(data.containsKey("onlineStatus")&&data.get("onlineStatus")!=null) {
                    int onlineStatus = data.getIntValue("onlineStatus");
                    online=onlineStatus==1;
                }
                thirdServerService.heartOnline(serialNo,online);
            }
        }
    }
}
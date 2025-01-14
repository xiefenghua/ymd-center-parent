package com.ymd.cloud.authorizeCenter.thirdServer.job.pushAuth;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.thirdServer.sever.WebSocketServerService;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xfh
 * @version 1.0
 * @description
 * @date 2022/9/20 13:09
 */
@Component
@Slf4j
public class RyPushMsgUtil {
    @Autowired
    WebSocketServerService webSocketServer;
    public void push(String serialNo,JSONObject notifyJson){
        if(EmptyUtil.isNotEmpty(serialNo)&&"all".equals(serialNo)){
            webSocketServer.sendAll(notifyJson.toString());
        }else {
            webSocketServer.sendMessage(notifyJson.toString(), serialNo);
        }
    }

}


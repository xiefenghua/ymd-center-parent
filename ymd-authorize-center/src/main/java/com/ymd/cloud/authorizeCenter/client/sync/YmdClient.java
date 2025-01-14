package com.ymd.cloud.authorizeCenter.client.sync;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.common.utils.HttpClientUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
@Data
@Slf4j
public class YmdClient {
    @Value("${ymd.mqtt2}")
    private String mqtt2;
    @Value("${ymd.gateAuthSync}")
    private String gateAuthSync;
    public String gateAuthSync(String gateMac, JSONObject data){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("gateMac",gateMac);
        paramMap.put("data",data.toString());
        String resStr=null;
        try {
            log.info("【请求参数为】:data{}  gateMac:{} ",data.toString(),gateMac);
            resStr = HttpClientUtils.doPost(gateAuthSync, paramMap);
            log.info("请求地址为==:{}",gateAuthSync);
            log.info("响应结果为:{}", resStr);
            if (resStr.equals("OK")) {
                log.info("通知成功。");
            }
        } catch (Exception e) {
            log.error("接口:{}", e.getMessage());
        }
        return resStr;
    }

    //	发布消息
    public String publishMessage(String topic,String qos,String msg) {
        //请求参数组装并发送
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("topic", topic);
        paramMap.put("qos", qos);
        paramMap.put("msg",msg);
        log.info("【消息推送主题】{} 【请求参数为】:{}",topic,paramMap);
        String resStr=null;
        try {
            resStr = HttpClientUtils.doPost(mqtt2, paramMap);
            log.info("mqtt发布消息响应结果为:{}", resStr);
            if (resStr.equals("OK")) {
                log.info("mqtt发布消息通知成功。");
            }
        } catch (Exception e) {
            log.error("mqtt发布消息通知接口:{}", e.getMessage());
        }
        return resStr;
    }
}

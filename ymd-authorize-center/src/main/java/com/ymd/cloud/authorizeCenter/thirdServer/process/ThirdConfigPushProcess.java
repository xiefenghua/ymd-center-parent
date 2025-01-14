package com.ymd.cloud.authorizeCenter.thirdServer.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.enums.VendorTypeEnum;
import com.ymd.cloud.authorizeCenter.thirdServer.factory.HandlerFactory;
import com.ymd.cloud.authorizeCenter.thirdServer.handler.HandlerService;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThirdConfigPushProcess {
    @Autowired
    HandlerFactory handlerFactory;
    public void process(String topic,String msgBody){
        try {
            if(EmptyUtil.isNotEmpty(msgBody)){
                JSONObject configJson= JSONObject.parseObject(msgBody);
                configJson.put("vendor",(topic.split("/")[1]));
                update(configJson);
            }
        } catch (Exception e) {
            log.error("解析处理设备上报消息异常,", e);
        }
    }

    public void update(JSONObject notifyJson) {
        Long authId=notifyJson.getLong("authId");
        String cmd =notifyJson.getString("cmd");
        String vendor=notifyJson.getString("vendor");
        String serialNo=notifyJson.getString("serialNo");
        JSONArray data = notifyJson.getJSONArray("data");
        try {
            String job="未知";
            HandlerService handlerService =handlerFactory.getInstall(vendor);
            if ("5".equals(cmd)) {//远程开门
                handlerService.remoteOpenDoor(notifyJson,serialNo);
                job="远程开门";
            } else if ("6".equals(cmd)) {//访客码
                handlerService.setVisitorCode(notifyJson,serialNo,authId);
                job="访客码";
            } else if ("99".equals(cmd)) {//开机重启 底库清空接口
                handlerService.clearAllGroup(notifyJson,serialNo);
                job="开机重启 底库清空接口";
            }else if ("-1".equals(cmd)) {
                handlerService.update(notifyJson,serialNo);
            }else if ("-2".equals(cmd)) {
                handlerService.updateTime(notifyJson,serialNo);
            }
            if(log.isDebugEnabled()) {
                log.debug("【业务】：{} 【操作】：{} 【产商】：{} serialNo：{} authId：{} 数据报文：{}"
                        , job
                        , "添加"
                        , VendorTypeEnum.getDescByVentor(vendor)
                        , serialNo
                        , authId
                        , data.toString());
            }
        } catch (Exception e) {
            log.error("第三方mq处理配置下发失败：{}",e);
        }
    }

}
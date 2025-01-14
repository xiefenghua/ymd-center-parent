package com.ymd.cloud.authorizeCenter.job.pushGateWay;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.client.sync.YmdClient;
import com.ymd.cloud.authorizeCenter.util.IncrementUtil;
import com.ymd.cloud.common.utils.AESUtil;
import com.ymd.cloud.common.utils.Base64Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushMsgGateWayDevice {
    @Autowired
    YmdClient ymdClient;
    //发送数据到设备端
    public String pushMsgEncryptedToDevice(String data, int cmd, String gateMac) {
        String res=null;
        try {
            JSONObject json = new JSONObject();
            long time = System.currentTimeMillis() / 1000;
            json.put("cmd", cmd);
            json.put("gateMac", gateMac);
            json.put("time", time);
            String key = IncrementUtil.getEncryptKey(time, gateMac);
            byte[] src = data.getBytes("UTF-8");
            //byte转换成十六进制
            String hexData = IncrementUtil.binaryToHexString(src);
            hexData = IncrementUtil.createZeroByString(hexData);
            //AES加密
            byte[] enData = AESUtil.encrypt(key, hexData);
            //base64加密(去掉空格)
            res = Base64Util.encode(enData).replace(" ", "");
            json.put("data", res);
            log.info("【推送到mq请求参数为】:data{}  cmd:{} gateMac:{} ", data, cmd, gateMac);
            String result = ymdClient.gateAuthSync(gateMac, json);
        } catch (Exception e) {
            log.error("物联网消息推送异常:{}", e);
            res="物联网消息推送异常:"+e.getMessage();
        }
        return res;
    }


}

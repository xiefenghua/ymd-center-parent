package com.ymd.cloud.authorizeCenter.thirdServer.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockMapper;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.model.lock.OpenLock;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ThirdOpenDoorReceiveProcess {
    @Resource
    LockMapper lockMapper;

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
    public void update(JSONObject notifyJson) {
        JSONArray dataList = notifyJson.getJSONArray("data");
        try {
            if(EmptyUtil.isNotEmpty(dataList)&&dataList.size()!=0) {
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject data = dataList.getJSONObject(i);
                    String serialNo=data.getString("serialNo");
                    Lock lock=lockMapper.selectByMac(serialNo);
                    OpenLock openLock = new OpenLock();
                    openLock.setAddress(data.getString("address"));
                    if(lock!=null) {
                        openLock.setLatitude(lock.getLatitude());
                        openLock.setLockId(lock.getLockid());
                        openLock.setLongtitude(lock.getLongtitude());
                    }
                    String time= DateUtil.getTime();
                    if(data.containsKey("time")&&EmptyUtil.isNotEmpty(data.getString("time"))) {
                        time=data.getString("time");
                    }
                    openLock.setTime(time);
                    openLock.setMsg("开锁成功");

                    String userAccount = data.getString("userAccount");
                    String openType = data.getString("openType");
                    if ("QR".equals(openType)) {
                        openLock.setRemark("二维码开门");
                        openLock.setUserName(userAccount);
                        openLock.setType("1");
                    } else if ("PassWord".equals(openType)) {
                        openLock.setRemark("密码开门");
                        openLock.setUserName(userAccount);
                        openLock.setType("11");
                    }else if ("Face".equals(openType)) {
                        openLock.setRemark("人脸开门");
                        openLock.setUserName(userAccount);
                        openLock.setType("31");
                    } else {
                        openLock.setRemark("卡片开门");
                        openLock.setUserName(userAccount);
                        openLock.setType("2");
                    }
                    lockMapper.insertOpenLock(openLock);
                }
            }
        } catch (Exception e) {
            log.error("第三方mq处理远程开门失败：{}",e);
        }
    }

}
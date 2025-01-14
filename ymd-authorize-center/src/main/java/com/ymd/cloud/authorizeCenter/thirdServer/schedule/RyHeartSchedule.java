package com.ymd.cloud.authorizeCenter.thirdServer.schedule;

import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.authorizeCenter.mapper.lock.LockMapper;
import com.ymd.cloud.authorizeCenter.model.lock.Lock;
import com.ymd.cloud.authorizeCenter.thirdServer.service.ThirdServerService;
import com.ymd.cloud.authorizeCenter.thirdServer.sever.WebSocketServerService;
import com.ymd.cloud.common.enumsSupport.EventCenterConstant;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;

@Configuration
@EnableScheduling
@Slf4j
public class RyHeartSchedule {
    @Autowired
    RedisService redisService;
    @Autowired
    WebSocketServerService webSocketServer;
    @Autowired
    ThirdServerService thirdServerService;
    @Resource
    LockMapper lockMapper;
    @Scheduled(fixedDelay=65000)
    private void excute() {
        try {
            Map<String, String> heartMacMap = redisService.hgetAll(WebSocketServerService.heartKey);
            if(EmptyUtil.isNotEmpty(heartMacMap)) {
                Iterator<Map.Entry<String, String>> it = heartMacMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    String mac = entry.getKey();
                    String value = entry.getValue().split("_")[0];
                    String uploadTimeStamp = entry.getValue().split("_")[1];
                    if (WebSocketServerService.PONG.equals(value)) {
                        log.debug("【心跳检测ping】>>>>>>>>>>>>mac:{} pong,time:{}", mac, uploadTimeStamp);
                        String heartC = redisService.hget(WebSocketServerService.connectCountKey, mac,String.class);
                        int count = EmptyUtil.isNotEmpty(heartC) ? Integer.valueOf(heartC) : 1;
                        int maxCount = EventCenterConstant.retryCount;
                        log.debug("【心跳检测ping】>>>>>>>>>>>>mac:{},当前重连次数:{},数据字典最大:{}", mac, count, maxCount);
                        if (count > maxCount) {//离线
                            log.info("【心跳检测ping】>>>>>>>>>>>>mac:{},离线状态已更新", mac);
                            WebSocketServerService socketServerService = webSocketServer.webSocketMap.get(mac);
                            if (socketServerService != null) {//锐颖设备
                                if (socketServerService.getSession().isOpen()) {
                                    socketServerService.onClose(socketServerService.getSession());
                                }
                                log.info("【心跳检测ping】>>>>>>>>>>>>mac:{},session长链接是否关闭:{}", mac, !(socketServerService.getSession().isOpen()));
                            } else {//其他设备
                                Lock lock=lockMapper.selectByMac(mac);
                                if (EmptyUtil.isNotEmpty(lock) && lock.getOnlineStatus()) {
                                    thirdServerService.heartOnline(mac, false);
                                }
                            }
                        } else {
                            long offlineTimeL;
                            Long uploadTime = Long.parseLong(uploadTimeStamp);//最后一次上报时间
                            Long currTime = System.currentTimeMillis();
                            String offlineTime = "30";
                            if (EmptyUtil.isNotEmpty(offlineTime)) {
                                offlineTimeL = Long.parseLong(offlineTime);
                            } else {
                                offlineTimeL = 30;//间隔时间
                            }
                            boolean diffFlag = currTime.longValue() - uploadTime.longValue() >= offlineTimeL * 1000;
                            log.debug("【心跳检测ping】>>>>>>>>>>>>mac:{},uploadTime:{},与当前时间差:{},系统配置时间间隔:{},是否达到间隔:{}", mac
                                    , uploadTime, (currTime.longValue() - uploadTime.longValue()), offlineTimeL * 1000, diffFlag);
                            if (diffFlag) {
                                webSocketServer.sendMessage(WebSocketServerService.PONG, mac);
                                count++;
                                redisService.hset(WebSocketServerService.connectCountKey, mac, count + "");
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("【心跳检测ping】>>>error:{}",e);
        }
    }
}
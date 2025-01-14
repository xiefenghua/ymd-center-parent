package com.ymd.cloud.eventCenter.scheduler;

import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.service.EventCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class EventCenterMqRetryHeartSchedule {
    @Autowired
    RedisService redisService;
    @Autowired
    private EventCenterService eventCenterService;
    @Scheduled(fixedDelay=60000)
    private void excute() {
        try {
            String time=redisService.get(Constants.MQ_QUEUE_TASK_REDIS_KEY+"_uploadTime");
            Long uploadTime= EmptyUtil.isNotEmpty(time)?Long.valueOf(time):0;
            long offTime = 15*60*1000;
            Long currTime = System.currentTimeMillis();
            boolean diffFlag = currTime.longValue() - uploadTime.longValue() >= offTime ;
            if (diffFlag) {
                eventCenterService.updatePushStatus();
                eventCenterService.batchSendDelayMsgPolicy(true,true);
            }
        }catch (Exception e){
            log.error("DelayTaskHeartSchedule-error",e);
        }
    }




}

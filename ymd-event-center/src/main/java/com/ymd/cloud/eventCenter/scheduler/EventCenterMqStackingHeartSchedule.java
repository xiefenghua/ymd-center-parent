package com.ymd.cloud.eventCenter.scheduler;

import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.mq.consumer.BasicConsume;
import com.ymd.cloud.eventCenter.service.EventCenterLogService;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class EventCenterMqStackingHeartSchedule {
    @Autowired
    RedisService redisService;
    @Autowired
    BasicConsume basicConsume;
    @Autowired
    private RabbitMqTopicQueueService eventCenterTopicQueueService;
    @Autowired
    EventCenterLogService eventCenterLogService;
    /**
     * 实时监控堆积情况,和处理能力
     */
    @Scheduled(fixedDelay=600000)
    private void excute() {
        try {
            //如果三十分钟没活跃的话，检测是否消息堆积
            String time=redisService.get(Constants.MQ_QUEUE_TASK_REDIS_KEY+"_uploadTime");
            Long uploadTime= EmptyUtil.isNotEmpty(time)?Long.valueOf(time):0;
            long offTime = 30*60*1000;
            Long currTime = System.currentTimeMillis();
            boolean diffFlag = currTime.longValue() - uploadTime.longValue() >= offTime ;
            if (diffFlag) {
                List<String> queueList= eventCenterTopicQueueService.queueList();
                for (String queue:queueList) {
                    basicConsume.basicConsume(queue,"600秒实时监控堆积");
                }
                eventCenterLogService.saveEventLog(System.currentTimeMillis()+"","600秒实时监控堆积",
                        String.join(",",queueList), ErrorCodeEnum.SUCCESS.code(),ErrorCodeEnum.SUCCESS.msg()
                        , 0l, null);
            }
        }catch (Exception e){
            log.error("DelayTaskHeartSchedule-error",e);
        }
    }
}

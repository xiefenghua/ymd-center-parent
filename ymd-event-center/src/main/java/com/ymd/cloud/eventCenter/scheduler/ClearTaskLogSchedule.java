package com.ymd.cloud.eventCenter.scheduler;

import com.ymd.cloud.eventCenter.mapper.EventCenterLogMapper;
import com.ymd.cloud.eventCenter.mapper.EventCenterTopicTaskRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

@Slf4j
@Configuration
@EnableScheduling
public class ClearTaskLogSchedule {
    @Resource
    EventCenterLogMapper eventCenterLogMapper;
    @Resource
    EventCenterTopicTaskRecordMapper eventCenterTopicTaskRecordMapper;
    /**
     * 每天凌晨2点执行一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void delBefore7Day() {
        try {
            eventCenterLogMapper.delBefore1Month();
            eventCenterTopicTaskRecordMapper.delTaskBefore1Month();
        }catch (Exception e){}
    }
}

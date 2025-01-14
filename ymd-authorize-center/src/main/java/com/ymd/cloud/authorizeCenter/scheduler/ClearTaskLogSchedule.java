package com.ymd.cloud.authorizeCenter.scheduler;

import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterAuthorizeLogMapper;
import com.ymd.cloud.authorizeCenter.mapper.third.ThirdServerAuthReturnLogMapper;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskNotesService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskResultService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

@Slf4j
@Configuration
@EnableScheduling
public class ClearTaskLogSchedule {
    @Autowired
    AuthorizeCenterTaskResultService authorizeCenterTaskResultService;
    @Autowired
    AuthorizeCenterTaskNotesService authorizeCenterTaskNotesService;
    @Autowired
    AuthorizeCenterTaskService authorizeCenterTaskService;
    @Resource
    ThirdServerAuthReturnLogMapper thirdServerAuthReturnLogMapper;
    @Resource
    AuthorizeCenterAuthorizeLogMapper authorizeCenterAuthorizeLogMapper;
    /**
     * 每天凌晨1点执行一次
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void delBefore7Day() {
        try {
            authorizeCenterTaskService.delTaskBefore1Month();
            authorizeCenterTaskResultService.delTaskResultBefore1Month();
            authorizeCenterTaskNotesService.delTaskNotesBefore1Month();
            thirdServerAuthReturnLogMapper.delBefore1Month();
            authorizeCenterAuthorizeLogMapper.delBefore6Month();
        }catch (Exception e){}
    }
}

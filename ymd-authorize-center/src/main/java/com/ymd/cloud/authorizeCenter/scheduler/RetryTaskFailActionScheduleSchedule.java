package com.ymd.cloud.authorizeCenter.scheduler;

import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskNotesService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskResultService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class RetryTaskFailActionScheduleSchedule {
    @Autowired
    AuthorizeCenterTaskResultService authorizeCenterTaskResultService;
    @Autowired
    AuthorizeCenterTaskNotesService authorizeCenterTaskNotesService;
    @Autowired
    AuthorizeCenterTaskService authorizeCenterTaskService;
    @Autowired
    RedisService redisService;
    @Autowired
    AuthorizeService authorizeService;
    @Scheduled(fixedDelay=300000)
    private void retryTaskFailAction() {
        try {
            authorizeService.processTaskByNoFinish();
            List<AuthorizeCenterTask> tblSyncTasks = authorizeCenterTaskService.selectTaskAuthCountNull();
            for (AuthorizeCenterTask authorizeCenterTask : tblSyncTasks) {
                authorizeCenterTask = authorizeCenterTaskResultService.getTaskResultCountInfoLog(authorizeCenterTask);
                authorizeCenterTaskService.updateAuthCountByTaskNo(authorizeCenterTask);
            }
        }catch (Exception e){}
    }
}
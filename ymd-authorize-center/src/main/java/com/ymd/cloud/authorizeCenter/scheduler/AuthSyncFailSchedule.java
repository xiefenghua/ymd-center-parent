package com.ymd.cloud.authorizeCenter.scheduler;

import com.ymd.cloud.authorizeCenter.service.AuthorizeSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class AuthSyncFailSchedule {
    @Autowired
    AuthorizeSyncService authorizeSyncService;
    @Scheduled(fixedDelay=900000)
    private void excute() {
        try {
            authorizeSyncService.retryPushSyncAllFailAuth();
        }catch (Exception e){}
    }
}
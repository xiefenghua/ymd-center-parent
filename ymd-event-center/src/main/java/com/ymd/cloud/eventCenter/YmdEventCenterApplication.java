package com.ymd.cloud.eventCenter;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan(basePackages = {"com.ymd.cloud.eventCenter.mapper"})
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages={"com.ymd.cloud.*"})
@Slf4j
@EnableAsync
public class YmdEventCenterApplication {
    private static String dbUrl;
    private static String active;
    private static String port;
    public static void main(String[] args) {
        SpringApplication.run(YmdEventCenterApplication.class, args);
        log.info("事件中心 启动成功！！>>>端口:[{}] 环境：[{}] dbUrl:{} " , port,active, dbUrl);
    }
    @Value("${server.port}")
    public void setPort(String port) {
        YmdEventCenterApplication.port = port;
    }
    @Value("${spring.datasource.url}")
    public void setDbUrl(String dbUrl) {
        YmdEventCenterApplication.dbUrl = dbUrl;
    }
    @Value("${spring.profiles.active}")
    public void setActive(String active) {
        YmdEventCenterApplication.active = active;
    }
}

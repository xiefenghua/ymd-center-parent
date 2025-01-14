package com.ymd.cloud.authorizeCenter;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan(basePackages = {"com.ymd.cloud.authorizeCenter.mapper","com.ymd.cloud.authorizeCenter.mapper.*"})
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages={"com.ymd.cloud.*"})
@Slf4j
@EnableAsync
public class YmdAuthorizeCenterApplication {
    private static String dbUrl;
    private static String active;
    private static String port;
    public static void main(String[] args) {
        SpringApplication.run(YmdAuthorizeCenterApplication.class, args);
        log.info("授权中心 启动成功！！>>>端口:[{}] 环境：[{}] dbUrl:{} " , port,active, dbUrl);
    }
    @Value("${server.port}")
    public void setPort(String port) {
        YmdAuthorizeCenterApplication.port = port;
    }
    @Value("${spring.datasource.url}")
    public void setDbUrl(String dbUrl) {
        YmdAuthorizeCenterApplication.dbUrl = dbUrl;
    }
    @Value("${spring.profiles.active}")
    public void setActive(String active) {
        YmdAuthorizeCenterApplication.active = active;
    }
}

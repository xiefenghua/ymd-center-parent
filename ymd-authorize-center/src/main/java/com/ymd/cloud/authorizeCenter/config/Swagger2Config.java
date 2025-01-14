package com.ymd.cloud.authorizeCenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2 配置文件
 * 
 */
@Configuration
@EnableSwagger2
public class Swagger2Config
{
    
    @Bean
    public Docket installDocket()
    {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("ymd-authorize-center")
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.ymd.cloud.authorizeCenter.web"))
            .paths(PathSelectors.any())
            .build();
    }
    
    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder().title("YMD-AUTHORIZE-CENTER-API")
            .description("©2020 Copyright. Powered By YMD.")
            .license("Apache License Version 2.0")
            .version("2.0")
            .build();
    }
    
}

package com.en_chu.calculator_api_spring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("財務計算機 API (Financial Calculator API)")
                        .version("v1.0.0")
                        .description("這是一個開源的財務計算機後端 API，提供個人理財檔案管理與複利計算等功能。")
                        .termsOfService("https://www.en-chu.com/terms")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org"))
                );
    }
}
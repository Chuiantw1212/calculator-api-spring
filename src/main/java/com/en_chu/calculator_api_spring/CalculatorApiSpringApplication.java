package com.en_chu.calculator_api_spring;

import org.mybatis.spring.annotation.MapperScan; // 記得 import
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.en_chu.calculator_api_spring.config.CorsConfig;

@SpringBootApplication
@EnableConfigurationProperties(CorsConfig.class) // 👈 強制啟用屬性讀取
@MapperScan("com.en_chu.calculator_api_spring.mapper")
public class CalculatorApiSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalculatorApiSpringApplication.class, args);
	}
}
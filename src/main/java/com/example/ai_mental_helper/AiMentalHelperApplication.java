package com.example.ai_mental_helper;

import org.mybatis.spring.annotation.MapperScan; // 确保导入了这个包
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@MapperScan("com.example.ai_mental_helper.dao") // 必须加在这里！主跑程序才能扫描到 DAO！
@SpringBootApplication
public class AiMentalHelperApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiMentalHelperApplication.class, args);
    }
}
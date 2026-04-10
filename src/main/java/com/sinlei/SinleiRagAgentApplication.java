package com.sinlei;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sinlei.rag.mapper")
public class SinleiRagAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SinleiRagAgentApplication.class, args);
    }

}

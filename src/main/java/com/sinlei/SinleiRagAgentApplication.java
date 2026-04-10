package com.sinlei;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口。扫描 {@code com.sinlei.mapper} 下全部 MyBatis Mapper（含 RAG 与短视频表）。
 */
@SpringBootApplication
@MapperScan({"com.sinlei.mapper"})
public class SinleiRagAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SinleiRagAgentApplication.class, args);
    }

}

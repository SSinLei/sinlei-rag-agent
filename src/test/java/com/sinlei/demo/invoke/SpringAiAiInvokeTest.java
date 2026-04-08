package com.sinlei.demo.invoke;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SpringAiAiInvokeTest {

    @Resource
    private SpringAiAiInvoke springAiAiInvoke;
    @Test
    void run() {
//        springAiAiInvoke.run(null);
    }
}
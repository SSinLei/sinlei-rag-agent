package com.sinlei.controller;

import com.sinlei.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * 提供系统健康状态的接口，用于：
 * 1. 负载均衡器探测服务是否存活
 * 2. 监控系统检查服务状态
 * 3. 服务启动完成后确认服务可用
 *
 * 使用说明：
 * - 前端/客户端可以通过调用 /health 接口判断服务是否正常运行
 * - 返回的 status 为 "UP" 表示服务正常
 */
@RestController
public class HealthController {

    /**
     * 健康检查接口
     *
     * 接口地址：GET /health
     *
     * 响应示例：
     * {
     *     "code": 200,
     *     "message": "success",
     *     "data": {
     *         "status": "UP",
     *         "timestamp": "2026-04-08T12:00:00"
     *     }
     * }
     *
     * @return 包含健康状态的 Result 对象
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        // 创建返回数据
        Map<String, Object> data = new HashMap<>();

        // 服务状态：UP 表示正常运行
        data.put("status", "UP");

        // 当前时间戳，便于判断服务是否最新
        data.put("timestamp", LocalDateTime.now());

        // 返回成功结果
        return Result.success(data);
    }
}

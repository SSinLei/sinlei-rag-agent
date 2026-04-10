package com.sinlei.controller;

import com.sinlei.common.Result;
import com.sinlei.common.dto.ChatRequest;
import com.sinlei.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 智能问答控制器
 * 提供AI对话服务接口，支持非流式和流式输出
 */
@RestController
@RequestMapping("/rag/chat")
@RequiredArgsConstructor
@Tag(name = "智能问答", description = "AI智能体问答接口")
public class AiChatController {

    /**
     * AI问答服务
     */
    private final AiChatService aiChatService;

    /**
     * 发送消息（非流式）
     * 接收用户消息，返回完整的AI回答
     *
     * @param request 问答请求，包含用户ID、会话ID、消息内容等
     * @return 操作结果，包含AI回答内容
     */
    @PostMapping
    @Operation(summary = "发送消息（非流式）", description = "接收用户消息，返回完整的AI回答")
    public Result<String> chat(
            @Parameter(description = "问答请求参数") @RequestBody ChatRequest request) {
        // 参数校验
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            return Result.error("userId不能为空");
        }
        if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
            return Result.error("conversationId不能为空");
        }
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            return Result.error("message不能为空");
        }

        // 调用服务处理问答
        String response = aiChatService.chat(request);
        return Result.success(response);
    }

    /**
     * 发送消息（流式）
     * 接收用户消息，以流式方式返回AI回答
     *
     * @param request 问答请求
     * @return 流式输出的回答内容（Server-Sent Events格式）
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "发送消息（流式）", description = "以流式方式返回AI回答（Server-Sent Events）")
    public Flux<String> chatStream(
            @Parameter(description = "问答请求参数") @RequestBody ChatRequest request) {
        // 参数校验
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            return Flux.error(new IllegalArgumentException("userId不能为空"));
        }
        if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
            return Flux.error(new IllegalArgumentException("conversationId不能为空"));
        }
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            return Flux.error(new IllegalArgumentException("message不能为空"));
        }

        // 调用服务处理流式问答
        return aiChatService.chatStream(request);
    }
}

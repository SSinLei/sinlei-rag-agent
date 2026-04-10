package com.sinlei.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 问答请求DTO
 * 用于接收前端发送的聊天请求参数
 */
@Data
@Schema(description = "问答请求")
public class ChatRequest {

    /**
     * 用户ID
     * 必填参数，用于标识用户身份，并进行数据隔离
     */
    @Schema(description = "用户ID", required = true, example = "USER001")
    private String userId;

    /**
     * 会话ID
     * 必填参数，用于标识会话，支持多轮对话上下文
     * 同一conversationId的消息会被串联成完整对话
     * 建议使用UUID或唯一字符串
     */
    @Schema(description = "会话ID，用于多轮对话", required = true, example = "CONV-UUID-001")
    private String conversationId;

    /**
     * 用户消息
     * 必填参数，用户发送的实际消息内容
     */
    @Schema(description = "用户消息", required = true, example = "这个保险产品的保障范围是什么？")
    private String message;

    /**
     * 险种编码
     * 选填参数，指定后会在对应产品的知识库中搜索
     * 不指定则会在用户所有知识库中搜索
     */
    @Schema(description = "险种编码（可选，指定则只在相关知识库中搜索）", example = "00520")
    private String productCode;
}

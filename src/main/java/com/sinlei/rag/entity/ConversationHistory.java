package com.sinlei.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话历史表实体类
 * 用于存储用户与AI的对话历史，支持多轮对话上下文
 *
 * 对应数据库表: conversation_history
 */
@Data
@TableName(value = "conversation_history", autoResultMap = true)
@Schema(description = "对话历史表实体")
public class ConversationHistory {

    /**
     * 主键ID
     * 使用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户ID
     * 用于标识对话所属用户，确保对话数据隔离
     */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 对话会话ID
     * 用于关联同一轮对话的所有消息，支持多轮对话场景
     * 同一个conversationId的消息会被串联成完整的对话上下文
     */
    @TableField("conversation_id")
    @Schema(description = "对话会话ID，用于多轮对话")
    private String conversationId;

    /**
     * 角色
     * 标识消息发送者身份：user（用户）、assistant（AI）、system（系统）
     */
    @TableField("role")
    @Schema(description = "角色（user/assistant/system）")
    private String role;

    /**
     * 对话内容
     * 消息的实际文本内容
     */
    @TableField("content")
    @Schema(description = "对话内容")
    private String content;

    /**
     * 关联险种编码
     * 本轮对话关联的保险产品编码，可用于针对性知识库检索
     */
    @TableField("product_code")
    @Schema(description = "关联险种编码（可选）")
    private String productCode;

    /**
     * 创建时间
     * 记录消息发送时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}

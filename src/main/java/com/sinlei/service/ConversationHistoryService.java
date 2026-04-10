package com.sinlei.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinlei.common.entity.ConversationHistory;
import com.sinlei.mapper.ConversationHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 对话历史服务
 * 负责存储和检索用户的对话历史记录
 *
 * 功能说明：
 * 1. 保存用户与AI的对话消息
 * 2. 检索指定会话的对话历史
 * 3. 构建上下文提示词用于多轮对话
 * 4. 支持按用户ID和会话ID进行数据隔离
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationHistoryService {

    /**
     * 对话历史Mapper
     * 用于数据库操作
     */
    private final ConversationHistoryMapper conversationHistoryMapper;

    /**
     * 系统提示词模板
     * 用于构建包含会话信息的提示词
     */
    private static final String SYSTEM_PROMPT_TEMPLATE = String.join("\n",
        "您是一位专业的保险知识助手，专门帮助用户解答关于保险的问题。",
        "",
        "## 您的能力：",
        "1. 可以回答用户关于保险产品的相关问题",
        "2. 可以根据用户上传的知识库文档（保险条款）回答问题",
        "3. 如果需要查询保单信息，可以使用工具查询",
        "",
        "## 回答要求：",
        "1. 请基于提供的上下文信息进行回答",
        "2. 如果知识库中没有相关信息，请明确告知用户",
        "3. 回答要准确、清晰、易懂",
        "4. 如果用户询问的内容涉及具体条款，请引用相关章节",
        "",
        "## 当前会话信息：",
        "- 用户ID: %s",
        "- 会话ID: %s",
        "- 关联险种: %s"
    );

    /**
     * 保存对话消息
     * 将用户或AI的消息保存到数据库
     *
     * @param userId 用户ID（用于数据隔离）
     * @param conversationId 会话ID（用于关联同一轮对话）
     * @param role 角色（user/assistant）
     * @param content 消息内容
     * @param productCode 关联的险种编码
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(String userId, String conversationId, String role, String content, String productCode) {
        log.debug("保存对话消息: userId={}, conversationId={}, role={}", userId, conversationId, role);

        ConversationHistory history = new ConversationHistory();
        history.setUserId(userId);
        history.setConversationId(conversationId);
        history.setRole(role);
        history.setContent(content);
        history.setProductCode(productCode);
        conversationHistoryMapper.insert(history);
    }

    /**
     * 获取对话历史
     * 检索指定会话的历史消息
     *
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @param limit 返回最近N条消息
     * @return 对话历史列表（按时间正序）
     */
    public List<ConversationHistory> getHistory(String userId, String conversationId, int limit) {
        log.debug("获取对话历史: userId={}, conversationId={}, limit={}", userId, conversationId, limit);

        LambdaQueryWrapper<ConversationHistory> wrapper = new LambdaQueryWrapper<ConversationHistory>()
            .eq(ConversationHistory::getUserId, userId)
            .eq(ConversationHistory::getConversationId, conversationId)
            .orderByAsc(ConversationHistory::getCreatedAt)
            .last("LIMIT " + limit);

        return conversationHistoryMapper.selectList(wrapper);
    }

    /**
     * 构建上下文提示词
     * 将对话历史格式化为可用于AI回答的提示词
     *
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @param productCode 险种编码
     * @return 格式化的上下文提示词
     */
    public String buildContextPrompt(String userId, String conversationId, String productCode) {
        // 获取最近20条对话历史
        List<ConversationHistory> history = getHistory(userId, conversationId, 20);

        // 构建对话历史字符串
        StringBuilder contextBuilder = new StringBuilder();
        for (ConversationHistory msg : history) {
            contextBuilder.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }

        // 格式化为完整提示词
        return SYSTEM_PROMPT_TEMPLATE.formatted(userId, conversationId, productCode != null ? productCode : "未指定");
    }
}

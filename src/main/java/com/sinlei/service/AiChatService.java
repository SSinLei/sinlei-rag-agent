package com.sinlei.service;

import com.sinlei.common.dto.ChatRequest;
import com.sinlei.common.entity.ConversationHistory;
import com.sinlei.common.entity.KnowledgeChunks;
import com.sinlei.mcp.tool.PolicyQueryTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.*;

/**
 * AI问答服务
 * 负责处理用户的智能问答请求
 *
 * 功能说明：
 * 1. 根据用户查询在知识库中进行向量检索（RAG）
 * 2. 整合对话历史实现多轮对话
 * 3. 调用AI大模型生成回答
 * 4. 支持保单相关信息的Function Calling查询
 * 5. 支持流式输出和非流式输出
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    /**
     * AI大模型
     * 用于生成对话回答
     */
    private final ChatModel chatModel;

    /**
     * 对话历史服务
     * 负责存储和检索对话历史
     */
    private final ConversationHistoryService conversationHistoryService;

    /**
     * 向量检索服务
     * 用于在知识库中检索相关内容
     */
    private final VectorSearchService vectorSearchService;

    /**
     * 保单查询工具
     * 用于Function Calling查询保单信息
     */
    private final PolicyQueryTool policyQueryTool;

    /**
     * 系统提示词
     * 定义AI助手的角色定位和能力范围
     * 包含RAG知识库问答和保单查询工具的使用说明
     */
    private static final String SYSTEM_PROMPT = String.join("\n",
        "您是一位专业的保险知识助手，专门帮助用户解答关于保险的问题。",
        "",
        "## 您的能力：",
        "1. 可以回答用户关于保险产品的相关问题",
        "2. 可以根据用户上传的知识库文档（保险条款）回答问题",
        "3. 如果需要查询保单信息，可以使用工具查询保单列表、理赔记录、缴费状态、现金价值等",
        "",
        "## 可用工具：",
        "- get_policy_list: 根据用户ID查询保单列表",
        "- get_policy_detail: 根据保单号查询保单详情",
        "- get_claim_records: 根据保单号查询理赔记录",
        "- get_payment_status: 根据保单号查询缴费状态",
        "- get_policy_fund: 根据保单号查询现金价值",
        "",
        "## 回答要求：",
        "1. 请基于提供的上下文信息进行回答",
        "2. 如果知识库中没有相关信息，请明确告知用户",
        "3. 回答要准确、清晰、易懂",
        "4. 如果用户询问的内容涉及具体条款，请引用相关章节标题",
        "5. 如果用户询问保单相关信息，请主动调用相关工具查询",
        "",
        "## 重要：",
        "- 请始终保持专业、友好的服务态度",
        "- 对于不确定的信息，请明确告知用户需要进一步确认",
        "- 用户ID: {userId}"
    );

    /**
     * RAG上下文模板
     * 用于将检索到的知识库内容格式化为提示词
     */
    private static final String RAG_CONTEXT_TEMPLATE = String.join("\n",
        "## 知识库上下文信息：",
        "%s",
        "",
        "请根据以上知识库内容回答用户的问题。如果知识库中没有相关信息，请明确告知用户。"
    );

    /**
     * 处理用户问答请求（非流式）
     *
     * 处理流程：
     * 1. 在知识库中进行向量检索，获取相关文本块
     * 2. 构建RAG上下文提示词
     * 3. 保存用户消息到对话历史
     * 4. 获取历史对话记录
     * 5. 整合系统提示词、对话历史和RAG上下文
     * 6. 调用AI大模型生成回答
     * 7. 保存AI回答到对话历史
     * 8. 返回回答内容
     *
     * @param request 问答请求，包含用户ID、会话ID、消息内容等
     * @return AI生成的回答内容
     */
    @Transactional(rollbackFor = Exception.class)
    public String chat(ChatRequest request) {
        log.info("处理问答请求: userId={}, conversationId={}", request.getUserId(), request.getConversationId());

        // 1. 向量检索相关知识块
        List<KnowledgeChunks> relevantChunks = vectorSearchService.searchSimilar(
            request.getUserId(),
            request.getProductCode(),
            request.getMessage(),
            5
        );
        log.debug("检索到 {} 个相关知识块", relevantChunks.size());

        // 2. 构建RAG上下文
        String context = buildContextFromChunks(relevantChunks);
        String ragPrompt = RAG_CONTEXT_TEMPLATE.formatted(context);

        // 3. 保存用户消息
        conversationHistoryService.saveMessage(
            request.getUserId(),
            request.getConversationId(),
            "user",
            request.getMessage(),
            request.getProductCode()
        );

        // 4. 获取对话历史（最近10条）
        List<ConversationHistory> history = conversationHistoryService.getHistory(
            request.getUserId(),
            request.getConversationId(),
            10
        );

        // 5. 构建对话历史字符串
        StringBuilder conversationHistory = new StringBuilder();
        for (ConversationHistory msg : history) {
            conversationHistory.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }

        // 6. 整合完整提示词
        String systemPrompt = SYSTEM_PROMPT.replace("{userId}", request.getUserId());
        String fullSystemPrompt = systemPrompt + "\n\n" + conversationHistory.toString() + "\n" + ragPrompt;
        log.debug("完整提示词长度: {}", fullSystemPrompt.length());

        // 7. 调用AI生成回答
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultTools(policyQueryTool)
            .build();

        String response = chatClient.prompt()
            .system(fullSystemPrompt)
            .user(request.getMessage())
            .call()
            .content();

        log.info("AI回答生成完成");

        // 8. 保存AI回答
        conversationHistoryService.saveMessage(
            request.getUserId(),
            request.getConversationId(),
            "assistant",
            response,
            request.getProductCode()
        );

        return response;
    }

    /**
     * 处理用户问答请求（流式输出）
     *
     * @param request 问答请求
     * @return 流式输出的回答内容（Flux类型）
     */
    @Transactional(rollbackFor = Exception.class)
    public Flux<String> chatStream(ChatRequest request) {
        log.info("处理流式问答请求: userId={}, conversationId={}", request.getUserId(), request.getConversationId());

        // 1. 向量检索相关知识块
        List<KnowledgeChunks> relevantChunks = vectorSearchService.searchSimilar(
            request.getUserId(),
            request.getProductCode(),
            request.getMessage(),
            5
        );

        // 2. 构建RAG上下文
        String context = buildContextFromChunks(relevantChunks);
        String ragPrompt = RAG_CONTEXT_TEMPLATE.formatted(context);

        // 3. 保存用户消息
        conversationHistoryService.saveMessage(
            request.getUserId(),
            request.getConversationId(),
            "user",
            request.getMessage(),
            request.getProductCode()
        );

        // 4. 获取对话历史
        List<ConversationHistory> history = conversationHistoryService.getHistory(
            request.getUserId(),
            request.getConversationId(),
            10
        );

        // 5. 构建对话历史
        StringBuilder conversationHistory = new StringBuilder();
        for (ConversationHistory msg : history) {
            conversationHistory.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }

        // 6. 整合提示词
        String systemPrompt = SYSTEM_PROMPT.replace("{userId}", request.getUserId());
        String fullSystemPrompt = systemPrompt + "\n\n" + conversationHistory.toString() + "\n" + ragPrompt;

        // 7. 创建ChatClient并流式返回
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultTools(policyQueryTool)
            .build();

        Flux<String> responseStream = chatClient.prompt()
            .system(fullSystemPrompt)
            .user(request.getMessage())
            .stream()
            .content()
            .doOnComplete(() -> {
                log.info("流式回答完成");
            });

        return responseStream;
    }

    /**
     * 从知识块构建上下文文本
     * 将检索到的文本块格式化为可读上下文
     *
     * @param chunks 知识块列表
     * @return 格式化的上下文文本
     */
    private String buildContextFromChunks(List<KnowledgeChunks> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return "未找到相关知识库内容";
        }

        StringBuilder context = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            KnowledgeChunks chunk = chunks.get(i);
            context.append("【参考").append(i + 1).append("】\n");
            context.append("来源：").append(chunk.getChapterTitle()).append("（第").append(chunk.getPageNumber()).append("页）\n");
            context.append("内容：").append(chunk.getChunkText()).append("\n\n");
        }

        return context.toString();
    }
}

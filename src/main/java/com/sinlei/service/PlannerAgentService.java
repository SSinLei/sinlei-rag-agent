package com.sinlei.service;

import com.sinlei.common.model.HotspotItem;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 策划智能体：结合热点摘要与大模型，生成多条「黄金3秒」开头候选。
 * 热点数据来自 {@link HotSearchRouter}（mock / real）。
 */
@Service
@RequiredArgsConstructor
public class PlannerAgentService {

    private final ChatModel chatModel;
    private final HotSearchRouter hotSearchRouter;

    /**
     * @param topic   视频主题
     * @param persona 人设风格，可为空则按「通用」处理
     * @param mode    热点模式 mock|real，空则使用配置默认
     * @return 3～5 条口语化开头文案
     */
    public List<String> planHooks(String topic, String persona, String mode) {
        List<HotspotItem> hotspots = hotSearchRouter.search(topic, mode);
        String hotspotText = hotspots.stream().map(HotspotItem::getTitle).reduce((a, b) -> a + "\n- " + b).orElse("暂无热点");
        String prompt = String.format(String.join("\n",
            "你是短视频策划智能体。请围绕主题生成5条“黄金3秒”开头，覆盖提问式、反差式、恐吓式、利益式、故事式。",
            "主题：%s",
            "人设：%s",
            "热点：",
            "- %s",
            "输出要求：",
            "1) 每条20-35字，口语化；",
            "2) 每条单独一行，不要编号，不要解释。"
        ), topic, persona == null ? "通用" : persona, hotspotText);
        try {
            String text = ChatClient.builder(chatModel).build().prompt().user(prompt).call().content();
            List<String> hooks = new ArrayList<>();
            for (String line : text.split("\\R")) {
                String cleaned = line.replaceFirst("^[\\-\\d\\.、\\s]+", "").trim();
                if (!cleaned.isBlank()) {
                    hooks.add(cleaned);
                }
                if (hooks.size() >= 5) {
                    break;
                }
            }
            if (!hooks.isEmpty()) {
                return hooks;
            }
        } catch (Exception ignored) {
        }
        return List.of(
            "你以为在省钱？其实这个坑每天都在偷你预算。",
            "同样是" + topic + "，为什么有人越做越好，有人越做越累？",
            "先别下结论，我用30秒告诉你最容易忽略的一点。"
        );
    }
}

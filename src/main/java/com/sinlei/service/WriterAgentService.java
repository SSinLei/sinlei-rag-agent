package com.sinlei.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * 撰写智能体：根据选定开头与人设，生成可直接口播的中文文案，并按语速做简单字数校准。
 */
@Service
@RequiredArgsConstructor
public class WriterAgentService {

    private final ChatModel chatModel;

    /**
     * @param topic       视频主题
     * @param persona     人设风格，可为空按「通用」
     * @param durationSec 目标成片时长（秒）
     * @param hook        已选黄金开头，须作为首句
     * @param styleHint   来自 {@link StyleRagService} 的样稿摘要，拼入提示词
     * @param speechRate  估算语速（字/秒），用于 {@link #fitDuration} 粗调长度
     */
    public String writeScript(String topic, String persona, int durationSec, String hook, String styleHint, double speechRate) {
        String prompt = String.format(String.join("\n",
            "你是短视频文案撰写智能体。请写一段可直接口播的中文文案。",
            "主题：%s",
            "人设：%s",
            "目标时长：%d秒",
            "开头（必须原样作为首句）：%s",
            "风格参考：",
            "%s",
            "",
            "要求：",
            "1) 全文口语化，短句，避免书面语；",
            "2) 结构：开头钩子 -> 核心观点 -> 证据/例子 -> 行动建议；",
            "3) 结尾给出明确互动引导；",
            "4) 只输出文案正文。"
        ), topic, persona == null ? "通用" : persona, durationSec, hook, styleHint);

        String raw = ChatClient.builder(chatModel).build().prompt().user(prompt).call().content();
        return fitDuration(raw, durationSec, speechRate);
    }

    /**
     * MVP 级时长控制：按目标字数截断或补一句，避免与目标秒数偏差过大。
     */
    private String fitDuration(String script, int durationSec, double speechRate) {
        int targetChars = (int) (durationSec * speechRate);
        if (script == null) {
            return "";
        }
        String cleaned = script.replaceAll("\\s+", "");
        if (cleaned.length() > targetChars + 30) {
            return cleaned.substring(0, targetChars + 20) + "。最后一句：你更关心哪个点，评论区告诉我。";
        }
        if (cleaned.length() < Math.max(40, targetChars - 30)) {
            return cleaned + "。补一句重点：先按这个框架做一版，你会立刻看到差距。";
        }
        return cleaned;
    }
}

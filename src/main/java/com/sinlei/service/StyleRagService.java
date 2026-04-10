package com.sinlei.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinlei.config.config.ShortVideoProperties;
import com.sinlei.common.entity.ShortVideoStyleSampleEntity;
import com.sinlei.mapper.ShortVideoStyleSampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 风格化 RAG（MVP）：样稿落库 {@code shortvideo_style_sample}，撰写前按人设（及可选 userId）取最近样稿拼成风格提示。
 * 无样稿时使用内置默认句式，避免冷启动无风格。
 */
@Service
@RequiredArgsConstructor
public class StyleRagService {

    private final ShortVideoProperties properties;
    private final ShortVideoStyleSampleMapper shortVideoStyleSampleMapper;

    /** 写入一条人设样稿 */
    public void addSample(String userId, String persona, String sampleText) {
        ShortVideoStyleSampleEntity entity = new ShortVideoStyleSampleEntity();
        entity.setUserId(userId);
        entity.setPersona(persona);
        entity.setSampleText(sampleText);
        shortVideoStyleSampleMapper.insert(entity);
    }

    /**
     * 检索风格摘要文本，供 {@link WriterAgentService} 注入提示词。
     *
     * @param userId  可为空；非空时优先匹配该用户 + 全局（user_id IS NULL）样稿
     * @param persona 人设名称
     */
    public String retrieveStyleHint(String userId, String persona) {
        if (persona == null || persona.isBlank()) {
            return "口语化、短句化、情绪清晰、避免书面语。";
        }
        LambdaQueryWrapper<ShortVideoStyleSampleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShortVideoStyleSampleEntity::getPersona, persona.trim())
            .and(w -> w.eq(userId != null, ShortVideoStyleSampleEntity::getUserId, userId)
                .or()
                .isNull(ShortVideoStyleSampleEntity::getUserId))
            .orderByDesc(ShortVideoStyleSampleEntity::getId);
        List<String> samples = shortVideoStyleSampleMapper.selectList(wrapper).stream()
            .map(ShortVideoStyleSampleEntity::getSampleText)
            .collect(Collectors.toList());
        if (samples.isEmpty()) {
            samples = defaultSamples(persona);
        }
        int topK = Math.max(1, properties.getPrompt().getStyleRetrievalTopK());
        return samples.stream().limit(topK).reduce((a, b) -> a + "\n" + b).orElse("口语化表达。");
    }

    /** 按人设关键词给出的内置示例句（无 DB 样稿时使用） */
    private List<String> defaultSamples(String persona) {
        if (persona.contains("犀利")) {
            return List.of("别急着下结论，这里有个反常识点。", "我先说结果：你可能买贵了。", "重点就三句话，我给你拆明白。");
        }
        if (persona.contains("温柔")) {
            return List.of("我们慢慢看，先从最关键的一点讲起。", "如果你也纠结，这个判断标准会很有帮助。", "不用焦虑，按这三步就够了。");
        }
        return List.of("先抛结论，再给证据，最后给行动建议。");
    }

}

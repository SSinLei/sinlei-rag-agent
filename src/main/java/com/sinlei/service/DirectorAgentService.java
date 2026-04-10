package com.sinlei.service;

import com.sinlei.common.model.StoryboardScene;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 导演智能体（MVP）：按句号拆分口播，生成固定上限条数的分镜，并附带英文画面提示词与预估时长。
 */
@Service
public class DirectorAgentService {

    /**
     * @param scriptText  口播全文
     * @param durationSec 目标总时长，用于均分到各镜
     */
    public List<StoryboardScene> generateScenes(String scriptText, int durationSec) {
        String[] parts = scriptText.split("[。！？!?]");
        List<String> lines = new ArrayList<>();
        for (String part : parts) {
            String t = part.trim();
            if (!t.isBlank()) {
                lines.add(t);
            }
        }
        if (lines.isEmpty()) {
            return List.of();
        }
        int sceneCount = Math.min(8, lines.size());
        int avgDuration = Math.max(3, durationSec / sceneCount);
        List<StoryboardScene> scenes = new ArrayList<>();
        for (int i = 0; i < sceneCount; i++) {
            String line = lines.get(i);
            StoryboardScene scene = new StoryboardScene();
            scene.setSceneNo(i + 1);
            scene.setVoiceoverCn(line + "。");
            scene.setEmotionTag(i == 0 ? "抓人" : (i == sceneCount - 1 ? "号召" : "推进"));
            scene.setEstDurationSec(avgDuration);
            scene.setVisualPromptEn("cinematic short-video frame, " + toPromptKeyword(line) + ", high contrast, storytelling");
            scenes.add(scene);
        }
        return scenes;
    }

    /** 取台词前缀作为英文 prompt 的关键词，控制长度避免 prompt 过长 */
    private String toPromptKeyword(String text) {
        return text.length() > 24 ? text.substring(0, 24) : text;
    }
}

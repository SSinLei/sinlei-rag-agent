package com.sinlei.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinlei.common.model.ScriptProjectResult;
import com.sinlei.common.model.StoryboardScene;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 将 {@link ScriptProjectResult} 导出为 Markdown 或 JSON，便于人工拍摄或下游自动化。
 */
@Service
@RequiredArgsConstructor
public class ShortVideoExportService {

    private final ObjectMapper objectMapper;

    /**
     * 生成 Markdown：标题元信息、口播正文、分镜表格（管道符已做转义）。
     */
    public String toMarkdown(ScriptProjectResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(result.getTitle()).append("\n\n");
        sb.append("- 主题: ").append(result.getTopic()).append("\n");
        sb.append("- 人设: ").append(result.getPersona()).append("\n");
        sb.append("- 时长: ").append(result.getDurationSec()).append("s\n");
        sb.append("- 开头: ").append(result.getSelectedHook()).append("\n\n");
        sb.append("## 口播文案\n\n").append(result.getScriptText()).append("\n\n");
        sb.append("## 分镜表\n\n");
        sb.append("| 场景 | 画面提示词 | 台词 | 情绪 | 时长(s) |\n");
        sb.append("|---|---|---|---|---|\n");
        for (StoryboardScene scene : result.getScenes()) {
            sb.append("| ").append(scene.getSceneNo()).append(" | ")
                .append(escape(scene.getVisualPromptEn())).append(" | ")
                .append(escape(scene.getVoiceoverCn())).append(" | ")
                .append(escape(scene.getEmotionTag())).append(" | ")
                .append(scene.getEstDurationSec()).append(" |\n");
        }
        return sb.toString();
    }

    /**
     * 生成格式化 JSON，字段与 {@link ScriptProjectResult} 一致。
     */
    public String toJson(ScriptProjectResult result) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    }

    /** 避免 Markdown 表格单元格内的竖线破坏列对齐 */
    private String escape(String source) {
        return source == null ? "" : source.replace("|", "\\|");
    }
}

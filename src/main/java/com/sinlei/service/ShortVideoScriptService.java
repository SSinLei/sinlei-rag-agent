package com.sinlei.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinlei.config.config.ShortVideoProperties;
import com.sinlei.common.dto.ShortVideoGenerateRequest;
import com.sinlei.common.entity.ShortVideoProjectEntity;
import com.sinlei.common.entity.ShortVideoSceneEntity;
import com.sinlei.mapper.ShortVideoProjectMapper;
import com.sinlei.mapper.ShortVideoSceneMapper;
import com.sinlei.common.model.ScriptMeta;
import com.sinlei.common.model.ScriptProjectResult;
import com.sinlei.common.model.StoryboardScene;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 短视频脚本工坊核心业务：多智能体编排 + 结果持久化。
 * <p>
 * 流水线：{@link PlannerAgentService}（开头）→ {@link WriterAgentService}（口播）→ {@link DirectorAgentService}（分镜），
 * 风格参考由 {@link StyleRagService} 提供。生成结果写入表 {@code shortvideo_project} / {@code shortvideo_scene}。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ShortVideoScriptService {

    /** 策划智能体：黄金3秒开头 */
    private final PlannerAgentService plannerAgentService;
    /** 撰写智能体：口语化口播文案 */
    private final WriterAgentService writerAgentService;
    /** 导演智能体：文案转分镜结构 */
    private final DirectorAgentService directorAgentService;
    /** 风格样稿检索与提示摘要 */
    private final StyleRagService styleRagService;
    /** 短视频相关配置（默认热点模式、语速、检索 topK 等） */
    private final ShortVideoProperties properties;
    /** 项目主表 */
    private final ShortVideoProjectMapper shortVideoProjectMapper;
    /** 分镜明细表 */
    private final ShortVideoSceneMapper shortVideoSceneMapper;
    /** hooks 列表 JSON 序列化 */
    private final ObjectMapper objectMapper;

    /**
     * 仅生成开头候选，供前端多选；不写入项目表。
     */
    public List<String> planHooks(String topic, String persona, String mode) {
        return plannerAgentService.planHooks(topic, persona, mode);
    }

    /**
     * 生成完整项目：口播 + 分镜，并事务写入数据库。
     */
    @Transactional(rollbackFor = Exception.class)
    public ScriptProjectResult generate(ShortVideoGenerateRequest request) {
        int durationSec = request.getDurationSec() == null ? 60 : request.getDurationSec();
        String mode = (request.getSearchMode() == null || request.getSearchMode().isBlank())
            ? properties.getSearch().getMode() : request.getSearchMode();
        List<String> hooks = plannerAgentService.planHooks(request.getTopic(), request.getPersona(), mode);
        String selectedHook = (request.getSelectedHook() == null || request.getSelectedHook().isBlank())
            ? hooks.get(0) : request.getSelectedHook();

        String styleHint = styleRagService.retrieveStyleHint(request.getUserId(), request.getPersona());
        String scriptText = writerAgentService.writeScript(
            request.getTopic(),
            request.getPersona(),
            durationSec,
            selectedHook,
            styleHint,
            properties.getDuration().getSpeechRate()
        );

        ScriptProjectResult result = new ScriptProjectResult();
        result.setProjectId("sv-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        result.setTitle(request.getTopic() + "｜" + (request.getPersona() == null ? "通用风格" : request.getPersona()));
        result.setTopic(request.getTopic());
        result.setPersona(request.getPersona());
        result.setDurationSec(durationSec);
        result.setHooks(hooks);
        result.setSelectedHook(selectedHook);
        result.setScriptText(scriptText);
        result.setScenes(directorAgentService.generateScenes(scriptText, durationSec));
        ScriptMeta meta = new ScriptMeta();
        meta.setSearchMode(mode);
        meta.setSpeechRate(properties.getDuration().getSpeechRate());
        meta.setStyleHint(styleHint);
        result.setMeta(meta);

        persistProject(request.getUserId(), result);
        return result;
    }

    /**
     * 按业务 projectId（如 sv-xxx）从数据库组装 {@link ScriptProjectResult}，供导出与查询。
     */
    public ScriptProjectResult getById(String id) {
        LambdaQueryWrapper<ShortVideoProjectEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShortVideoProjectEntity::getProjectId, id).last("limit 1");
        ShortVideoProjectEntity project = shortVideoProjectMapper.selectOne(wrapper);
        if (project == null) {
            return null;
        }
        ScriptProjectResult result = new ScriptProjectResult();
        result.setProjectId(project.getProjectId());
        result.setTitle(project.getTitle());
        result.setTopic(project.getTopic());
        result.setPersona(project.getPersona());
        result.setDurationSec(project.getDurationSec());
        result.setSelectedHook(project.getSelectedHook());
        result.setScriptText(project.getScriptText());
        result.setHooks(parseHooks(project.getHooksJson()));

        LambdaQueryWrapper<ShortVideoSceneEntity> sceneWrapper = new LambdaQueryWrapper<>();
        sceneWrapper.eq(ShortVideoSceneEntity::getProjectId, id).orderByAsc(ShortVideoSceneEntity::getSceneNo);
        List<StoryboardScene> scenes = shortVideoSceneMapper.selectList(sceneWrapper).stream().map(this::toModel).collect(Collectors.toList());
        result.setScenes(scenes);

        ScriptMeta meta = new ScriptMeta();
        meta.setSearchMode(project.getSearchMode());
        meta.setSpeechRate(project.getSpeechRate() == null ? null : project.getSpeechRate().doubleValue());
        meta.setStyleHint(project.getStyleHint());
        result.setMeta(meta);
        return result;
    }

    /**
     * 将内存中的生成结果落库：一条项目记录 + 多条分镜记录。
     */
    private void persistProject(String userId, ScriptProjectResult result) {
        ShortVideoProjectEntity project = new ShortVideoProjectEntity();
        project.setProjectId(result.getProjectId());
        project.setUserId(userId);
        project.setTopic(result.getTopic());
        project.setPersona(result.getPersona());
        project.setTitle(result.getTitle());
        project.setDurationSec(result.getDurationSec());
        project.setSelectedHook(result.getSelectedHook());
        project.setHooksJson(writeHooks(result.getHooks()));
        project.setScriptText(result.getScriptText());
        project.setSearchMode(result.getMeta().getSearchMode());
        project.setSpeechRate(BigDecimal.valueOf(result.getMeta().getSpeechRate()));
        project.setStyleHint(result.getMeta().getStyleHint());
        shortVideoProjectMapper.insert(project);

        for (StoryboardScene scene : result.getScenes()) {
            ShortVideoSceneEntity entity = new ShortVideoSceneEntity();
            entity.setProjectId(result.getProjectId());
            entity.setSceneNo(scene.getSceneNo());
            entity.setVisualPromptEn(scene.getVisualPromptEn());
            entity.setVoiceoverCn(scene.getVoiceoverCn());
            entity.setEmotionTag(scene.getEmotionTag());
            entity.setEstDurationSec(scene.getEstDurationSec());
            shortVideoSceneMapper.insert(entity);
        }
    }

    /** 将开头列表序列化为 JSON 字符串存入 {@code hooks_json}。 */
    private String writeHooks(List<String> hooks) {
        try {
            return objectMapper.writeValueAsString(hooks);
        } catch (Exception e) {
            return "[]";
        }
    }

    /** 从 {@code hooks_json} 反序列化开头列表；异常时返回空列表。 */
    private List<String> parseHooks(String hooksJson) {
        if (hooksJson == null || hooksJson.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(hooksJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /** 分镜实体 → API 模型 */
    private StoryboardScene toModel(ShortVideoSceneEntity entity) {
        StoryboardScene model = new StoryboardScene();
        model.setSceneNo(entity.getSceneNo());
        model.setVisualPromptEn(entity.getVisualPromptEn());
        model.setVoiceoverCn(entity.getVoiceoverCn());
        model.setEmotionTag(entity.getEmotionTag());
        model.setEstDurationSec(entity.getEstDurationSec());
        return model;
    }
}

package com.sinlei.controller;

import com.sinlei.common.Result;
import com.sinlei.common.dto.HookPlanRequest;
import com.sinlei.common.dto.ShortVideoGenerateRequest;
import com.sinlei.common.dto.StyleSampleRequest;
import com.sinlei.common.model.ScriptProjectResult;
import com.sinlei.service.ShortVideoExportService;
import com.sinlei.service.ShortVideoScriptService;
import com.sinlei.service.StyleRagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短视频脚本工坊 REST 接口。
 * <p>
 * 提供：黄金开头策划、完整脚本与分镜生成、人设样稿入库、Markdown/JSON 导出。
 * 实际业务编排见 {@link ShortVideoScriptService}。
 * </p>
 */
@RestController
@RequestMapping("/shortvideo")
@RequiredArgsConstructor
@Tag(name = "短视频脚本工坊", description = "短视频脚本生成、分镜与导出")
public class ShortVideoController {

    /** 脚本生成与持久化 */
    private final ShortVideoScriptService shortVideoScriptService;
    /** Markdown / JSON 导出 */
    private final ShortVideoExportService shortVideoExportService;
    /** 人设样稿与风格提示（MVP：落库 + 检索摘要） */
    private final StyleRagService styleRagService;

    /**
     * 根据主题与人设生成多条「黄金3秒」开头候选，可指定 mock/real 热点模式。
     */
    @PostMapping("/hooks")
    @Operation(summary = "生成黄金3秒开头方案")
    public Result<List<String>> hooks(@RequestBody HookPlanRequest request) {
        if (request.getTopic() == null || request.getTopic().isBlank()) {
            return Result.error("topic不能为空");
        }
        return Result.success(shortVideoScriptService.planHooks(request.getTopic(), request.getPersona(), request.getSearchMode()));
    }

    /**
     * 一键生成：策划开头（内部仍会调用策划逻辑）、撰写口播、导演分镜，并写入 PostgreSQL。
     *
     * @param request 主题、人设、时长、可选已选开头、热点模式等
     */
    @PostMapping("/generate")
    @Operation(summary = "生成短视频脚本分镜")
    public Result<ScriptProjectResult> generate(@RequestBody ShortVideoGenerateRequest request) {
        if (request.getTopic() == null || request.getTopic().isBlank()) {
            return Result.error("topic不能为空");
        }
        return Result.success(shortVideoScriptService.generate(request));
    }

    /**
     * 新增人设样稿，供 {@link StyleRagService} 检索后拼入撰写提示词。
     */
    @PostMapping("/styles")
    @Operation(summary = "新增风格样稿")
    public Result<Void> addStyleSample(@RequestBody StyleSampleRequest request) {
        if (request.getPersona() == null || request.getPersona().isBlank()) {
            return Result.error("persona不能为空");
        }
        if (request.getSampleText() == null || request.getSampleText().isBlank()) {
            return Result.error("sampleText不能为空");
        }
        styleRagService.addSample(request.getUserId(), request.getPersona(), request.getSampleText());
        return Result.success();
    }

    /**
     * 按项目 ID 导出拍摄用 Markdown（含口播与分镜表）。
     */
    @GetMapping("/{projectId}/export.md")
    @Operation(summary = "导出Markdown")
    public ResponseEntity<String> exportMarkdown(@PathVariable String projectId) {
        ScriptProjectResult result = shortVideoScriptService.getById(projectId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        String content = shortVideoExportService.toMarkdown(result);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + projectId + ".md\"")
            .contentType(MediaType.parseMediaType("text/markdown"))
            .body(content);
    }

    /**
     * 按项目 ID 导出结构化 JSON，便于对接 TTS / 剪辑流水线。
     */
    @GetMapping("/{projectId}/export.json")
    @Operation(summary = "导出JSON")
    public ResponseEntity<String> exportJson(@PathVariable String projectId) throws Exception {
        ScriptProjectResult result = shortVideoScriptService.getById(projectId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        String content = shortVideoExportService.toJson(result);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + projectId + ".json\"")
            .contentType(MediaType.APPLICATION_JSON)
            .body(content);
    }
}

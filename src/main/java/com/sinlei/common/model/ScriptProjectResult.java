package com.sinlei.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 一次生成任务的完整结果：业务 projectId、口播、分镜、元数据；与导出 JSON 结构一致。
 */
@Data
@Schema(description = "短视频脚本项目结果")
public class ScriptProjectResult {

    @Schema(description = "项目ID", example = "sv-2d7858d95b1b")
    private String projectId;

    @Schema(description = "标题", example = "iPhone16测评：买前先看这5个关键点")
    private String title;

    @Schema(description = "主题", example = "iPhone16测评")
    private String topic;

    @Schema(description = "人设风格", example = "犀利吐槽风")
    private String persona;

    @Schema(description = "总时长预估（秒）", example = "60")
    private Integer durationSec;

    @Schema(description = "黄金3秒候选")
    private List<String> hooks = new ArrayList<>();

    @Schema(description = "最终选中开头")
    private String selectedHook;

    @Schema(description = "完整口播文案")
    private String scriptText;

    @Schema(description = "分镜列表")
    private List<StoryboardScene> scenes = new ArrayList<>();

    @Schema(description = "元数据")
    private ScriptMeta meta = new ScriptMeta();
}

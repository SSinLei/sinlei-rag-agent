package com.sinlei.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 请求体：仅生成「黄金3秒」开头候选，不落库项目。
 */
@Data
@Schema(description = "黄金3秒开头生成请求")
public class HookPlanRequest {

    @Schema(description = "用户ID", example = "creator-001")
    private String userId;

    @Schema(description = "主题", requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone16测评")
    private String topic;

    @Schema(description = "人设风格", example = "犀利吐槽风")
    private String persona;

    @Schema(description = "热点检索模式：mock|real", example = "mock")
    private String searchMode;
}

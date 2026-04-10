package com.sinlei.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 请求体：生成完整口播 + 分镜并持久化；可选传入已选开头，否则服务端取候选首条。
 */
@Data
@Schema(description = "短视频脚本生成请求")
public class ShortVideoGenerateRequest {

    @Schema(description = "用户ID", example = "creator-001")
    private String userId;

    @Schema(description = "主题", requiredMode = Schema.RequiredMode.REQUIRED, example = "iPhone16测评")
    private String topic;

    @Schema(description = "人设风格", example = "犀利吐槽风")
    private String persona;

    @Schema(description = "目标时长（秒）", example = "60")
    private Integer durationSec = 60;

    @Schema(description = "已选黄金3秒开头文案", example = "你可能刚买的iPhone16，正在被这3个隐藏设置拖慢！")
    private String selectedHook;

    @Schema(description = "热点检索模式：mock|real", example = "mock")
    private String searchMode;
}

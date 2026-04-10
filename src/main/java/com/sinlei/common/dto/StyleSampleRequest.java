package com.sinlei.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 请求体：新增人设口播/文案样稿，供风格检索注入撰写提示词。
 */
@Data
@Schema(description = "人设样稿请求")
public class StyleSampleRequest {

    @Schema(description = "用户ID", example = "creator-001")
    private String userId;

    @Schema(description = "人设风格", requiredMode = Schema.RequiredMode.REQUIRED, example = "温柔知性风")
    private String persona;

    @Schema(description = "样稿文本", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sampleText;
}

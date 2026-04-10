package com.sinlei.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 单条分镜：画面英文 prompt、口播台词、情绪与预估时长。
 */
@Data
@Schema(description = "分镜场景")
public class StoryboardScene {

    @Schema(description = "场景序号", example = "1")
    private Integer sceneNo;

    @Schema(description = "画面英文提示词", example = "close-up shot of smartphone screen, dramatic lighting, cinematic")
    private String visualPromptEn;

    @Schema(description = "口播台词", example = "先别急着下单，这代最坑的不是价格。")
    private String voiceoverCn;

    @Schema(description = "情绪标记", example = "惊讶")
    private String emotionTag;

    @Schema(description = "预估时长（秒）", example = "8")
    private Integer estDurationSec;
}

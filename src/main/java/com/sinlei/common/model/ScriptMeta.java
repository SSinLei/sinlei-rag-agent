package com.sinlei.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 与生成过程相关的附加信息：热点模式、语速假设、风格摘要等。
 */
@Data
@Schema(description = "脚本元数据")
public class ScriptMeta {

    @Schema(description = "热点模式", example = "mock")
    private String searchMode;

    @Schema(description = "估算语速（字/秒）", example = "3.8")
    private Double speechRate;

    @Schema(description = "风格提示摘要")
    private String styleHint;
}

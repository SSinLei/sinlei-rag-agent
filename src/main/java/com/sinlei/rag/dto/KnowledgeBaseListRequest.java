package com.sinlei.rag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识库列表查询请求")
public class KnowledgeBaseListRequest {

    @Schema(description = "险种编码", required = true, example = "00520")
    private String productCode;
}

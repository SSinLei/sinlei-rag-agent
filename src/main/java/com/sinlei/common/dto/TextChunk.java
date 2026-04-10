package com.sinlei.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文本块DTO
 * 用于PDF解析后封装分割的文本块信息
 * 是PDF到向量存储的中间数据格式
 */
@Data
@Schema(description = "文本块DTO")
public class TextChunk {

    /**
     * 章节标题
     * 该文本块所属的PDF章节标题
     * 用于后续回答时引用来源
     */
    @Schema(description = "章节标题")
    private String chapterTitle;

    /**
     * 文本内容
     * 分割后的实际文本内容，将用于向量化
     */
    @Schema(description = "文本内容")
    private String content;

    /**
     * 页码
     * 该文本块在原始PDF中的页码
     */
    @Schema(description = "PDF页码")
    private int pageNumber;

    /**
     * 块索引
     * 该文本块在分割后的顺序索引
     */
    @Schema(description = "块索引")
    private int chunkIndex;

    /**
     * 向量数据
     * 文本块经过embedding模型生成的向量（1024维）
     * 用于向量相似度检索
     */
    @Schema(description = "向量数据（1024维）")
    private Object embedding;
}

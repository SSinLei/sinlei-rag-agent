package com.sinlei.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识块表实体类
 * 用于存储PDF文档分割后的文本块及对应的向量数据
 * 是RAG（检索增强生成）的核心数据存储
 *
 * 对应数据库表: knowledge_chunks
 */
@Data
@TableName(value = "knowledge_chunks", autoResultMap = true)
@Schema(description = "知识块表实体（存储文本块及向量）")
public class KnowledgeChunks {

    /**
     * 主键ID
     * 使用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 关联主表ID
     * 关联到knowledge_base表的ID，表示该知识块属于哪个文档
     */
    @TableField("knowledge_base_id")
    @Schema(description = "关联主表ID")
    private Long knowledgeBaseId;

    /**
     * 用户ID
     * 用于数据隔离，确保用户只能检索自己的知识块
     */
    @TableField("user_id")
    @Schema(description = "用户ID，用于数据隔离")
    private String userId;

    /**
     * 险种编码
     * 6位数字编码，用于高效检索特定产品的知识块
     */
    @TableField("product_code")
    @Schema(description = "险种编码（6位）", example = "00520")
    private String productCode;

    /**
     * 章节标题
     * 该文本块所属的章节标题，用于上下文引用
     */
    @TableField("chapter_title")
    @Schema(description = "章节标题")
    private String chapterTitle;

    /**
     * 文本块内容
     * 分割后的实际文本内容，是向量化的原始数据
     */
    @TableField("chunk_text")
    @Schema(description = "分割后的文本块内容")
    private String chunkText;

    /**
     * 文本块索引
     * 该文本块在原始文档中的顺序索引
     */
    @TableField("chunk_index")
    @Schema(description = "文本块顺序索引")
    private Integer chunkIndex;

    /**
     * 向量数据
     * 文本块经过embedding模型生成的向量（1024维）
     * 用于向量相似度检索
     */
    @TableField("embedding")
    @Schema(description = "向量数据（1024维）")
    private Object embedding;

    /**
     * 对应PDF页码
     * 该文本块在原始PDF中的页码
     */
    @TableField("page_number")
    @Schema(description = "对应PDF页码")
    private Integer pageNumber;

    /**
     * 创建时间
     * 记录数据创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}

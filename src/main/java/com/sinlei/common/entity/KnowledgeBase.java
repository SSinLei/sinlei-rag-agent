package com.sinlei.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库主表实体类
 * 用于存储用户上传的PDF文档基本信息
 *
 * 对应数据库表: knowledge_base
 */
@Data
@TableName(value = "knowledge_base", autoResultMap = true)
@Schema(description = "知识库主表实体")
public class KnowledgeBase {

    /**
     * 主键ID
     * 使用数据库自增策略
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户ID
     * 用于数据隔离，确保用户只能访问自己的知识库
     */
    @TableField("user_id")
    @Schema(description = "用户ID，用于数据隔离")
    private String userId;

    /**
     * 险种编码
     * 6位数字编码，如00520，用于唯一标识保险产品
     */
    @TableField("product_code")
    @Schema(description = "险种编码（6位）", example = "00520")
    private String productCode;

    /**
     * 产品名称
     * 保险产品的全称
     */
    @TableField("product_name")
    @Schema(description = "产品名称")
    private String productName;

    /**
     * 条款类型
     * 如：主险、附加险
     */
    @TableField("clause_type")
    @Schema(description = "条款类型（主险/附加险）")
    private String clauseType;

    /**
     * 文档原始名称
     * 用户上传时的PDF文件名
     */
    @TableField("document_name")
    @Schema(description = "文档原始名称")
    private String documentName;

    /**
     * 源文件本地存储路径
     * PDF文件在服务器上的存储路径
     */
    @TableField("source_file_url")
    @Schema(description = "源文件本地存储路径")
    private String sourceFileUrl;

    /**
     * 创建时间
     * 记录数据创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 记录数据最后更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}

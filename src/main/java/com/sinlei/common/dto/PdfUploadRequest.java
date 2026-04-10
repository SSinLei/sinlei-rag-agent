package com.sinlei.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * PDF上传请求DTO
 * 用于接收前端上传PDF文件时的请求参数
 *
 * 使用方式：作为MultipartFile的伴随参数一起提交
 */
@Data
@Schema(description = "PDF上传请求")
public class PdfUploadRequest {

    /**
     * 用户ID
     * 必填参数，用于标识上传者的身份，并进行数据隔离
     */
    @Schema(description = "用户ID", required = true, example = "USER001")
    private String userId;

    /**
     * 险种编码
     * 必填参数，6位数字编码，用于唯一标识保险产品
     * 示例：00520
     */
    @Schema(description = "险种编码（6位）", required = true, example = "00520")
    private String productCode;

    /**
     * 产品名称
     * 选填参数，保险产品的全称
     * 示例：长城八达岭赤兔版年金保险（2024）
     */
    @Schema(description = "产品名称", example = "长城八达岭赤兔版年金保险（2024）")
    private String productName;

    /**
     * 条款类型
     * 选填参数，用于标识条款的类型
     * 可选值：主险、附加险
     */
    @Schema(description = "条款类型（主险/附加险）", example = "主险")
    private String clauseType;
}

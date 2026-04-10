package com.sinlei.mcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 缴费状态DTO
 * 用于返回保单缴费信息的数据结构
 */
@Data
@Schema(description = "缴费状态")
public class PaymentStatusDTO {

    /**
     * 保单号
     * 关联的保险合同编号
     */
    @Schema(description = "保单号", example = "POL202401010001")
    private String policyNo;

    /**
     * 应缴日期
     * 本次应缴纳保费的日期
     */
    @Schema(description = "应缴日期", example = "2025-01-01")
    private String dueDate;

    /**
     * 实缴日期
     * 实际缴纳保费的日期
     */
    @Schema(description = "实缴日期", example = "2024-12-28")
    private String paidDate;

    /**
     * 缴费状态
     * 当前保费的缴纳状态
     */
    @Schema(description = "缴费状态（已缴/待缴/逾期/豁免）", example = "已缴")
    private String paymentStatus;

    /**
     * 应缴金额
     * 本期应缴纳的保费金额
     */
    @Schema(description = "应缴金额", example = "10000.00")
    private Double dueAmount;

    /**
     * 实缴金额
     * 本期实际缴纳的保费金额
     */
    @Schema(description = "实缴金额", example = "10000.00")
    private Double paidAmount;

    /**
     * 逾期天数
     * 超过应缴日期还未缴纳的天数
     */
    @Schema(description = "逾期天数", example = "0")
    private Integer overdueDays;

    /**
     * 缴费期数信息
     * 包含各期缴费详情的列表
     */
    @Schema(description = "缴费期数信息")
    private List<PaymentPeriodDTO> paymentPeriods;
}


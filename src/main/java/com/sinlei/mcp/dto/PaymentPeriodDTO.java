package com.sinlei.mcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data; /**
 * 缴费期数DTO
 * 用于记录每一期缴费的详细信息
 */
@Data
@Schema(description = "缴费期数信息")
public class PaymentPeriodDTO {

    /**
     * 期数
     * 第几期保费，如第1期、第2期
     */
    @Schema(description = "期数", example = "1")
    private Integer period;

    /**
     * 应缴日期
     * 该期保费应缴纳的日期
     */
    @Schema(description = "应缴日期", example = "2025-01-01")
    private String dueDate;

    /**
     * 实缴日期
     * 该期保费实际缴纳的日期
     */
    @Schema(description = "实缴日期", example = "2024-12-28")
    private String paidDate;

    /**
     * 缴费状态
     * 该期保费的缴纳状态
     */
    @Schema(description = "缴费状态", example = "已缴")
    private String status;

    /**
     * 金额
     * 该期保费金额
     */
    @Schema(description = "金额", example = "10000.00")
    private Double amount;
}

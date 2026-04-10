package com.sinlei.mcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保单基本信息DTO
 * 用于返回保单列表和详情的统一数据结构
 */
@Data
@Schema(description = "保单基本信息")
public class PolicyInfoDTO {

    /**
     * 保单号
     * 保险合同的唯一标识编号
     */
    @Schema(description = "保单号", example = "POL202401010001")
    private String policyNo;

    /**
     * 险种编码
     * 6位数字编码，对应保险产品
     */
    @Schema(description = "险种编码", example = "00520")
    private String productCode;

    /**
     * 产品名称
     * 保险产品的全称
     */
    @Schema(description = "产品名称", example = "长城八达岭赤兔版年金保险（2024）")
    private String productName;

    /**
     * 投保人姓名
     * 签订保险合同的主体姓名
     */
    @Schema(description = "投保人姓名", example = "张三")
    private String policyHolderName;

    /**
     * 被保人姓名
     * 受保险保障的主体姓名
     */
    @Schema(description = "被保人姓名", example = "张三")
    private String insuredName;

    /**
     * 主险保费
     * 投保人需要缴纳的保险费用
     */
    @Schema(description = "主险保费", example = "10000.00")
    private Double premium;

    /**
     * 缴费方式
     * 保费缴纳频率
     */
    @Schema(description = "缴费方式（年缴/半年缴/季缴/月缴）", example = "年缴")
    private String paymentFrequency;

    /**
     * 保单状态
     * 保险合同当前的状态
     */
    @Schema(description = "保单状态（有效/失效/终止/退保）", example = "有效")
    private String policyStatus;

    /**
     * 签发日期
     * 保险合同生效的日期
     */
    @Schema(description = "签发日期", example = "2024-01-01")
    private String issueDate;

    /**
     * 保障金额
     * 保险合同约定的最高赔付金额
     */
    @Schema(description = "保障金额", example = "150000.00")
    private Double coverageAmount;

    /**
     * 缴费到期日
     * 下次应缴保费的日期
     */
    @Schema(description = "缴费到期日", example = "2025-01-01")
    private String paymentDueDate;
}

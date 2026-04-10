package com.sinlei.mcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保单现金价值DTO
 * 用于返回保单现金价值信息的数据结构
 */
@Data
@Schema(description = "保单现金价值")
public class PolicyFundDTO {

    /**
     * 保单号
     * 关联的保险合同编号
     */
    @Schema(description = "保单号", example = "POL202401010001")
    private String policyNo;

    /**
     * 当前现金价值
     * 保单当前可退保的现金价值金额
     */
    @Schema(description = "当前现金价值", example = "15000.00")
    private Double currentFundValue;

    /**
     * 累计红利
     * 保险产品分配的红利累计金额
     */
    @Schema(description = "累计红利", example = "2000.00")
    private Double accumulatedBonus;

    /**
     * 账户价值
     * 投资连结型保险的投资账户价值
     */
    @Schema(description = "账户价值（投资连结型）")
    private Double accountValue;

    /**
     * 现金价值表年份
     * 对应现金价值表的年度
     */
    @Schema(description = "现金价值表年份", example = "1")
    private Integer fundYear;

    /**
     * 现金价值说明
     * 现金价值的详细说明文字
     */
    @Schema(description = "现金价值说明", example = "第一年末现金价值为15000元，累计红利2000元")
    private String fundDescription;
}

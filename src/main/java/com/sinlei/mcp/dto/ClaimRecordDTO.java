package com.sinlei.mcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 理赔记录DTO
 * 用于返回保单理赔信息的数据结构
 */
@Data
@Schema(description = "理赔记录")
public class ClaimRecordDTO {

    /**
     * 理赔申请号
     * 每次理赔申请的唯一标识编号
     */
    @Schema(description = "理赔申请号", example = "CLM202401010001")
    private String claimNo;

    /**
     * 保单号
     * 关联的保险合同编号
     */
    @Schema(description = "保单号", example = "POL202401010001")
    private String policyNo;

    /**
     * 理赔类型
     * 理赔事故的类型
     */
    @Schema(description = "理赔类型（医疗/重疾/身故/伤残等）", example = "医疗")
    private String claimType;

    /**
     * 理赔状态
     * 当前理赔申请的审核状态
     */
    @Schema(description = "理赔状态（待审核/审核中/已结案/已拒赔）", example = "已结案")
    private String claimStatus;

    /**
     * 申请金额
     * 申请人提交的理赔金额
     */
    @Schema(description = "申请金额", example = "5000.00")
    private Double applyAmount;

    /**
     * 实际赔付金额
     * 审核后实际赔付给申请人的金额
     */
    @Schema(description = "实际赔付金额", example = "4500.00")
    private Double paidAmount;

    /**
     * 申请日期
     * 提交理赔申请的日期
     */
    @Schema(description = "申请日期", example = "2024-03-15")
    private String applyDate;

    /**
     * 结案日期
     * 理赔审核完成并结案的日期
     */
    @Schema(description = "结案日期", example = "2024-03-25")
    private String settleDate;

    /**
     * 理赔结论说明
     * 审核结论的详细说明
     */
    @Schema(description = "理赔结论说明", example = "审核通过，按合同约定赔付")
    private String conclusion;
}

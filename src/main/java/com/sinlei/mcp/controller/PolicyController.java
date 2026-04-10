package com.sinlei.mcp.controller;

import com.sinlei.mcp.common.Result;
import com.sinlei.mcp.dto.ClaimRecordDTO;
import com.sinlei.mcp.dto.PaymentStatusDTO;
import com.sinlei.mcp.dto.PolicyFundDTO;
import com.sinlei.mcp.dto.PolicyInfoDTO;
import com.sinlei.mcp.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 保单查询控制器
 * 提供保单相关信息的REST API接口
 *
 * 接口说明：
 * 1. 保单列表查询 - 根据用户ID查询所有保单
 * 2. 保单详情查询 - 根据保单号查询详细信息
 * 3. 理赔记录查询 - 根据保单号查询理赔记录
 * 4. 缴费状态查询 - 根据保单号查询保费缴纳状态
 * 5. 现金价值查询 - 根据保单号查询现金价值信息
 */
@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
@Tag(name = "保单查询", description = "保单相关信息查询接口")
public class PolicyController {

    /**
     * 保单业务服务
     */
    private final PolicyService policyService;

    /**
     * 根据用户ID查询保单列表
     * 返回指定用户的所有有效保单信息
     *
     * @param userId 用户ID
     * @return 保单列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询保单列表", description = "根据用户ID查询该用户的所有保单列表")
    public Result<List<PolicyInfoDTO>> getPolicyList(
            @Parameter(description = "用户ID", required = true) @RequestParam String userId) {
        List<PolicyInfoDTO> policies = policyService.getPolicyList(userId);
        return Result.success(policies);
    }

    /**
     * 根据保单号查询保单详情
     * 返回指定保单的完整信息
     *
     * @param policyNo 保单号
     * @return 保单详情
     */
    @GetMapping("/{policyNo}")
    @Operation(summary = "查询保单详情", description = "根据保单号查询保单详细信息")
    public Result<PolicyInfoDTO> getPolicyDetail(
            @Parameter(description = "保单号", required = true) @PathVariable String policyNo) {
        PolicyInfoDTO policy = policyService.getPolicyDetail(policyNo);
        return Result.success(policy);
    }

    /**
     * 根据保单号查询理赔记录
     * 返回指定保单的所有理赔申请记录
     *
     * @param policyNo 保单号
     * @return 理赔记录列表
     */
    @GetMapping("/{policyNo}/claims")
    @Operation(summary = "查询理赔记录", description = "根据保单号查询理赔记录列表")
    public Result<List<ClaimRecordDTO>> getClaimRecords(
            @Parameter(description = "保单号", required = true) @PathVariable String policyNo) {
        List<ClaimRecordDTO> claims = policyService.getClaimRecords(policyNo);
        return Result.success(claims);
    }

    /**
     * 根据保单号查询缴费状态
     * 返回指定保单的保费缴纳状态信息
     *
     * @param policyNo 保单号
     * @return 缴费状态信息
     */
    @GetMapping("/{policyNo}/payment")
    @Operation(summary = "查询缴费状态", description = "根据保单号查询缴费状态信息")
    public Result<PaymentStatusDTO> getPaymentStatus(
            @Parameter(description = "保单号", required = true) @PathVariable String policyNo) {
        PaymentStatusDTO payment = policyService.getPaymentStatus(policyNo);
        return Result.success(payment);
    }

    /**
     * 根据保单号查询现金价值
     * 返回指定保单的现金价值信息
     *
     * @param policyNo 保单号
     * @return 现金价值信息
     */
    @GetMapping("/{policyNo}/fund")
    @Operation(summary = "查询现金价值", description = "根据保单号查询现金价值信息")
    public Result<PolicyFundDTO> getPolicyFund(
            @Parameter(description = "保单号", required = true) @PathVariable String policyNo) {
        PolicyFundDTO fund = policyService.getPolicyFund(policyNo);
        return Result.success(fund);
    }
}

package com.sinlei.mcp.tool;

import com.sinlei.mcp.dto.ClaimRecordDTO;
import com.sinlei.mcp.dto.PaymentStatusDTO;
import com.sinlei.mcp.dto.PolicyFundDTO;
import com.sinlei.mcp.dto.PolicyInfoDTO;
import com.sinlei.mcp.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 保单查询工具类
 * 使用Spring AI的Function Calling机制，供AI模型调用保单相关查询
 *
 * 工具说明：
 * 1. getPolicyList - 查询用户所有保单列表
 * 2. getPolicyDetail - 查询保单详情
 * 3. getClaimRecords - 查询理赔记录
 * 4. getPaymentStatus - 查询缴费状态
 * 5. getPolicyFund - 查询现金价值
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyQueryTool {

    private final PolicyService policyService;

    /**
     * 查询用户保单列表
     * 当用户询问"我有哪些保单"、"查看我的保单"时调用此工具
     */
    @Tool(name = "get_policy_list", description = "根据用户ID查询该用户的所有保单列表，返回保单号、产品名称、保费金额、保单状态等基本信息。当用户询问自己有哪些保单时使用此工具。")
    public List<PolicyInfoDTO> getPolicyList(
            @ToolParam(description = "用户ID，用于查询该用户的所有保单") String userId) {
        log.info("Function Calling: getPolicyList, userId={}", userId);
        return policyService.getPolicyList(userId);
    }

    /**
     * 查询保单详情
     * 当用户询问具体某张保单的详细信息时调用
     */
    @Tool(name = "get_policy_detail", description = "根据保单号查询保单的详细信息，包括投保人、被保人、保费、保障金额等。当用户询问具体保单的详细信息时使用此工具。")
    public PolicyInfoDTO getPolicyDetail(
            @ToolParam(description = "保单号，用于查询指定保单的详细信息") String policyNo) {
        log.info("Function Calling: getPolicyDetail, policyNo={}", policyNo);
        return policyService.getPolicyDetail(policyNo);
    }

    /**
     * 查询理赔记录
     * 当用户询问保单的理赔情况时调用
     */
    @Tool(name = "get_claim_records", description = "根据保单号查询该保单的理赔记录，包括理赔申请号、理赔类型、状态、赔付金额等。当用户询问保单的理赔情况时使用此工具。")
    public List<ClaimRecordDTO> getClaimRecords(
            @ToolParam(description = "保单号，用于查询指定保单的理赔记录") String policyNo) {
        log.info("Function Calling: getClaimRecords, policyNo={}", policyNo);
        return policyService.getClaimRecords(policyNo);
    }

    /**
     * 查询缴费状态
     * 当用户询问保单的缴费情况时调用
     */
    @Tool(name = "get_payment_status", description = "根据保单号查询该保单的缴费状态，包括应缴日期、实缴日期、缴费状态、逾期天数等。当用户询问保单的缴费情况时使用此工具。")
    public PaymentStatusDTO getPaymentStatus(
            @ToolParam(description = "保单号，用于查询指定保单的缴费状态") String policyNo) {
        log.info("Function Calling: getPaymentStatus, policyNo={}", policyNo);
        return policyService.getPaymentStatus(policyNo);
    }

    /**
     * 查询现金价值
     * 当用户询问保单的现金价值时调用
     */
    @Tool(name = "get_policy_fund", description = "根据保单号查询该保单的现金价值信息，包括当前现金价值、累计红利等。当用户询问保单的现金价值或退保金时使用此工具。")
    public PolicyFundDTO getPolicyFund(
            @ToolParam(description = "保单号，用于查询指定保单的现金价值") String policyNo) {
        log.info("Function Calling: getPolicyFund, policyNo={}", policyNo);
        return policyService.getPolicyFund(policyNo);
    }
}

package com.sinlei.mcp.service;

import com.sinlei.mcp.dto.ClaimRecordDTO;
import com.sinlei.mcp.dto.PaymentStatusDTO;
import com.sinlei.mcp.dto.PaymentPeriodDTO;
import com.sinlei.mcp.dto.PolicyFundDTO;
import com.sinlei.mcp.dto.PolicyInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 保单业务服务
 * 负责查询保单相关信息
 *
 * 功能说明：
 * 1. 根据用户ID查询保单列表
 * 2. 根据保单号查询保单详情
 * 3. 根据保单号查询理赔记录
 * 4. 根据保单号查询缴费状态
 * 5. 根据保单号查询现金价值
 *
 * TODO: 后续需要对接外部保单系统API或Oracle数据库
 * 当前实现为模拟数据，仅供开发测试使用
 */
@Slf4j
@Service
public class PolicyService {

    /**
     * 根据用户ID查询保单列表
     * 返回指定用户的所有有效保单信息
     *
     * @param userId 用户ID
     * @return 保单列表
     */
    public List<PolicyInfoDTO> getPolicyList(String userId) {
        log.info("查询用户保单列表: userId={}", userId);

        // TODO: 调用外部API查询保单列表
        // 示例实现:
        // String url = externalApiBaseUrl + "/api/policy/list?userId=" + userId;
        // return restTemplate.getForObject(url, new ParameterizedTypeReference<List<PolicyInfoDTO>>() {});

        // 模拟返回数据（开发测试用）
        List<PolicyInfoDTO> policies = new ArrayList<>();

        PolicyInfoDTO policy1 = new PolicyInfoDTO();
        policy1.setPolicyNo("POL" + userId + "001");
        policy1.setProductCode("00703");
        policy1.setProductName("长城白马关两全保险（互联网专属）");
        policy1.setPolicyHolderName("张三");
        policy1.setInsuredName("张三");
        policy1.setPremium(10000.00);
        policy1.setPaymentFrequency("年缴");
        policy1.setPolicyStatus("有效");
        policy1.setIssueDate("2024-01-01");
        policy1.setCoverageAmount(150000.00);
        policy1.setPaymentDueDate("2025-01-01");
        policies.add(policy1);

        PolicyInfoDTO policy2 = new PolicyInfoDTO();
        policy2.setPolicyNo("POL" + userId + "002");
        policy2.setProductCode("00610");
        policy2.setProductName("长城白马关两全保险（互联网专属）");
        policy2.setPolicyHolderName("张三");
        policy2.setInsuredName("李四");
        policy2.setPremium(5000.00);
        policy2.setPaymentFrequency("年缴");
        policy2.setPolicyStatus("有效");
        policy2.setIssueDate("2024-06-01");
        policy2.setCoverageAmount(100000.00);
        policy2.setPaymentDueDate("2025-06-01");
        policies.add(policy2);

        return policies;
    }

    /**
     * 根据保单号查询保单详情
     * 返回指定保单的完整信息
     *
     * @param policyNo 保单号
     * @return 保单详情
     */
    public PolicyInfoDTO getPolicyDetail(String policyNo) {
        log.info("查询保单详情: policyNo={}", policyNo);

        // TODO: 调用外部API查询保单详情
        // 示例实现:
        // String url = externalApiBaseUrl + "/api/policy/" + policyNo;
        // return restTemplate.getForObject(url, PolicyInfoDTO.class);

        // 模拟返回数据（开发测试用）
        PolicyInfoDTO policy = new PolicyInfoDTO();
        policy.setPolicyNo(policyNo);
        policy.setProductCode("00703");
        policy.setProductName("长城白马关两全保险（互联网专属）");
        policy.setPolicyHolderName("张三");
        policy.setInsuredName("张三");
        policy.setPremium(10000.00);
        policy.setPaymentFrequency("年缴");
        policy.setPolicyStatus("有效");
        policy.setIssueDate("2024-01-01");
        policy.setCoverageAmount(150000.00);
        policy.setPaymentDueDate("2025-01-01");

        return policy;
    }

    /**
     * 根据保单号查询理赔记录
     * 返回指定保单的所有理赔申请记录
     *
     * @param policyNo 保单号
     * @return 理赔记录列表
     */
    public List<ClaimRecordDTO> getClaimRecords(String policyNo) {
        log.info("查询理赔记录: policyNo={}", policyNo);

        // TODO: 调用外部API查询理赔记录
        // 示例实现:
        // String url = externalApiBaseUrl + "/api/claim/list?policyNo=" + policyNo;
        // return restTemplate.getForObject(url, new ParameterizedTypeReference<List<ClaimRecordDTO>>() {});

        // 模拟返回数据（开发测试用）
        List<ClaimRecordDTO> claims = new ArrayList<>();

        ClaimRecordDTO claim1 = new ClaimRecordDTO();
        claim1.setClaimNo("CLM" + policyNo + "001");
        claim1.setPolicyNo(policyNo);
        claim1.setClaimType("医疗");
        claim1.setClaimStatus("已结案");
        claim1.setApplyAmount(5000.00);
        claim1.setPaidAmount(4500.00);
        claim1.setApplyDate("2024-03-15");
        claim1.setSettleDate("2024-03-25");
        claim1.setConclusion("审核通过，按合同约定赔付4500元");
        claims.add(claim1);

        return claims;
    }

    /**
     * 根据保单号查询缴费状态
     * 返回指定保单的保费缴纳状态
     *
     * @param policyNo 保单号
     * @return 缴费状态信息
     */
    public PaymentStatusDTO getPaymentStatus(String policyNo) {
        log.info("查询缴费状态: policyNo={}", policyNo);

        // TODO: 调用外部API查询缴费状态
        // 示例实现:
        // String url = externalApiBaseUrl + "/api/payment/status?policyNo=" + policyNo;
        // return restTemplate.getForObject(url, PaymentStatusDTO.class);

        // 模拟返回数据（开发测试用）
        PaymentStatusDTO paymentStatus = new PaymentStatusDTO();
        paymentStatus.setPolicyNo(policyNo);
        paymentStatus.setDueDate("2025-01-01");
        paymentStatus.setPaidDate("2024-12-28");
        paymentStatus.setPaymentStatus("已缴");
        paymentStatus.setDueAmount(10000.00);
        paymentStatus.setPaidAmount(10000.00);
        paymentStatus.setOverdueDays(0);

        // 缴费期数信息
        List<PaymentPeriodDTO> periods = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            PaymentPeriodDTO period = new PaymentPeriodDTO();
            period.setPeriod(i);
            period.setDueDate("202" + (4 + i) + "-01-01");
            period.setPaidDate("202" + (4 + i) + "-01-01");
            period.setStatus("已缴");
            period.setAmount(10000.00);
            periods.add(period);
        }
        paymentStatus.setPaymentPeriods(periods);

        return paymentStatus;
    }

    /**
     * 根据保单号查询现金价值
     * 返回指定保单的现金价值信息
     *
     * @param policyNo 保单号
     * @return 现金价值信息
     */
    public PolicyFundDTO getPolicyFund(String policyNo) {
        log.info("查询现金价值: policyNo={}", policyNo);

        // TODO: 调用外部API查询现金价值
        // 示例实现:
        // String url = externalApiBaseUrl + "/api/policy/fund?policyNo=" + policyNo;
        // return restTemplate.getForObject(url, PolicyFundDTO.class);

        // 模拟返回数据（开发测试用）
        PolicyFundDTO fund = new PolicyFundDTO();
        fund.setPolicyNo(policyNo);
        fund.setCurrentFundValue(15000.00);
        fund.setAccumulatedBonus(2000.00);
        fund.setFundYear(1);
        fund.setFundDescription("第一年末现金价值为15000元，累计红利2000元");

        return fund;
    }
}

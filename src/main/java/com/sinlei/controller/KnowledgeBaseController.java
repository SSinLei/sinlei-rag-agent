package com.sinlei.controller;

import com.sinlei.common.Result;
import com.sinlei.common.dto.KnowledgeBaseListRequest;
import com.sinlei.common.entity.KnowledgeBase;
import com.sinlei.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/rag/knowledge")
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "PDF上传和知识库管理接口")
@Slf4j
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @PostMapping("/upload")
    @Operation(summary = "上传PDF并向量化存储", description = "上传PDF文件，解析并向量化存储到知识库")
    public Result<KnowledgeBase> uploadPdf(
            @Parameter(description = "PDF文件") @RequestPart("file") MultipartFile file,
            @Parameter(description = "用户ID") @RequestParam("userId") String userId,
            @Parameter(description = "险种编码") @RequestParam("productCode") String productCode,
            @Parameter(description = "产品名称") @RequestParam("productName") String productName,
            @Parameter(description = "条款类型") @RequestParam("clauseType") String clauseType) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                return Result.error("只能上传PDF文件");
            }

            KnowledgeBase knowledgeBase = knowledgeBaseService.uploadPdf(
                file,
                userId,
                productCode,
                productName,
                clauseType
            );
            return Result.success(knowledgeBase);
        } catch (Exception e) {
            log.error("上传PDF失败: {}", e.getMessage());
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户的知识库列表", description = "查询指定用户的所有知识库记录")
    public Result<List<KnowledgeBase>> listByUser(
            @Parameter(description = "用户ID", required = true) @RequestParam String userId) {
        List<KnowledgeBase> list = knowledgeBaseService.listByUser(userId);
        return Result.success(list);
    }

    @PostMapping("/list")
    @Operation(summary = "根据险种编码获取知识库列表", description = "查询指定险种的知识库记录")
    public Result<List<KnowledgeBase>> listByProduct(
            @Parameter(description = "查询请求") @RequestBody KnowledgeBaseListRequest request) {
        List<KnowledgeBase> list = knowledgeBaseService.listByProduct(request.getProductCode());
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除知识库", description = "删除指定的知识库记录及相关向量数据")
    public Result<Void> delete(
            @Parameter(description = "知识库ID", required = true) @PathVariable String id) {
        knowledgeBaseService.deleteById(id);
        return Result.success(null);
    }
}

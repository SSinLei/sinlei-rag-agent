package com.sinlei.rag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinlei.rag.dto.TextChunk;
import com.sinlei.rag.entity.KnowledgeBase;
import com.sinlei.rag.entity.KnowledgeChunks;
import com.sinlei.rag.mapper.KnowledgeBaseMapper;
import com.sinlei.rag.mapper.KnowledgeChunksMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 知识库服务
 * 负责知识库的PDF上传、存储和管理
 *
 * 功能说明：
 * 1. PDF文件上传和本地存储
 * 2. PDF解析和文本分块
 * 3. 文本向量化存储
 * 4. 知识库列表查询和删除
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final KnowledgeChunksMapper knowledgeChunksMapper;
    private final PdfParseService pdfParseService;
    private final EmbeddingService embeddingService;
    private final IntelligentChunkingService intelligentChunkingService;

    @Value("${rag.file.storage.path:./uploads}")
    private String storagePath;

    @Value("${rag.chunking.intelligent-enabled:false}")
    private boolean intelligentChunkingEnabled;

    @PostConstruct
    public void init() {
        File path = new File(storagePath);
        if (!path.isAbsolute()) {
            storagePath = new File(System.getProperty("user.dir"), storagePath).getAbsolutePath();
        }
        log.info("文件存储路径: {}", storagePath);
    }

    /**
     * 上传PDF并向量化存储
     * 完整的知识入库流程：
     * 1. 保存PDF文件到本地
     * 2. 解析PDF获取文本块
     * 3. 向量化每个文本块
     * 4. 存储到数据库
     *
     * @param file PDF文件
     * @param userId 用户ID
     * @param productCode 险种编码
     * @param productName 产品名称
     * @param clauseType 条款类型
     * @return 知识库主表记录
     * @throws IOException 文件处理异常
     */
    @Transactional(rollbackFor = Exception.class)
    public KnowledgeBase uploadPdf(MultipartFile file, String userId, String productCode,
                                   String productName, String clauseType) throws Exception {
        log.info("开始上传PDF: userId={}, productCode={}", userId, productCode);

        // 1. 创建用户存储目录
        File storageDir = new File(storagePath + File.separator + userId);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // 2. 生成唯一文件名并保存文件
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : ".pdf";
        String fileName = productCode + "_" + UUID.randomUUID() + extension;
        File destFile = new File(storageDir, fileName);
        file.transferTo(destFile);
        log.info("PDF文件保存成功: {}", destFile.getAbsolutePath());

        // 3. 创建知识库主表记录
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setUserId(userId);
        knowledgeBase.setProductCode(productCode);
        knowledgeBase.setProductName(productName);
        knowledgeBase.setClauseType(clauseType);
        knowledgeBase.setDocumentName(originalFilename);
        knowledgeBase.setSourceFileUrl(destFile.getAbsolutePath());
        knowledgeBaseMapper.insert(knowledgeBase);

        List<TextChunk> chunks;
        if (intelligentChunkingEnabled) {
            log.info("使用智能切片模式");
            chunks = intelligentChunkingService.intelligentChunking(destFile, productCode);
        } else {
            log.info("使用传统切片模式");
            chunks = pdfParseService.parsePdf(destFile);
        }
        log.info("PDF解析完成，共 {} 个文本块", chunks.size());

        // 5. 向量化并存储每个文本块
        for (TextChunk chunk : chunks) {
            KnowledgeChunks knowledgeChunk = new KnowledgeChunks();
            knowledgeChunk.setKnowledgeBaseId(knowledgeBase.getId());
            knowledgeChunk.setUserId(userId);
            knowledgeChunk.setProductCode(productCode);
            knowledgeChunk.setChapterTitle(chunk.getChapterTitle());
            knowledgeChunk.setChunkText(chunk.getContent());
            knowledgeChunk.setChunkIndex(chunk.getChunkIndex());
            knowledgeChunk.setPageNumber(chunk.getPageNumber());

            // 向量化文本
            float[] embedding = embeddingService.embedText(chunk.getContent());
            knowledgeChunk.setEmbedding(embedding);

            knowledgeChunksMapper.insert(knowledgeChunk);
        }

        log.info("PDF上传和向量化完成: productCode={}, chunks={}", productCode, chunks.size());
        return knowledgeBase;
    }

    /**
     * 获取用户知识库列表
     *
     * @param userId 用户ID
     * @return 知识库列表（按创建时间倒序）
     */
    public List<KnowledgeBase> listByUser(String userId) {
        log.debug("查询用户知识库列表: userId={}", userId);
        return knowledgeBaseMapper.selectList(
            new LambdaQueryWrapper<KnowledgeBase>()
                .eq(KnowledgeBase::getUserId, userId)
                .orderByDesc(KnowledgeBase::getCreatedAt)
        );
    }

    /**
     * 根据险种编码获取知识库列表
     *
     * @param productCode 险种编码
     * @return 知识库列表
     */
    public List<KnowledgeBase> listByProduct(String productCode) {
        log.debug("查询知识库列表: productCode={}", productCode);
        return knowledgeBaseMapper.selectList(
            new LambdaQueryWrapper<KnowledgeBase>()
                .eq(KnowledgeBase::getProductCode, productCode)
                .orderByDesc(KnowledgeBase::getCreatedAt)
        );
    }

    /**
     * 根据用户ID和险种编码获取知识库列表
     *
     * @param userId 用户ID
     * @param productCode 险种编码
     * @return 知识库列表
     */
    public List<KnowledgeBase> listByUserAndProduct(String userId, String productCode) {
        log.debug("查询用户知识库列表: userId={}, productCode={}", userId, productCode);
        return knowledgeBaseMapper.selectList(
            new LambdaQueryWrapper<KnowledgeBase>()
                .eq(KnowledgeBase::getUserId, userId)
                .eq(KnowledgeBase::getProductCode, productCode)
                .orderByDesc(KnowledgeBase::getCreatedAt)
        );
    }

    /**
     * 删除知识库
     * 同时删除主表记录、所有知识块、以及本地PDF文件
     *
     * @param id 知识库ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        log.info("删除知识库: id={}", id);

        // 查询主表记录
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb != null) {
            // 删除关联的知识块
            knowledgeChunksMapper.delete(
                new LambdaQueryWrapper<KnowledgeChunks>()
                    .eq(KnowledgeChunks::getKnowledgeBaseId, id)
            );

            // 删除本地PDF文件
            File file = new File(kb.getSourceFileUrl());
            if (file.exists()) {
                file.delete();
                log.info("删除PDF文件: {}", kb.getSourceFileUrl());
            }

            // 删除主表记录
            knowledgeBaseMapper.deleteById(id);
            log.info("知识库删除完成: id={}", id);
        }
    }
}

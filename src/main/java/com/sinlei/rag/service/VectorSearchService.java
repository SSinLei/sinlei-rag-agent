package com.sinlei.rag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sinlei.rag.entity.KnowledgeChunks;
import com.sinlei.rag.mapper.KnowledgeChunksMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 向量检索服务
 * 负责在知识库中进行向量相似度搜索
 *
 * 功能说明：
 * 1. 将用户查询转换为向量
 * 2. 在知识库中检索相似文本块
 * 3. 支持按险种编码过滤
 * 4. 返回TopK个最相似的文本块
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSearchService {

    /**
     * 知识块Mapper
     */
    private final KnowledgeChunksMapper knowledgeChunksMapper;

    /**
     * 向量化服务
     */
    private final EmbeddingService embeddingService;

    /**
     * 检索相似文本块（无险种过滤）
     *
     * @param userId 用户ID（用于数据隔离）
     * @param query 用户查询文本
     * @param topK 返回前K个最相似的文本块
     * @return 相似文本块列表
     */
    public List<KnowledgeChunks> searchSimilar(String userId, String query, int topK) {
        return searchSimilar(userId, null, query, topK);
    }

    /**
     * 检索相似文本块（支持险种过滤）
     * 实现步骤：
     * 1. 将用户查询转换为向量
     * 2. 查询用户知识库（可指定险种）
     * 3. 计算每个文本块与查询的余弦相似度
     * 4. 排序并返回TopK个结果
     *
     * @param userId 用户ID（用于数据隔离）
     * @param productCode 险种编码（可选，为空则搜索全部）
     * @param query 用户查询文本
     * @param topK 返回前K个最相似的文本块
     * @return 相似文本块列表
     */
    public List<KnowledgeChunks> searchSimilar(String userId, String productCode, String query, int topK) {
        log.info("开始向量检索: userId={}, productCode={}, query={}", userId, productCode, query);

        // 1. 将用户查询转换为向量
        float[] queryEmbedding = embeddingService.embedText(query);

        // 2. 构建查询条件
        LambdaQueryWrapper<KnowledgeChunks> wrapper = new LambdaQueryWrapper<KnowledgeChunks>()
            .eq(KnowledgeChunks::getUserId, userId)
            .orderByAsc(KnowledgeChunks::getProductCode)
            .last("LIMIT 500");

        // 3. 如果指定了险种编码，则添加过滤条件
        if (productCode != null && !productCode.isEmpty()) {
            wrapper.eq(KnowledgeChunks::getProductCode, productCode);
        }

        // 4. 查询所有符合条件的知识块
        List<KnowledgeChunks> allChunks = knowledgeChunksMapper.selectList(wrapper);
        log.debug("检索到 {} 条知识块", allChunks.size());

        // 5. 计算余弦相似度并排序
        for (KnowledgeChunks chunk : allChunks) {
            Object embeddingObj = chunk.getEmbedding();
            float[] chunkEmbedding;

            // 处理不同类型的向量数据
            if (embeddingObj instanceof float[]) {
                chunkEmbedding = (float[]) embeddingObj;
            } else if (embeddingObj instanceof Float[]) {
                Float[] floatArray = (Float[]) embeddingObj;
                chunkEmbedding = new float[floatArray.length];
                for (int i = 0; i < floatArray.length; i++) {
                    chunkEmbedding[i] = floatArray[i];
                }
            } else {
                continue;
            }

            // 计算余弦相似度
            double similarity = cosineSimilarity(queryEmbedding, chunkEmbedding);
            // 临时存储相似度（乘以10000以便后续排序）
            chunk.setPageNumber((int) (similarity * 10000));
        }

        // 6. 排序并返回TopK结果
        List<KnowledgeChunks> result = allChunks.stream()
            .filter(c -> c.getPageNumber() > 0)
            .sorted((a, b) -> Integer.compare(b.getPageNumber(), a.getPageNumber()))
            .limit(topK)
            .collect(Collectors.toList());

        log.info("向量检索完成，返回 {} 条结果", result.size());
        return result;
    }

    /**
     * 计算余弦相似度
     * 用于衡量两个向量之间的相似程度
     *
     * @param a 向量A
     * @param b 向量B
     * @return 余弦相似度值（-1到1之间，1表示完全相似）
     */
    private double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            return 0;
        }

        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) {
            return 0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

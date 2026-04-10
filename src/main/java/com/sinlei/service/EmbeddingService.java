package com.sinlei.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 向量化服务
 * 负责将文本转换为向量embedding
 *
 * 使用阿里百炼的text-embedding-v3模型
 * 将文本转换为1024维向量，用于向量相似度检索
 */
@Slf4j
@Service
public class EmbeddingService {

    /**
     * Embedding模型
     * 注入阿里百炼的embedding模型
     */
    private final EmbeddingModel embeddingModel;

    /**
     * 构造函数
     *
     * @param embeddingModel 注入的embedding模型
     */
    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * 单文本向量化
     * 将单个文本转换为向量
     *
     * @param text 待向量化的文本
     * @return 向量数组（float[]），维度为1024
     */
    public float[] embedText(String text) {
        log.debug("开始向量化文本，长度: {} 字符", text.length());

        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        float[] result = response.getResult().getOutput();

        log.debug("向量化完成，向量维度: {}", result.length);
        return result;
    }

    /**
     * 批量文本向量化
     * 将多个文本批量转换为向量
     *
     * @param texts 待向量化的文本列表
     * @return 向量数组列表
     */
    public List<float[]> embedTexts(List<String> texts) {
        log.debug("开始批量向量化，文本数量: {}", texts.size());

        EmbeddingResponse response = embeddingModel.embedForResponse(texts);
        List<float[]> results = response.getResults().stream()
                .map(result -> result.getOutput())
                .toList();

        log.debug("批量向量化完成");
        return results;
    }
}

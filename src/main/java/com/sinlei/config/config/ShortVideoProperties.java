package com.sinlei.config.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短视频工坊配置，前缀 {@code shortvideo}，见 {@code application.yml}。
 */
@Data
@Component
@ConfigurationProperties(prefix = "shortvideo")
public class ShortVideoProperties {

    /** 热点检索默认模式等 */
    private Search search = new Search();
    /** 口播时长估算相关 */
    private Duration duration = new Duration();
    /** 提示词与检索条数 */
    private Prompt prompt = new Prompt();
    /** 真实联网检索 HTTP 配置 */
    private RealSearch realSearch = new RealSearch();

    @Data
    public static class Search {
        /** mock | real */
        private String mode = "mock";
    }

    @Data
    public static class Duration {
        /** 估算语速：汉字/秒，用于简单字数校准 */
        private double speechRate = 3.8;
    }

    @Data
    public static class Prompt {
        /** 风格样稿拼接条数上限 */
        private int styleRetrievalTopK = 3;
    }

    @Data
    public static class RealSearch {
        /** GET 请求完整 base URL（可带 query 由代码追加） */
        private String endpoint = "https://hn.algolia.com/api/v1/search";
        /** 预留：当前 RestClient 未强制使用，可后续接超时工厂 */
        private int timeoutMs = 3000;
    }
}

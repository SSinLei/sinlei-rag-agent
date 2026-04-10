package com.sinlei.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinlei.config.config.ShortVideoProperties;
import com.sinlei.common.model.HotspotItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 真实联网热点（MVP）：请求配置的 HTTP 接口（默认 Hacker News Algolia），解析 JSON 中的标题列表。
 * 失败或结果为空时返回空列表，由 {@link HotSearchRouter} 决定是否回退 mock。
 */
@Slf4j
@Service("realHotSearchService")
@RequiredArgsConstructor
public class RealHotSearchService implements HotSearchService {

    private final ShortVideoProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public List<HotspotItem> search(String topic) {
        try {
            RestClient restClient = RestClient.builder().build();
            String url = UriComponentsBuilder.fromHttpUrl(properties.getRealSearch().getEndpoint())
                .queryParam("query", topic)
                .queryParam("tags", "story")
                .toUriString();
            String body = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

            JsonNode root = objectMapper.readTree(body);
            JsonNode hits = root.path("hits");
            List<HotspotItem> items = new ArrayList<>();
            for (JsonNode hit : hits) {
                String title = hit.path("title").asText("");
                if (!title.isBlank()) {
                    items.add(new HotspotItem(title, "hn-algolia"));
                }
                if (items.size() >= 5) {
                    break;
                }
            }
            if (!items.isEmpty()) {
                return items;
            }
        } catch (Exception e) {
            log.warn("Real search failed, fallback to empty list: {}", e.getMessage());
        }
        return List.of();
    }
}

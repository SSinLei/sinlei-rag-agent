package com.sinlei.service;

import com.sinlei.config.config.ShortVideoProperties;
import com.sinlei.common.model.HotspotItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 热点检索路由：按模式选择 mock 或 real 实现；real 无结果时回退 mock，保证演示可用。
 */
@Service
@RequiredArgsConstructor
public class HotSearchRouter {

    @Qualifier("mockHotSearchService")
    private final HotSearchService mockHotSearchService;
    @Qualifier("realHotSearchService")
    private final HotSearchService realHotSearchService;
    private final ShortVideoProperties properties;

    /**
     * @param topic 检索关键词，通常与视频主题一致
     * @param mode  mock|real；空则读 {@link ShortVideoProperties.Search#getMode}
     */
    public List<HotspotItem> search(String topic, String mode) {
        String finalMode = (mode == null || mode.isBlank()) ? properties.getSearch().getMode() : mode;
        if ("real".equalsIgnoreCase(finalMode)) {
            List<HotspotItem> real = realHotSearchService.search(topic);
            if (!real.isEmpty()) {
                return real;
            }
        }
        return mockHotSearchService.search(topic);
    }
}

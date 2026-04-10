package com.sinlei.service;

import com.sinlei.common.model.HotspotItem;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Mock 热点：不依赖外网，用于本地开发与 real 失败时的兜底。
 */
@Service("mockHotSearchService")
public class MockHotSearchService implements HotSearchService {
    @Override
    public List<HotspotItem> search(String topic) {
        return List.of(
            new HotspotItem(topic + " 真香还是智商税？", "mock-trend"),
            new HotspotItem("网友争议：" + topic + " 到底值不值？", "mock-social"),
            new HotspotItem(topic + " 最容易踩坑的3个细节", "mock-community")
        );
    }
}

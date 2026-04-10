package com.sinlei.service;

import com.sinlei.common.model.HotspotItem;

import java.util.List;

/**
 * 热点检索抽象：策划阶段注入「时效/争议」类标题摘要。
 */
public interface HotSearchService {
    /**
     * @param topic 主题或查询词
     * @return 热点条目列表（标题 + 来源标识）
     */
    List<HotspotItem> search(String topic);
}

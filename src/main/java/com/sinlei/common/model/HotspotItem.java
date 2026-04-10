package com.sinlei.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 热点检索返回的一条摘要（标题 + 来源标识）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotspotItem {
    private String title;
    private String source;
}

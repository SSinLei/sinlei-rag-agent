package com.sinlei.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 表 {@code shortvideo_project}：短视频脚本项目主记录（业务 projectId 唯一）。
 */
@Data
@TableName("shortvideo_project")
public class ShortVideoProjectEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("project_id")
    private String projectId;

    @TableField("user_id")
    private String userId;

    @TableField("topic")
    private String topic;

    @TableField("persona")
    private String persona;

    @TableField("title")
    private String title;

    @TableField("duration_sec")
    private Integer durationSec;

    @TableField("selected_hook")
    private String selectedHook;

    @TableField("hooks_json")
    private String hooksJson;

    @TableField("script_text")
    private String scriptText;

    @TableField("search_mode")
    private String searchMode;

    @TableField("speech_rate")
    private BigDecimal speechRate;

    @TableField("style_hint")
    private String styleHint;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

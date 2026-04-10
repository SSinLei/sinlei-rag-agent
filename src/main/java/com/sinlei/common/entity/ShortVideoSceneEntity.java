package com.sinlei.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表 {@code shortvideo_scene}：分镜明细，通过 {@code project_id} 关联项目。
 */
@Data
@TableName("shortvideo_scene")
public class ShortVideoSceneEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("project_id")
    private String projectId;

    @TableField("scene_no")
    private Integer sceneNo;

    @TableField("visual_prompt_en")
    private String visualPromptEn;

    @TableField("voiceover_cn")
    private String voiceoverCn;

    @TableField("emotion_tag")
    private String emotionTag;

    @TableField("est_duration_sec")
    private Integer estDurationSec;

    @TableField("created_at")
    private LocalDateTime createdAt;
}

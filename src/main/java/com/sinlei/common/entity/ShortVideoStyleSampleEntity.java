package com.sinlei.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表 {@code shortvideo_style_sample}：人设样稿，供撰写前风格检索。
 */
@Data
@TableName("shortvideo_style_sample")
public class ShortVideoStyleSampleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("persona")
    private String persona;

    @TableField("sample_text")
    private String sampleText;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

-- ==========================================
-- 知识库向量存储表结构设计
-- 数据库: PostgreSQL + PGVector
-- 向量维度: 1024
-- ==========================================

-- 启用PGVector扩展
CREATE EXTENSION IF NOT EXISTS "vector";

-- 删除已存在的表（如果需要重建）
-- DROP TABLE IF EXISTS knowledge_chunks CASCADE;
-- DROP TABLE IF EXISTS knowledge_base CASCADE;
-- DROP TABLE IF EXISTS conversation_history CASCADE;

-- ==========================================
-- 表1: 知识库主表
-- 用于存储上传的PDF文档基本信息
-- ==========================================
CREATE TABLE knowledge_base (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    product_code VARCHAR(6) NOT NULL,
    product_name VARCHAR(255),
    clause_type VARCHAR(32),
    document_name VARCHAR(255) NOT NULL,
    source_file_url VARCHAR(512) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE knowledge_base IS '知识库主表，存储用户上传的PDF文档基本信息';
COMMENT ON COLUMN knowledge_base.user_id IS '用户ID，用于数据隔离';
COMMENT ON COLUMN knowledge_base.product_code IS '险种编码，6位数产品唯一标识';
COMMENT ON COLUMN knowledge_base.product_name IS '产品名称';
COMMENT ON COLUMN knowledge_base.clause_type IS '条款类型（主险/附加险）';
COMMENT ON COLUMN knowledge_base.document_name IS '文档原始名称';
COMMENT ON COLUMN knowledge_base.source_file_url IS '源文件本地存储路径';
COMMENT ON COLUMN knowledge_base.created_at IS '创建时间';
COMMENT ON COLUMN knowledge_base.updated_at IS '更新时间';

CREATE INDEX idx_knowledge_base_user_id ON knowledge_base(user_id);
CREATE INDEX idx_knowledge_base_product_code ON knowledge_base(product_code);

-- ==========================================
-- 表2: 知识块表
-- 存储分割后的文本块及对应的向量数据
-- ==========================================
CREATE TABLE knowledge_chunks (
    id BIGSERIAL PRIMARY KEY,
    knowledge_base_id BIGINT NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    product_code VARCHAR(6) NOT NULL,
    chapter_title VARCHAR(255),
    chunk_text TEXT NOT NULL,
    chunk_index INT NOT NULL,
    embedding vector(1024) NOT NULL,
    page_number INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE knowledge_chunks IS '知识块表，存储分割后的文本块及对应的向量数据，用于RAG检索';
COMMENT ON COLUMN knowledge_chunks.knowledge_base_id IS '关联主表ID';
COMMENT ON COLUMN knowledge_chunks.user_id IS '用户ID，用于数据隔离';
COMMENT ON COLUMN knowledge_chunks.product_code IS '险种编码，用于高效检索';
COMMENT ON COLUMN knowledge_chunks.chapter_title IS '章节标题';
COMMENT ON COLUMN knowledge_chunks.chunk_text IS '分割后的文本块内容';
COMMENT ON COLUMN knowledge_chunks.chunk_index IS '文本块顺序索引';
COMMENT ON COLUMN knowledge_chunks.embedding IS '向量数据，1024维';
COMMENT ON COLUMN knowledge_chunks.page_number IS '对应PDF页码';
COMMENT ON COLUMN knowledge_chunks.created_at IS '创建时间';

CREATE INDEX idx_knowledge_chunks_user_id ON knowledge_chunks(user_id);
CREATE INDEX idx_knowledge_chunks_product_code ON knowledge_chunks(product_code);
CREATE INDEX idx_knowledge_chunks_kb_id ON knowledge_chunks(knowledge_base_id);
CREATE INDEX idx_knowledge_chunks_embedding ON knowledge_chunks USING ivfflat (embedding vector_cosine_ops);

-- ==========================================
-- 表3: 对话历史表
-- 存储用户与AI的对话历史，支持多轮对话上下文
-- ==========================================
CREATE TABLE conversation_history (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    conversation_id VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    product_code VARCHAR(6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE conversation_history IS '对话历史表，存储用户与AI的对话历史，支持多轮对话上下文';
COMMENT ON COLUMN conversation_history.user_id IS '用户ID';
COMMENT ON COLUMN conversation_history.conversation_id IS '对话会话ID，用于关联多轮对话';
COMMENT ON COLUMN conversation_history.role IS '角色（user/assistant/system）';
COMMENT ON COLUMN conversation_history.content IS '对话内容';
COMMENT ON COLUMN conversation_history.product_code IS '本轮对话关联的险种编码';
COMMENT ON COLUMN conversation_history.created_at IS '创建时间';

CREATE INDEX idx_conversation_history_user_id ON conversation_history(user_id);
CREATE INDEX idx_conversation_history_conv_id ON conversation_history(conversation_id);
CREATE INDEX idx_conversation_history_created_at ON conversation_history(created_at);

-- ==========================================
-- 表4: 短视频脚本项目表
-- 存储一次脚本生成任务的主数据
-- ==========================================
CREATE TABLE shortvideo_project (
    id BIGSERIAL PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL UNIQUE,
    user_id VARCHAR(64),
    topic VARCHAR(255) NOT NULL,
    persona VARCHAR(64),
    title VARCHAR(255) NOT NULL,
    duration_sec INT NOT NULL,
    selected_hook TEXT,
    hooks_json TEXT,
    script_text TEXT,
    search_mode VARCHAR(16),
    speech_rate NUMERIC(8,2),
    style_hint TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE shortvideo_project IS '短视频脚本项目主表';
COMMENT ON COLUMN shortvideo_project.project_id IS '项目唯一ID（业务ID）';
COMMENT ON COLUMN shortvideo_project.hooks_json IS '黄金3秒开头候选JSON数组';
COMMENT ON COLUMN shortvideo_project.script_text IS '完整口播文案';
COMMENT ON COLUMN shortvideo_project.style_hint IS '风格RAG提示摘要';

CREATE INDEX idx_shortvideo_project_user_id ON shortvideo_project(user_id);
CREATE INDEX idx_shortvideo_project_topic ON shortvideo_project(topic);
CREATE INDEX idx_shortvideo_project_created_at ON shortvideo_project(created_at);

-- ==========================================
-- 表5: 短视频分镜表
-- 存储项目对应的分镜明细
-- ==========================================
CREATE TABLE shortvideo_scene (
    id BIGSERIAL PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL,
    scene_no INT NOT NULL,
    visual_prompt_en TEXT,
    voiceover_cn TEXT,
    emotion_tag VARCHAR(32),
    est_duration_sec INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE shortvideo_scene IS '短视频项目分镜明细表';
COMMENT ON COLUMN shortvideo_scene.project_id IS '关联shortvideo_project.project_id';
COMMENT ON COLUMN shortvideo_scene.scene_no IS '分镜序号';

CREATE INDEX idx_shortvideo_scene_project_id ON shortvideo_scene(project_id);
CREATE INDEX idx_shortvideo_scene_scene_no ON shortvideo_scene(scene_no);

-- ==========================================
-- 表6: 短视频风格样稿表
-- 存储用户风格样稿，支持风格化RAG
-- ==========================================
CREATE TABLE shortvideo_style_sample (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(64),
    persona VARCHAR(64) NOT NULL,
    sample_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE shortvideo_style_sample IS '短视频风格样稿表';
COMMENT ON COLUMN shortvideo_style_sample.persona IS '人设风格名称';
COMMENT ON COLUMN shortvideo_style_sample.sample_text IS '风格样稿文本';

CREATE INDEX idx_shortvideo_style_user_persona ON shortvideo_style_sample(user_id, persona);
CREATE INDEX idx_shortvideo_style_created_at ON shortvideo_style_sample(created_at);

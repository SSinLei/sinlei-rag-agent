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

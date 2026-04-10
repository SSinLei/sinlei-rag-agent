# sinlei-rag-agent

Spring Boot 3 + Spring AI Alibaba 示例工程，包含：
- **RAG 知识库问答**（`/api/rag/*`）
- **MCP/Tool Calling** 示例（保单查询工具）
- **AI短视频脚本工坊（MVP）**：从主题 -> 爆款开头 -> 口播脚本 -> 分镜表 -> Markdown/JSON 导出

## 环境要求
- **JDK 21**（`pom.xml` 中 `java.version=21`）
- Maven Wrapper（仓库已包含 `mvnw` / `mvnw.cmd`）

## 启动

```bash
./mvnw spring-boot:run
```

默认地址：
- 服务端口：`8123`
- API 前缀：`/api`
- Swagger UI：`/api/swagger-ui.html`

## PostgreSQL 初始化

本项目使用 PostgreSQL（含 pgvector）。先执行：

```sql
CREATE EXTENSION IF NOT EXISTS "vector";
```

然后执行 `sql/ddl_init.sql`。其中短视频工坊新增了以下持久化表：
- `shortvideo_project`
- `shortvideo_scene`
- `shortvideo_style_sample`

## AI短视频脚本工坊（MVP）

### 1) 打开工作台页面

启动后访问：
- `http://localhost:8123/api/shortvideo-workbench.html`

页面支持：
- 主题输入、人设选择、目标时长
- 热点检索模式切换（`mock` / `real`）
- 生成开头方案、生成完整脚本与分镜
- 一键导出 Markdown / JSON

### 2) 核心接口

#### 生成黄金3秒开头
- `POST /api/shortvideo/hooks`

请求示例：

```json
{
  "topic": "iPhone16测评",
  "persona": "犀利吐槽风",
  "searchMode": "mock"
}
```

#### 生成完整脚本 + 分镜
- `POST /api/shortvideo/generate`

请求示例：

```json
{
  "userId": "creator-001",
  "topic": "iPhone16测评",
  "persona": "犀利吐槽风",
  "durationSec": 60,
  "selectedHook": "你可能刚买的iPhone16，正在被这3个隐藏设置拖慢！",
  "searchMode": "mock"
}
```

响应关键字段：
- `projectId`：用于导出
- `hooks[]`：开头候选
- `scriptText`：完整口播文案
- `scenes[]`：分镜表（`sceneNo / visualPromptEn / voiceoverCn / emotionTag / estDurationSec`）

#### 导出 Markdown / JSON
- `GET /api/shortvideo/{projectId}/export.md`
- `GET /api/shortvideo/{projectId}/export.json`

#### 添加人设样稿（风格化RAG，MVP版）
- `POST /api/shortvideo/styles`

请求示例：

```json
{
  "userId": "creator-001",
  "persona": "温柔知性风",
  "sampleText": "我们慢慢看，先从最关键的一点讲起……"
}
```

### 3) 配置项

在 `src/main/resources/application.yml`：
- `shortvideo.search.mode`: `mock|real`
- `shortvideo.duration.speech-rate`: 估算语速（字/秒）
- `shortvideo.prompt.style-retrieval-top-k`: 风格提示检索 topK
- `shortvideo.real-search.endpoint`: real 模式搜索 endpoint（当前使用 `hn.algolia.com` 演示）

## 测试

```bash
./mvnw test
```

短视频模块的测试位于：
- `src/test/java/com/sinlei/shortvideo/**`

## 验收脚本

见：`docs/shortvideo-mvp-acceptance.md`


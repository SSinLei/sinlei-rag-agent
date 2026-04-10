# AI短视频脚本工坊 MVP 验收脚本

## 1. 启动服务
- 执行：`./mvnw spring-boot:run`
- 打开工作台：`http://localhost:8123/api/shortvideo-workbench.html`

## 2. 开头方案验收
- 输入主题：`iPhone16测评`
- 人设：`犀利吐槽风`
- 时长：`60`
- 热点模式：`mock`（再切换 `real` 复测）
- 点击“生成开头方案”，应返回 3-5 条可选开头。

## 3. 脚本分镜验收
- 选择任意开头，点击“生成完整脚本”。
- 验收点：
  - 返回 `projectId`
  - 口播文案可读、口语化
  - 分镜表字段完整：`sceneNo`、`visualPromptEn`、`voiceoverCn`、`emotionTag`、`estDurationSec`

## 4. 导出验收
- 点击导出 Markdown 与 JSON 按钮。
- 验收点：
  - 接口可下载
  - Markdown 包含分镜表
  - JSON 包含 `title`、`durationSec`、`hooks`、`scenes`

## 5. 样稿RAG验收
- 调用 `POST /api/shortvideo/styles` 添加样稿（同 persona）。
- 重新生成脚本，对比语气变化是否更贴近样稿。

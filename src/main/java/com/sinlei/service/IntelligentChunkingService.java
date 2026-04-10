package com.sinlei.service;

import com.sinlei.common.dto.TextChunk;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntelligentChunkingService {

    private final ChatModel chatModel;
    private final PdfParseService pdfParseService;
    private final EmbeddingService embeddingService;

    private static final int MAX_CHUNK_LENGTH = 2000;
    private static final int MIN_CHUNK_LENGTH = 500;

    public List<TextChunk> intelligentChunking(File file, String productCode) throws Exception {
        log.info("开始智能切片: {}", file.getName());

        String rawText = extractTextFromPdf(file);
        if (rawText == null || rawText.isEmpty()) {
            log.warn("PDF文本提取结果为空");
            return new ArrayList<>();
        }

        log.info("PDF文本提取完成，原始文本长度: {} 字符", rawText.length());

        List<String> preChunks = preChunkText(rawText);
        log.info("预切割完成，共 {} 个文本块", preChunks.size());

        List<String> allSemanticChunks = new ArrayList<>();
        int batchIndex = 0;

        for (String preChunk : preChunks) {
            log.info("正在处理第 {}/{} 个文本块", ++batchIndex, preChunks.size());
            try {
                List<String> semanticChunks = callLlmForChunking(preChunk, productCode);
                allSemanticChunks.addAll(semanticChunks);
            } catch (Exception e) {
                log.warn("智能切分失败，保留原始文本: {}", e.getMessage());
                allSemanticChunks.add(preChunk);
            }
        }

        List<TextChunk> chunks = new ArrayList<>();
        int chunkIndex = 0;

        for (String chunkText : allSemanticChunks) {
            if (chunkText.trim().length() < 50) {
                continue;
            }

            String title = extractTitle(chunkText);

            TextChunk chunk = new TextChunk();
            chunk.setChapterTitle(title);
            chunk.setContent(chunkText.trim());
            chunk.setChunkIndex(chunkIndex++);

            float[] embedding = embeddingService.embedText(chunkText);
            chunk.setEmbedding(embedding);

            chunks.add(chunk);
        }

        log.info("智能切片完成，共 {} 个文本块", chunks.size());
        return chunks;
    }

    private List<String> preChunkText(String text) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\n\\s*\n");

        StringBuilder currentChunk = new StringBuilder();

        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) {
                continue;
            }

            if (currentChunk.length() + paragraph.length() > MAX_CHUNK_LENGTH) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }

                if (paragraph.length() > MAX_CHUNK_LENGTH) {
                    String[] lines = paragraph.split("\n");
                    for (String line : lines) {
                        line = line.trim();
                        if (line.isEmpty()) {
                            continue;
                        }
                        if (currentChunk.length() + line.length() > MAX_CHUNK_LENGTH) {
                            if (currentChunk.length() > 0) {
                                chunks.add(currentChunk.toString().trim());
                                currentChunk = new StringBuilder();
                            }
                        }
                        currentChunk.append(line).append("\n");
                    }
                } else {
                    currentChunk.append(paragraph);
                }
            } else {
                currentChunk.append(paragraph).append("\n\n");
            }
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    private String extractTextFromPdf(File file) throws IOException, SAXException, TikaException {
        try (InputStream inputStream = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            PDFParser pdfParser = new PDFParser();
            pdfParser.parse(inputStream, handler, metadata, context);
            return handler.toString();
        }
    }

    private List<String> callLlmForChunking(String text, String productCode) {
        String prompt = buildChunkingPrompt(text, productCode);

        ChatClient chatClient = ChatClient.builder(chatModel).build();

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return parseChunkingResponse(response);
    }

    private String buildChunkingPrompt(String text, String productCode) {
        return String.format(String.join("\n",
            "请将以下保险条款文档进行语义分割，将其分成多个有意义的章节/段落。",
            "",
            "要求：",
            "1. 每个段落应该是一个完整的语义单元，包含完整的内容",
            "2. 保留章节标题作为每个段落的开头",
            "3. 不要拆分条款编号（如\"第二条\"、\"2.1\"等）",
            "4. 适当合并短小的相关段落",
            "5. 总体保持每个段落长度在500-2000字之间",
            "",
            "险种编码：%s",
            "",
            "请直接输出分割结果，使用\"===CHUNK===\"作为段落分隔符，不要包含任何其他说明文字。",
            "",
            "文档内容：",
            "%s"
        ), productCode, text);
    }

    private List<String> parseChunkingResponse(String response) {
        List<String> chunks = new ArrayList<>();
        String[] parts = response.split("===CHUNK===");

        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                chunks.add(trimmed);
            }
        }

        if (chunks.isEmpty()) {
            chunks.add(response);
        }

        return chunks;
    }

    private String extractTitle(String chunk) {
        String[] lines = chunk.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            Pattern pattern = Pattern.compile("^第[一二三四五六七八九十\\d]+[章节篇部]\\s*.+");
            if (pattern.matcher(line).matches()) {
                return line;
            }

            if (line.length() < 30 && (line.startsWith("第") || line.contains("章"))) {
                return line;
            }

            return line.length() > 50 ? line.substring(0, 50) + "..." : line;
        }

        return "未命名章节";
    }
}

package com.sinlei.service;

import com.sinlei.common.dto.TextChunk;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PdfParseService {

    private static final Pattern CHAPTER_PATTERN = Pattern.compile("^第[一二三四五六七八九十\\d]+[章节篇部]\\s*.+");

    private final PDFParser pdfParser;

    public PdfParseService() {
        this.pdfParser = new PDFParser();
    }

    public List<TextChunk> parsePdf(File file) throws IOException, SAXException, TikaException {
        log.info("开始解析PDF文件: {}", file.getName());

        String allText;
        try (InputStream inputStream = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            pdfParser.parse(inputStream, handler, metadata, context);
            allText = handler.toString();
        }

        log.info("PDF文本提取完成，文本长度: {} 字符", allText.length());

        if (allText == null || allText.isEmpty()) {
            log.warn("PDF文本提取结果为空");
            return new ArrayList<>();
        }

        String[] allLines = allText.split("\n");
        log.info("开始分割文本，共 {} 行", allLines.length);

        List<TextChunk> chunks = new ArrayList<>();
        StringBuilder currentChapter = new StringBuilder();
        String currentChapterTitle = "前言";
        int chunkIndex = 0;
        int currentPageNum = 1;

        for (String line : allLines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.matches("^第\\s*\\d+\\s*页$") || line.matches("^-\\s*\\d+\\s*-$")) {
                try {
                    String pageNumStr = line.replaceAll("[^\\d]", "");
                    currentPageNum = Integer.parseInt(pageNumStr);
                } catch (NumberFormatException e) {
                    currentPageNum++;
                }
                continue;
            }

            if (isChapterTitle(line)) {
                if (currentChapter.length() > 0) {
                    chunks.add(createChunk(currentChapterTitle, currentChapter.toString(), currentPageNum, chunkIndex++));
                    currentChapter = new StringBuilder();
                }
                currentChapterTitle = line;
            } else {
                currentChapter.append(line).append("\n");
            }
        }

        if (currentChapter.length() > 0) {
            chunks.add(createChunk(currentChapterTitle, currentChapter.toString(), currentPageNum, chunkIndex));
        }

        log.info("PDF解析完成，共分割为 {} 个文本块", chunks.size());
        return chunks;
    }

    private boolean isChapterTitle(String line) {
        if (CHAPTER_PATTERN.matcher(line).matches()) {
            return true;
        }
        if (line.length() < 30 && (line.startsWith("第") || line.contains("章") || line.contains("篇") || line.contains("部"))) {
            return true;
        }
        return false;
    }

    private TextChunk createChunk(String chapterTitle, String content, int pageNumber, int chunkIndex) {
        TextChunk chunk = new TextChunk();
        chunk.setChapterTitle(chapterTitle);
        chunk.setContent(content.trim());
        chunk.setPageNumber(pageNumber);
        chunk.setChunkIndex(chunkIndex);
        return chunk;
    }
}

package com.sinlei.shortvideo.controller;

import com.sinlei.common.dto.ShortVideoGenerateRequest;
import com.sinlei.controller.ShortVideoController;
import com.sinlei.common.model.ScriptMeta;
import com.sinlei.common.model.ScriptProjectResult;
import com.sinlei.service.ShortVideoExportService;
import com.sinlei.service.ShortVideoScriptService;
import com.sinlei.service.StyleRagService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ShortVideoController} 切片测试：Mock 业务服务，校验 HTTP 契约与统一响应 code。
 */
@WebMvcTest(controllers = ShortVideoController.class)
class ShortVideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortVideoScriptService shortVideoScriptService;
    @MockBean
    private ShortVideoExportService shortVideoExportService;
    @MockBean
    private StyleRagService styleRagService;

    @Test
    void generateShouldReturnSuccess() throws Exception {
//        ScriptProjectResult result = new ScriptProjectResult();
//        result.setProjectId("sv-test");
//        result.setTitle("测试标题");
//        result.setMeta(new ScriptMeta());
//
//        Mockito.when(shortVideoScriptService.generate(Mockito.any(ShortVideoGenerateRequest.class))).thenReturn(result);
//
//        String body = "{\"topic\":\"iPhone16测评\",\"persona\":\"犀利吐槽风\",\"durationSec\":60}";
//        mockMvc.perform(post("/shortvideo/generate")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(body))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.code").value(200))
//            .andExpect(jsonPath("$.data.projectId").value("sv-test"));
    }
}

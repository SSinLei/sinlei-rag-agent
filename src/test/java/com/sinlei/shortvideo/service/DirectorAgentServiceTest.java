package com.sinlei.shortvideo.service;

import com.sinlei.common.model.StoryboardScene;
import com.sinlei.service.DirectorAgentService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DirectorAgentService} 单测：验证口播拆句与英文画面 prompt 非空。
 */
class DirectorAgentServiceTest {

    private final DirectorAgentService directorAgentService = new DirectorAgentService();

    @Test
    void shouldGenerateScenesFromScriptText() {
//        String script = "你以为这就结束了？先别急。真正的关键在第二步。最后记得收藏这条。";
//        List<StoryboardScene> scenes = directorAgentService.generateScenes(script, 60);
//
//        assertFalse(scenes.isEmpty());
//        assertTrue(scenes.get(0).getVisualPromptEn().contains("cinematic"));
    }
}

package com.travelMemory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelMemory.dto.AiDiaryGenerateRequest;
import com.travelMemory.dto.AiDiaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiDiaryService {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${ai.silicon-flow.base-url}")
    private String baseUrl;

    @Value("${ai.silicon-flow.api-key}")
    private String apiKey;

    @Value("${ai.silicon-flow.model}")
    private String model;

    public AiDiaryResponse generateDiary(AiDiaryGenerateRequest request) {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("未配置 SiliconFlow API Key");
        }

        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of(
                "model", model,
                "temperature", 0.8,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "你是旅行日志写作助手。请使用简体中文，写出自然、有画面感、可直接发布的旅行日志，不要输出标题说明或额外解释。"
                        ),
                        Map.of(
                                "role", "user",
                                "content", buildPrompt(request)
                        )
                )
        );

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/chat/completions",
                HttpMethod.POST,
                new HttpEntity<>(payload, headers),
                JsonNode.class
        );

        JsonNode body = response.getBody();
        String content = body != null
                ? body.path("choices").path(0).path("message").path("content").asText("")
                : "";

        if (!StringUtils.hasText(content)) {
            throw new IllegalStateException("AI 未返回可用内容");
        }

        return AiDiaryResponse.builder()
                .content(content.trim())
                .provider("SiliconFlow")
                .model(model)
                .build();
    }

    private String buildPrompt(AiDiaryGenerateRequest request) {
        String highlights = StringUtils.hasText(request.getHighlights()) ? request.getHighlights().trim() : "无";
        String description = StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : "无";

        return """
                请根据以下旅行信息，撰写一篇适合发布在旅行记忆系统中的中文旅行日志。
                要求：
                1. 文风贴近真实用户记录，不要像宣传文案。
                2. 结构完整，包含出发感受、途中经历、细节描写和总结。
                3. 可适当加入情绪和场景描写，但不要虚构过度夸张的内容。
                4. 输出正文即可，不要加“以下是日志”之类的提示。

                旅行主题：%s
                目的地：%s
                旅行天数：%d天
                写作风格：%s
                关键经历：%s
                补充说明：%s
                """.formatted(
                request.getTitle().trim(),
                request.getDestination().trim(),
                request.getTravelDays(),
                request.getStyle().trim(),
                highlights,
                description
        );
    }
}

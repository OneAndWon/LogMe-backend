package com.haru.LogMe.domain.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logme/test")
@RequiredArgsConstructor
public class OpenAiTestController {
    private final WebClient openAiWebClient; // Config에서 만든 빈 주입

    @Value("${openai.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    @Operation(summary = "OpenAI API 연결 테스트(api에 들어가지 않음)", description = "단순한 인사말을 보내 API 키와 통신 상태를 확인합니다.")
    @GetMapping("/openai")
    public String testOpenAiConnection() {

        // OpenAI에 보낼 아주 간단한 질문 데이터
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", "안녕! 네가 정상적으로 연결되었는지 테스트 중이야. 짧게 인사해줘.")
                )
        );

        try {
            // WebClient를 이용해 OpenAI API 호출
            Map response = openAiWebClient.post()
                    .uri(apiUrl)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // 테스트용이므로 동기 블로킹 사용

            // 응답 결과 중 메시지 부분만 추출
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return "연결 성공! AI의 답변: " + message.get("content");

        } catch (Exception e) {
            return "연결 실패... 에러 메시지: " + e.getMessage();
        }
    }
}

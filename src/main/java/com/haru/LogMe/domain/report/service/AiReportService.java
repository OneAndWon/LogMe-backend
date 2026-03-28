package com.haru.LogMe.domain.report.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.LogMe.domain.report.entity.AiReport;
import com.haru.LogMe.domain.report.repository.AiReportRepository;
import com.haru.LogMe.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiReportService {

    private final AiReportRepository aiReportRepository;
    private final ReportDataAggregator aggregator;
    private final ObjectMapper objectMapper;
    private final WebClient openAiWebClient;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String apiUrl;

    @Async("aiReportExecutor")
    @Transactional
    public void generateAiReportAsync(Long reportId, User user, LocalDate startDate, LocalDate endDate) {
        AiReport report = aiReportRepository.findById(reportId).orElse(null);
        if (report == null) {
            log.error("AI Report not found for ID: {}", reportId);
            return;
        }

        try {
            // 1. 일자별 융합 타임라인 데이터 가공
            String aggregatedData = aggregator.aggregateDataForPrompt(user, startDate, endDate);

            // 2. 시스템 프롬프트
            /*String systemPrompt = "당신은 '삶은 감정, 소비, 일정이 분절되어 있지 않고 하나로 유기적으로 연결되어 있다'는 철학을 바탕으로, 사용자에게 깊은 삶의 인사이트를 제공하는 전문적이고 따뜻한 '라이프 코치 AI'입니다. " +
                    "당신의 목표는 일기, 가계부, 투두의 경계를 허물고 넘나드는 통찰력을 보여주어 사용자의 삶을 긍정적으로 개선하는 것입니다. " +
                    "사용자의 지출이나 미룬 할 일에 대해 절대 비난하거나 가르치려 들지 말고, 따뜻하게 응원하는 어투('~해요', '~해볼까요?')를 사용하세요.\n\n" +
                    "【할루시네이션 방지 및 예외 처리 규칙 (매우 중요)】\n" +
                    "1. 팩트 기반: 절대 제공된 데이터에 없는 수치, 일정, 지출 내역을 지어내지 마세요.\n" +
                    "2. 데이터 부족 처리: 만약 데이터가 너무 적어 특정 항목(예: 소비 습관)의 분석이 불가능하다면 억지로 지어내지 마세요. 그럴 때는 '아직 기록이 부족해서 패턴을 찾지 못했어요! 꾸준히 기록하시면 정확한 인사이트를 알려드릴게요!' 와 같이 부드럽게 안내하는 문구로 대체하세요.\n\n" +
                    "【필수 분석 요구사항 5가지 (도메인 융합 원칙)】\n" +
                    "데이터가 충분할 경우, 반드시 2개 이상의 도메인(감정, 생산성, 소비)을 유기적으로 엮어서 입체적으로 해석하세요.\n" +
                    "1. pattern_analysis (라이프스타일 교차 분석): 세 가지 데이터가 서로 어떤 영향을 미치는지 복합적인 연결고리를 찾아내세요.\n" +
                    "2. productivity_tips (생산성 팁): 어떤 감정 상태일 때나 어떤 소비가 있었을 때 할 일 효율이 가장 좋았는지 환경을 분석하세요.\n" +
                    "3. consumption_habits (소비 습관): 할 일을 미룬 날이나 특정 감정일 때 무의식적으로 발생하는 소비 패턴을 짚어주세요.\n" +
                    "4. goal_suggestion (목표 제안): 삶의 밸런스를 잡기 위한 통합 액션 플랜을 제안하세요. (예: 예산 방어를 위해 우울한 날엔 쇼핑 대신 '산책' 투두 실천)\n" +
                    "5. future_forecast (미래 예측): 현재 패턴이 유지될 경우 예상되는 삶의 모습을 예측하고 긍정적인 변화를 조언하세요.\n\n" +
                    "반드시 아래 JSON 구조로만 응답하세요. 예시의 괄호나 주석 없이 완벽하고 순수한 JSON 포맷이어야 합니다:\n" +
                    "{\n" +
                    "  \"insights\": {\n" +
                    "    \"pattern_analysis\": \"이번 주 기록을 보면, 피곤함(SAD)을 느낀 날에는 중요도가 높은 할 일을 미루고 배달음식 지출이 늘어나는 연결고리가 발견되었어요.\",\n" +
                    "    \"productivity_tips\": \"친구들과 즐겁게 외식(소비)을 한 다음 날, 오히려 오전 시간대 할 일 달성률이 100%로 훌쩍 뛰었네요! 적절한 보상이 집중력을 높여주고 있어요.\",\n" +
                    "    \"consumption_habits\": \"운동이나 자기계발 투두를 완료하지 못한 날에 쇼핑 카테고리 지출이 평균 4만원 증가했어요. 성취감을 소비로 채우려는 경향이 있을 수 있어요.\",\n" +
                    "    \"goal_suggestion\": \"다음 주에는 예산도 아끼고 스트레스도 풀 수 있도록, 화가 나는 날엔 스마트폰 쇼핑 대신 일기를 길게 쓰거나 가벼운 런닝(투두)을 해보는 건 어떨까요?\",\n" +
                    "    \"future_forecast\": \"지금처럼 긍정적인 날에 무지출과 투두 100% 달성을 이어간다면, 월말에는 목표 예산을 세이브하면서도 건강한 루틴을 완벽히 정착시킬 수 있을 거예요!\"\n" +
                    "  },\n" +
                    "  \"emotion_weather\": [\n" +
                    "    {\"date\": \"2026-03-17\", \"emotion\": \"ANGRY\"}\n" +
                    "  ],\n" +
                    "  \"life_balance\": {\n" +
                    "    \"health\": 65.5, \"work\": 80.0, \"finance\": 45.0, \"self_development\": 70.0, \"leisure\": 55.0\n" +
                    "  }\n" +
                    "}"*/;
            String systemPrompt = "당신은 '삶은 감정, 소비, 일정이 분절되어 있지 않고 하나로 유기적으로 연결되어 있다'는 철학을 바탕으로, 사용자에게 깊은 삶의 인사이트를 제공하는 전문적이고 따뜻한 '라이프 코치 AI'입니다. " +
                    "【 절대 금지 규칙 (오만한 훈장님 말투 및 단정 짓기 엄격 금지)】\n" +
                    "1. 단정 짓기 금지: 사용자의 행동이나 심리를 함부로 확정 지어 말하지 마세요. ('~한 행동이었어요', '~한 거죠', '보상하기 위한 것입니다' 등 오만한 확언 절대 금지. 대신 '~했던 것 같아요', '~한 마음이 들었을 수 있어요'처럼 부드럽고 조심스럽게 추측하세요.)\n" +
                    "2. 부정적 지적 및 팩트 폭력 금지: 할 일을 달성하지 못한 것에 대해 '전혀 완료하지 못했네요', '0개네요'처럼 부정적으로 지적하거나 죄책감을 유발하지 마세요. '에너지가 조금 부족했던 것 같아요', '잠시 쉬어가는 하루였네요'처럼 따뜻하게 감싸주세요.\n" +
                    "3. 기계적 번역투 금지: '특정 감정이 강하게 작용할 때', '대체 아이템' 같은 어색한 표현을 쓰지 말고, 진짜 다정한 친구처럼 자연스럽게 말하세요.\n\n" +
                    "【할루시네이션 방지 및 예외 처리 규칙】\n" +
                    "1. 절대 없는 수치나 감정을 지어내지 마세요.\n" +
                    "2. 데이터가 부족해 분석이 어렵다면 억지로 지어내지 말고, '아직 기록이 부족해서 패턴을 찾기 어려워요! 꾸준히 기록하시면 정확한 인사이트를 드릴게요'라고 안내하세요.\n\n" +
                    "【필수 분석 요구사항 5가지 (도메인 융합 원칙)】\n" +
                    "1. pattern_analysis: 세 가지 데이터가 서로 어떤 영향을 미치는지 부드럽게 연결해서 분석하세요.\n" +
                    "2. productivity_tips: 어떤 감정이나 상황일 때 할 일 효율이 좋았는지 칭찬 위주로 분석하세요.\n" +
                    "3. consumption_habits: 지출이 컸던 날의 감정을 살피고, 그 지출이 사용자에게 어떤 위로가 되었을지 먼저 공감해 준 뒤 조언하세요.\n" +
                    "4. goal_suggestion: 삶의 밸런스를 잡기 위한 작고 현실적인 행동을 다정하게 제안하세요.\n" +
                    "5. future_forecast: 현재의 긍정적인 면을 부각하며, 앞으로 더 좋아질 것이라는 희망찬 예측을 제공하세요.\n\n" +
                    "반드시 아래 JSON 구조로만 응답하세요. 예시의 괄호나 주석 없이 완벽하고 순수한 JSON 포맷이어야 합니다:\n" +
                    "{\n" +
                    "  \"insights\": {\n" +
                    "    \"pattern_analysis\": \"기획안으로 많이 지치셨던 17일에는 평소 잘 해내시던 '운동'도 잠시 쉬어가셨네요. 에너지가 많이 방전된 하루였던 것 같아요. 반면 푹 쉬고 일어나 기분이 좋았던 18일에는 아침 운동부터 시작해 하루를 알차게 보내셨어요. 컨디션 관리가 일상에 큰 영향을 주고 있네요!\",\n" +
                    "    \"productivity_tips\": \"18일의 기록을 보면, 푹 자고 일어나서 기분이 좋은 날 오전 10시부터 집중력이 가장 높았어요. 기분 좋은 아침 산책이나 운동이 회원님의 하루를 끌어올려 주는 멋진 부스터 역할을 하는 것 같아요.\",\n" +
                    "    \"consumption_habits\": \"스트레스를 크게 받았던 17일에 옷과 마라탕으로 지출이 조금 있었어요. 지치고 힘든 마음을 달래기 위해 나에게 주는 작은 위로의 선물이었던 것 같아요. 가끔은 이런 위로도 필요하지만, 잦아지면 지갑이 아파할 수 있으니 다음번엔 다른 방법도 함께 고민해 볼까요?\",\n" +
                    "    \"goal_suggestion\": \"다음번에 또 직장에서 스트레스를 받는 날이 온다면, 무언가를 사기보다는 18일처럼 기분 좋게 땀 흘렸던 '가벼운 운동'이나 '일기 쓰기'로 마음을 훌훌 털어내 보는 건 어떨까요?\",\n" +
                    "    \"future_forecast\": \"18일처럼 긍정적인 감정과 좋은 루틴을 계속 이어가신다면, 스트레스성 지출은 자연스럽게 줄어들고 몸과 마음, 그리고 지갑까지 모두 든든해지는 한 달을 보내실 수 있을 거예요! 응원합니다!\"\n" +
                    "  },\n" +
                    "  \"emotion_weather\": [\n" +
                    "    {\"date\": \"2026-03-17\", \"emotion\": \"ANGRY\"}\n" +
                    "  ],\n" +
                    "  \"life_balance\": {\n" +
                    "    \"health\": 65.5, \"work\": 80.0, \"finance\": 45.0, \"self_development\": 70.0, \"leisure\": 55.0\n" +
                    "  }\n" +
                    "}";

            Map response = openAiWebClient.post()
                    .uri(apiUrl)
                    .bodyValue(Map.of(
                            "model", model,
                            "response_format", Map.of("type", "json_object"),
                            "messages", List.of(
                                    Map.of("role", "system", "content", systemPrompt),
                                    Map.of("role", "user", "content", aggregatedData)
                            )
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String contentString = (String) message.get("content");

            Map<String, Object> usage = (Map<String, Object>) response.get("usage");
            Integer totalTokens = (Integer) usage.get("total_tokens");

            Map<String, Object> contentJson = objectMapper.readValue(contentString, Map.class);

            report.updateStatusToCompleted(contentJson, totalTokens, model);
            aiReportRepository.save(report);

        } catch (Exception e) {
            log.error("AI 리포트 생성 실패 (Report ID: {}): {}", reportId, e.getMessage());
            report.updateStatusToFailed(e.getMessage());
            aiReportRepository.save(report);
        }
    }
}

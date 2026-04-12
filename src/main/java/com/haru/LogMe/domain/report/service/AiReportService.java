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
                    "}";*/

            String systemPrompt = "당신은 '삶의 감정, 소비, 일정은 하나로 유기적으로 연결되어 있다'는 철학을 바탕으로 분석하는 전문적인 'LogMe 라이프 코치 AI'입니다. " +
                    "사용자의 데이터를 통합적으로 통찰하여 삶을 개선할 수 있는 따뜻한 인사이트를 제공하는 것이 당신의 목적입니다. " +

                    "【 소통 및 윤리 규칙 】\n" +
                    "1. 단정적 어조 금지: '비싸게 샀네요', '스트레스 때문입니다' 같은 확언 대신 '~했던 것 같아요', '~한 마음이 들었을 수 있어요'처럼 조심스럽게 추측하며 공감하세요.\n" +
                    "2. 부정적 지적 금지: 할 일을 못 했거나 지출이 큰 것에 대해 죄책감을 주지 마세요. '에너지가 조금 부족했던 날이었네요'처럼 따뜻하게 감싸주며 긍정적인 면을 먼저 찾으세요.\n" +
                    "3. 자연스러운 구어체: '특정 감정의 작용' 같은 딱딱한 번역투 대신, 다정한 친구가 말해주는 듯한 자연스러운 한국어를 구사하세요.\n" +
                    "4. 할루시네이션 방지: 없는 수치나 감정을 지어내지 마세요. 데이터(일정, 소비, 감정 중 하나라도)가 부족하여 분석이 어렵다면 억지로 지어내지 말고 다음 문장을 출력하세요: \"아직 기록이 부족해서 패턴을 찾기 어려워요! 조금 더 꾸준히 기록하시면 당신만을 위한 정밀한 인사이트를 드릴게요.\"\n\n" +

                    "【 5대 융합 인사이트 요구사항 】\n" +
                    "모든 항목은 반드시 [일정-소비-감정] 세 가지 데이터를 유기적으로 교차 분석하여 서술해야 합니다.\n" +
                    "1. pattern_analysis (삶의 연결고리): 세 데이터가 얽혀 만드는 반복적 패턴을 찾으세요. (예: 특정 감정일 때 할 일 달성률이 변하거나 보상 소비가 발생하는 흐름)\n" +
                    "2. productivity_tips (지속 가능한 효율성): 어떤 감정과 소비 상태일 때 업무 효율이 좋았는지 칭찬 위주로 분석하고, 에너지를 잃지 않는 실질적인 시간 관리법을 제안하세요.\n" +
                    "3. consumption_habits (심리적 재정 흐름): 지출이 컸던 날의 일정과 감정을 살피고, 그 소비가 사용자에게 어떤 위로가 되었을지 먼저 충분히 공감한 뒤 부드러운 관리 전략을 제시하세요.\n" +
                    "4. goal_suggestion (데이터 기반 자기 성장): 과거 데이터 속 긍정적인 순간(성공 루틴)을 포착하여, 삶의 밸런스를 잡기 위한 작고 현실적인 행동을 다정하게 제안하세요.\n" +
                    "5. future_forecast (삶의 궤적 조언): 현재의 긍정적인 면을 부각하며, 현재 패턴을 유지하거나 소폭 수정했을 때 다가올 희망찬 미래의 결과를 예측해 주세요.\n\n" +

                    "【 출력 형식 】\n" +
                    "반드시 아래 JSON 구조로만 응답하세요 (순수 JSON 데이터만 출력):\n" +
                    "{\n" +
                    "  \"insights\": {\n" +
                    "    \"pattern_analysis\": \"...\",\n" +
                    "    \"productivity_tips\": \"...\",\n" +
                    "    \"consumption_habits\": \"...\",\n" +
                    "    \"goal_suggestion\": \"...\",\n" +
                    "    \"future_forecast\": \"...\"\n" +
                    "  },\n" +
                    "  \"life_balance\": {\n" +
                    "    \"work\": 80.0, \"health\": 65.5, \"finance\": 45.0, \"self_development\": 70.0, \"leisure\": 55.0\n" +
                    "  },\n" +
                    "  \"emotion_weather\": [\n" +
                    "    { \"date\": \"YYYY-MM-DD\", \"emotion\": \"...\" }\n" +
                    "  ]\n" +
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

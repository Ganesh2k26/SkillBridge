package com.ganesh.skillbridge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.skillbridge.dto.StudyPlanRequest;
import com.ganesh.skillbridge.dto.StudyPlanResponse;
import com.ganesh.skillbridge.entity.StudyPlan;
import com.ganesh.skillbridge.entity.TopicProgress;
import com.ganesh.skillbridge.entity.User;
import com.ganesh.skillbridge.repository.CompanyRepository;
import com.ganesh.skillbridge.repository.PracticeAttemptRepository;
import com.ganesh.skillbridge.repository.QuestionRepository;
import com.ganesh.skillbridge.repository.StudyPlanRepository;
import com.ganesh.skillbridge.repository.TopicProgressRepository;
import com.ganesh.skillbridge.util.PromptBuilder;
import com.ganesh.skillbridge.util.ReadinessScoreCalculator;
import com.ganesh.skillbridge.repository.AiFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyPlanService {

    @Value("${gemini.api.key}")
    private String geminiKey;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    private final StudyPlanRepository        planRepo;
    private final TopicProgressRepository    topicProgressRepo;
    private final PracticeAttemptRepository  attemptRepo;
    private final QuestionRepository         questionRepo;
    private final CompanyRepository          companyRepo;
    private final AiFeedbackRepository       feedbackRepo;
    private final PromptBuilder              promptBuilder;
    private final ReadinessScoreCalculator   scoreCalculator;
    private final ObjectMapper               mapper = new ObjectMapper();

    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build();

    public StudyPlanResponse generatePlan(StudyPlanRequest req, User user) {

        // ── 1. Resolve weak topics ────────────────────────────────────────────
        List<String> weakTopics = req.getWeakTopics();
        if (weakTopics == null || weakTopics.isEmpty()) {
            // Auto-detect from the user's actual practice history
            weakTopics = topicProgressRepo
                .findByUserIdAndStrengthLevel(user.getId(), "WEAK")
                .stream()
                .map(TopicProgress::getTopic)
                .collect(Collectors.toList());
        }

        // ── 2. Calculate real readiness score for the target company ──────────
        int readinessScore = computeReadiness(user, req.getCompanyName());

        // ── 3. Build dynamic prompt ───────────────────────────────────────────
        String prompt = promptBuilder.buildStudyPlanPrompt(
            req.getCompanyName(), req.getPlanDays(), weakTopics, readinessScore);

        // ── 4. Call Gemini; fall back to structured plan on failure ───────────
        String planContent;
        try {
            planContent = callGemini(prompt);
            // Clean markdown fences if Gemini wraps JSON
            planContent = cleanJson(planContent);
        } catch (Exception e) {
            log.warn("Study plan AI failed (company={}, days={}): {}",
                req.getCompanyName(), req.getPlanDays(), e.getMessage());
            planContent = buildFallbackPlan(req.getCompanyName(), req.getPlanDays(), weakTopics);
        }

        // ── 5. Persist ────────────────────────────────────────────────────────
        StudyPlan plan = new StudyPlan();
        plan.setUser(user);
        plan.setCompanyName(req.getCompanyName());
        plan.setPlanDays(req.getPlanDays());
        plan.setPlanContent(planContent);
        plan.setWeakTopics(weakTopics.isEmpty() ? "General preparation" : String.join(", ", weakTopics));
        plan.setReadinessScore(readinessScore);
        planRepo.save(plan);

        return toResponse(plan);
    }

    public List<StudyPlanResponse> getUserPlans(Long userId) {
        return planRepo.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Compute readiness for the requested company by name.
     * Falls back to overall readiness when the company is not found in DB.
     */
    private int computeReadiness(User user, String companyName) {
        try {
            long totalQuestions = companyRepo.findByNameIgnoreCase(companyName)
                .map(c -> questionRepo.countByCompanyId(c.getId()))
                .orElseGet(questionRepo::count);

            if (totalQuestions == 0) return 0;

            long feedbackCount = feedbackRepo
                .findByUserIdOrderByCreatedAtDesc(user.getId()).size();

            // If we found the company, use company-specific attempt stats
            return companyRepo.findByNameIgnoreCase(companyName)
                .map(company -> {
                    long attempted = attemptRepo.countByUserIdAndCompanyId(user.getId(), company.getId());
                    long correct   = attemptRepo.countCorrectByUserIdAndCompanyId(user.getId(), company.getId());
                    return scoreCalculator.calculate(totalQuestions, attempted, correct, feedbackCount, 1);
                })
                .orElseGet(() -> {
                    // Company not in DB — compute overall score
                    long attempted = attemptRepo.countByUserId(user.getId());
                    long correct   = attemptRepo.countByUserIdAndIsCorrect(user.getId(), true);
                    return scoreCalculator.calculate(totalQuestions, attempted, correct, feedbackCount, 1);
                });
        } catch (Exception e) {
            log.warn("Could not compute readiness score: {}", e.getMessage());
            return 0;
        }
    }

    private String callGemini(String prompt) throws Exception {
        String escapedPrompt = mapper.writeValueAsString(prompt);
        String reqBody = "{\"contents\":[{\"parts\":[{\"text\":" + escapedPrompt + "}]}],"
            + "\"generationConfig\":{\"temperature\":0.5,\"maxOutputTokens\":3000}}";

        Request request = new Request.Builder()
            .url(geminiUrl + "?key=" + geminiKey)
            .post(RequestBody.create(reqBody, MediaType.parse("application/json")))
            .build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                String body = resp.body() != null ? resp.body().string() : "";
                throw new RuntimeException("Gemini HTTP " + resp.code() + ": " + body);
            }
            String body = resp.body() != null ? resp.body().string() : "";
            JsonNode root = mapper.readTree(body);
            return root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
        }
    }

    private String cleanJson(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.startsWith("```json")) s = s.substring(7);
        else if (s.startsWith("```")) s = s.substring(3);
        if (s.endsWith("```")) s = s.substring(0, s.length() - 3);
        return s.trim();
    }

    /**
     * Fallback plan: still structured JSON, tailored by company & weak topics.
     * Used when Gemini is unavailable (no key, quota exceeded, network error).
     */
    private String buildFallbackPlan(String company, int days, List<String> weak) {
        // Pick day themes: prioritise weak topics first, then standard curriculum
        String[] defaultTopics = {
            "Aptitude & Reasoning", "Core Java & OOP", "SQL & Databases",
            "Data Structures (Arrays, LinkedList)", "Algorithms (Sorting, Searching)",
            "HR & Behavioural", "Mock Test & Review"
        };

        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"summary\":\"").append(days).append("-day focused preparation plan for ")
          .append(company).append(". Prioritises ")
          .append(weak.isEmpty() ? "core placement topics" : String.join(", ", weak))
          .append(".\",")
          .append("\"days\":[");

        for (int i = 0; i < days; i++) {
            if (i > 0) sb.append(",");
            // First use weak topics if available, then default
            String topic = i < weak.size() ? weak.get(i) : defaultTopics[i % defaultTopics.length];
            int practiceCount = (i == days - 1) ? 20 : 10;   // last day is mock test
            int hours         = (i == days - 1) ? 4  : 3;

            sb.append("{")
              .append("\"day\":").append(i + 1).append(",")
              .append("\"title\":\"").append(topic).append("\",")
              .append("\"topics\":[\"").append(topic).append(" fundamentals\",\"Practice problems\"],")
              .append("\"tasks\":[")
              .append("\"Study key concepts and theory\",")
              .append("\"Solve ").append(practiceCount).append(" practice questions on SkillBridge\",")
              .append("\"Review incorrect answers and note gaps\"")
              .append("],")
              .append("\"practiceCount\":").append(practiceCount).append(",")
              .append("\"estimatedHours\":").append(hours)
              .append("}");
        }

        sb.append("],")
          .append("\"tips\":[")
          .append("\"Revise weak topics every morning for 15 minutes\",")
          .append("\"Time yourself during practice — placement tests are timed\",")
          .append("\"Keep a mistake log and revisit it daily\"")
          .append("],")
          .append("\"expectedScoreAfter\":").append(Math.min(95, 45 + days * 3))
          .append("}");

        return sb.toString();
    }

    private StudyPlanResponse toResponse(StudyPlan p) {
        return StudyPlanResponse.builder()
            .id(p.getId())
            .companyName(p.getCompanyName())
            .planDays(p.getPlanDays())
            .planContent(p.getPlanContent())
            .readinessScore(p.getReadinessScore())
            .createdAt(p.getCreatedAt())
            .build();
    }
}

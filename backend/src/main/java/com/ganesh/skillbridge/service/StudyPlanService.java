package com.ganesh.skillbridge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.skillbridge.dto.StudyPlanRequest;
import com.ganesh.skillbridge.dto.StudyPlanResponse;
import com.ganesh.skillbridge.entity.StudyPlan;
import com.ganesh.skillbridge.entity.TopicProgress;
import com.ganesh.skillbridge.entity.User;
import com.ganesh.skillbridge.repository.StudyPlanRepository;
import com.ganesh.skillbridge.repository.TopicProgressRepository;
import com.ganesh.skillbridge.util.PromptBuilder;
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

    private final StudyPlanRepository planRepo;
    private final TopicProgressRepository topicProgressRepo;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper mapper = new ObjectMapper();

    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build();

    public StudyPlanResponse generatePlan(StudyPlanRequest req, User user) {
        List<String> weakTopics = req.getWeakTopics();
        if (weakTopics == null || weakTopics.isEmpty()) {
            weakTopics = topicProgressRepo
                .findByUserIdAndStrengthLevel(user.getId(), "WEAK")
                .stream()
                .map(TopicProgress::getTopic)
                .collect(Collectors.toList());
        }

        String prompt = promptBuilder.buildStudyPlanPrompt(
            req.getCompanyName(), req.getPlanDays(), weakTopics, 45);

        String planContent;
        try {
            planContent = callGemini(prompt);
        } catch (Exception e) {
            log.warn("Study plan AI failed: {}", e.getMessage());
            planContent = buildFallbackPlan(req.getCompanyName(), req.getPlanDays(), weakTopics);
        }

        StudyPlan plan = new StudyPlan();
        plan.setUser(user);
        plan.setCompanyName(req.getCompanyName());
        plan.setPlanDays(req.getPlanDays());
        plan.setPlanContent(planContent);
        plan.setWeakTopics(String.join(", ", weakTopics));
        plan.setReadinessScore(45);
        planRepo.save(plan);

        return StudyPlanResponse.builder()
            .id(plan.getId())
            .companyName(plan.getCompanyName())
            .planDays(plan.getPlanDays())
            .planContent(plan.getPlanContent())
            .readinessScore(plan.getReadinessScore())
            .createdAt(plan.getCreatedAt())
            .build();
    }

    public List<StudyPlanResponse> getUserPlans(Long userId) {
        return planRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(p -> StudyPlanResponse.builder()
                .id(p.getId())
                .companyName(p.getCompanyName())
                .planDays(p.getPlanDays())
                .planContent(p.getPlanContent())
                .readinessScore(p.getReadinessScore())
                .createdAt(p.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }

    private String callGemini(String prompt) throws Exception {
        String escapedPrompt = mapper.writeValueAsString(prompt);
        String reqBody = "{\"contents\":[{\"parts\":[{\"text\":" + escapedPrompt + "}]}],"
            + "\"generationConfig\":{\"temperature\":0.4,\"maxOutputTokens\":2048}}";

        Request request = new Request.Builder()
            .url(geminiUrl + "?key=" + geminiKey)
            .post(RequestBody.create(reqBody, MediaType.parse("application/json")))
            .build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) throw new RuntimeException("Gemini error: " + resp.code());
            String body = resp.body() != null ? resp.body().string() : "";
            JsonNode root = mapper.readTree(body);
            return root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
        }
    }

    private String buildFallbackPlan(String company, int days, List<String> weak) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"summary\":\"Focused ").append(days)
          .append("-day preparation plan for ").append(company).append("\",\"days\":[");
        String[] topics = {"Aptitude & Reasoning","Core Java & OOP","SQL & Databases",
                           "DSA Basics","HR & Behavioral","Mock Tests","Revision"};
        for (int i = 0; i < days; i++) {
            if (i > 0) sb.append(",");
            sb.append("{\"day\":").append(i + 1)
              .append(",\"title\":\"").append(topics[i % topics.length]).append("\"")
              .append(",\"tasks\":[\"Practice 10 questions\",\"Review mistakes\",\"Revise notes\"]")
              .append(",\"practiceCount\":10,\"estimatedHours\":3}");
        }
        sb.append("],\"tips\":[\"Consistency beats intensity\",\"Review mistakes daily\","
                + "\"Practice time management\"],\"expectedScoreAfter\":65}");
        return sb.toString();
    }
}

package com.ganesh.skillbridge.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.skillbridge.dto.AiFeedbackRequest;
import com.ganesh.skillbridge.dto.AiFeedbackResponse;
import com.ganesh.skillbridge.entity.AiFeedback;
import com.ganesh.skillbridge.entity.Question;
import com.ganesh.skillbridge.entity.User;
import com.ganesh.skillbridge.exception.ResourceNotFoundException;
import com.ganesh.skillbridge.repository.AiFeedbackRepository;
import com.ganesh.skillbridge.repository.QuestionRepository;
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
public class AiFeedbackService {

    @Value("${gemini.api.key}")
    private String geminiKey;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    private final AiFeedbackRepository feedbackRepo;
    private final QuestionRepository questionRepo;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper mapper = new ObjectMapper();

    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build();

    public AiFeedbackResponse getFeedback(AiFeedbackRequest req, User user) {
        Question q = questionRepo.findById(req.getQuestionId())
            .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        String prompt = promptBuilder.buildAiFeedbackPrompt(
            q.getTitle(), q.getDescription(), q.getCategory(),
            req.getUserAnswer(), q.getCorrectAnswer());

        AiFeedback feedback;
        try {
            String aiJson = callGemini(prompt);
            String cleaned = cleanJson(aiJson);
            JsonNode node = mapper.readTree(cleaned);

            feedback = new AiFeedback();
            feedback.setUser(user);
            feedback.setQuestion(q);
            feedback.setUserAnswer(req.getUserAnswer());
            feedback.setFeedback(node.path("feedback").asText("No feedback available"));
            feedback.setImprovedAnswer(node.path("improvedAnswer").asText(""));
            feedback.setMissingPoints(node.path("missingPoints").asText(""));
            feedback.setScore(node.path("score").asInt(50));

        } catch (Exception e) {
            log.warn("Gemini call failed: {}", e.getMessage());
            feedback = new AiFeedback();
            feedback.setUser(user);
            feedback.setQuestion(q);
            feedback.setUserAnswer(req.getUserAnswer());
            feedback.setFeedback("AI analysis temporarily unavailable. Your answer has been recorded.");
            feedback.setImprovedAnswer(q.getCorrectAnswer());
            feedback.setMissingPoints("Please check the explanation provided with the question.");
            feedback.setScore(50);
        }

        feedbackRepo.save(feedback);
        return toResponse(feedback, q);
    }

    public List<AiFeedbackResponse> getUserFeedbacks(Long userId) {
        return feedbackRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(f -> toResponse(f, f.getQuestion()))
            .collect(Collectors.toList());
    }

    private String callGemini(String prompt) throws Exception {
        String escapedPrompt = mapper.writeValueAsString(prompt);
        String reqBody = "{\"contents\":[{\"parts\":[{\"text\":" + escapedPrompt + "}]}],"
            + "\"generationConfig\":{\"temperature\":0.3,\"maxOutputTokens\":1024}}";

        Request request = new Request.Builder()
            .url(geminiUrl + "?key=" + geminiKey)
            .post(RequestBody.create(reqBody, MediaType.parse("application/json")))
            .build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new RuntimeException("Gemini error: " + resp.code());
            }
            String body = resp.body() != null ? resp.body().string() : "";
            JsonNode root = mapper.readTree(body);
            return root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
        }
    }

    private String cleanJson(String raw) {
        String s = raw.trim();
        if (s.startsWith("```json")) s = s.substring(7);
        if (s.startsWith("```"))     s = s.substring(3);
        if (s.endsWith("```"))       s = s.substring(0, s.length() - 3);
        return s.trim();
    }

    private AiFeedbackResponse toResponse(AiFeedback f, Question q) {
        return AiFeedbackResponse.builder()
            .id(f.getId())
            .questionId(q.getId())
            .questionTitle(q.getTitle())
            .userAnswer(f.getUserAnswer())
            .feedback(f.getFeedback())
            .improvedAnswer(f.getImprovedAnswer())
            .missingPoints(f.getMissingPoints())
            .score(f.getScore())
            .createdAt(f.getCreatedAt())
            .build();
    }
}

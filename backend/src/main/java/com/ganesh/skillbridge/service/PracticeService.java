package com.ganesh.skillbridge.service;

import com.ganesh.skillbridge.dto.PracticeSubmissionRequest;
import com.ganesh.skillbridge.dto.PracticeSubmissionResponse;
import com.ganesh.skillbridge.entity.*;
import com.ganesh.skillbridge.exception.ResourceNotFoundException;
import com.ganesh.skillbridge.repository.*;
import com.ganesh.skillbridge.util.ReadinessScoreCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PracticeService {

    private final PracticeAttemptRepository attemptRepo;
    private final QuestionRepository questionRepo;
    private final TopicProgressRepository topicProgressRepo;
    private final ReadinessScoreCalculator calculator;

    @Transactional
    public PracticeSubmissionResponse submitAnswer(PracticeSubmissionRequest req, User user) {
        Question q = questionRepo.findById(req.getQuestionId())
            .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        boolean correct = q.getCorrectAnswer().trim().equalsIgnoreCase(req.getUserAnswer().trim());
        String status = correct ? "CORRECT" : "NEEDS_REVISION";

        PracticeAttempt attempt = attemptRepo
            .findByUserIdAndQuestionId(user.getId(), q.getId())
            .orElse(new PracticeAttempt());

        attempt.setUser(user);
        attempt.setQuestion(q);
        attempt.setUserAnswer(req.getUserAnswer());
        attempt.setIsCorrect(correct);
        attempt.setStatus(status);
        if (req.getTimeTakenSeconds() != null) {
            attempt.setTimeTakenSeconds(req.getTimeTakenSeconds());
        }
        attemptRepo.save(attempt);

        updateTopicProgress(user, q, correct);

        return PracticeSubmissionResponse.builder()
            .isCorrect(correct)
            .correctAnswer(q.getCorrectAnswer())
            .explanation(q.getExplanation())
            .status(status)
            .pointsEarned(correct ? q.getPoints() : 0)
            .message(correct ? "🎉 Correct! Well done!" : "❌ Incorrect. Review the explanation.")
            .build();
    }

    private void updateTopicProgress(User user, Question q, boolean correct) {
        TopicProgress tp = topicProgressRepo
            .findByUserIdAndTopicAndCategory(user.getId(), q.getTopic(), q.getCategory())
            .orElse(new TopicProgress());

        if (tp.getId() == null) {
            tp.setUser(user);
            tp.setTopic(q.getTopic());
            tp.setCategory(q.getCategory());
            tp.setTotalQuestions((int) questionRepo.countByCompanyId(q.getCompany().getId()));
            tp.setAttempted(0);
            tp.setCorrect(0);
        }

        tp.setAttempted(tp.getAttempted() + 1);
        if (correct) tp.setCorrect(tp.getCorrect() + 1);
        tp.setStrengthLevel(calculator.getStrengthLevel(tp.getAttempted(), tp.getCorrect()));
        topicProgressRepo.save(tp);
    }

    public List<PracticeAttempt> getUserHistory(Long userId) {
        return attemptRepo.findByUserIdOrderByAttemptedAtDesc(userId);
    }
}

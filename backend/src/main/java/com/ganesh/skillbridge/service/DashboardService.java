package com.ganesh.skillbridge.service;

import com.ganesh.skillbridge.dto.DashboardResponse;
import com.ganesh.skillbridge.entity.TopicProgress;
import com.ganesh.skillbridge.entity.User;
import com.ganesh.skillbridge.repository.*;
import com.ganesh.skillbridge.util.ReadinessScoreCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PracticeAttemptRepository attemptRepo;
    private final TopicProgressRepository topicProgressRepo;
    private final AiFeedbackRepository feedbackRepo;
    private final QuestionRepository questionRepo;
    private final CompanyRepository companyRepo;
    private final ReadinessScoreCalculator calculator;

    public DashboardResponse getDashboard(User user) {
        long total   = attemptRepo.countByUserId(user.getId());
        long correct = attemptRepo.countByUserIdAndIsCorrect(user.getId(), true);
        double accuracy = total == 0 ? 0 : Math.round((double) correct / total * 1000.0) / 10.0;
        long feedbackCount = feedbackRepo.findByUserIdOrderByCreatedAtDesc(user.getId()).size();
        long totalQuestions = resolveTotalQuestionsForUser(user);

        int readiness = calculator.calculate(totalQuestions, total, correct, feedbackCount, 1);

        List<TopicProgress> allProgress = topicProgressRepo.findByUserId(user.getId());

        List<DashboardResponse.TopicStat> weak = allProgress.stream()
            .filter(tp -> "WEAK".equals(tp.getStrengthLevel()))
            .map(this::toStat).collect(Collectors.toList());

        List<DashboardResponse.TopicStat> strong = allProgress.stream()
            .filter(tp -> "STRONG".equals(tp.getStrengthLevel()))
            .map(this::toStat).collect(Collectors.toList());

        Map<String, long[]> catMap = new LinkedHashMap<>();
        attemptRepo.findByUserIdOrderByAttemptedAtDesc(user.getId()).forEach(a -> {
            String cat = a.getQuestion().getCategory();
            catMap.computeIfAbsent(cat, k -> new long[]{0, 0});
            catMap.get(cat)[0]++;
            if (a.getIsCorrect()) catMap.get(cat)[1]++;
        });

        List<DashboardResponse.CategoryStat> catStats = catMap.entrySet().stream().map(e ->
            DashboardResponse.CategoryStat.builder()
                .category(e.getKey())
                .attempted(e.getValue()[0])
                .correct(e.getValue()[1])
                .accuracy(e.getValue()[0] == 0 ? 0 :
                    Math.round((double) e.getValue()[1] / e.getValue()[0] * 1000.0) / 10.0)
                .build()
        ).collect(Collectors.toList());

        return DashboardResponse.builder()
            .userId(user.getId())
            .userName(user.getName())
            .targetCompany(user.getTargetCompany())
            .totalAttempted(total)
            .totalCorrect(correct)
            .accuracyPercent(accuracy)
            .readinessScore(readiness)
            .weakAreas(weak)
            .strongAreas(strong)
            .categoryBreakdown(catStats)
            .recentActivity(DashboardResponse.RecentActivity.builder()
                .lastActiveAt(user.getLastLogin())
                .streakDays(1)
                .lastAttemptedTopics(weak.stream().limit(3)
                    .map(DashboardResponse.TopicStat::getTopic)
                    .collect(Collectors.toList()))
                .build())
            .build();
    }

    public int getReadinessScore(User user, Long companyId) {
        long totalQuestions = questionRepo.countByCompanyId(companyId);
        long attempted = attemptRepo.countByUserIdAndCompanyId(user.getId(), companyId);
        long correct   = attemptRepo.countCorrectByUserIdAndCompanyId(user.getId(), companyId);
        long feedback  = feedbackRepo.findByUserIdOrderByCreatedAtDesc(user.getId()).size();
        return calculator.calculate(totalQuestions, attempted, correct, feedback, 1);
    }

    private long resolveTotalQuestionsForUser(User user) {
        if (user.getTargetCompany() != null && !user.getTargetCompany().isBlank()) {
            return companyRepo.findByNameIgnoreCase(user.getTargetCompany())
                .map(c -> questionRepo.countByCompanyId(c.getId()))
                .orElseGet(questionRepo::count);
        }
        return questionRepo.count();
    }

    private DashboardResponse.TopicStat toStat(TopicProgress tp) {
        double acc = tp.getAttempted() == 0 ? 0 :
            Math.round((double) tp.getCorrect() / tp.getAttempted() * 1000.0) / 10.0;
        return DashboardResponse.TopicStat.builder()
            .topic(tp.getTopic())
            .category(tp.getCategory())
            .attempted(tp.getAttempted())
            .correct(tp.getCorrect())
            .strengthLevel(tp.getStrengthLevel())
            .accuracy(acc)
            .build();
    }
}

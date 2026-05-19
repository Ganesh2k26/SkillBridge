package com.ganesh.skillbridge.util;

import org.springframework.stereotype.Component;

@Component
public class ReadinessScoreCalculator {

    /**
     * Readiness = Practice(40%) + Accuracy(30%) + Consistency(20%) + AI Feedback(10%)
     */
    public int calculate(long totalQuestions, long attempted, long correct,
                          long aiFeedbackCount, int streakDays) {
        if (totalQuestions == 0) return 0;

        // Practice completion (0-40)
        double practiceScore = Math.min((double) attempted / totalQuestions, 1.0) * 40;

        // Accuracy (0-30)
        double accuracyScore = attempted == 0 ? 0
                : ((double) correct / attempted) * 30;

        // Consistency/streak (0-20) — max after 7 days
        double consistencyScore = Math.min(streakDays / 7.0, 1.0) * 20;

        // AI Feedback engagement (0-10)
        double feedbackScore = Math.min(aiFeedbackCount / 5.0, 1.0) * 10;

        int total = (int) Math.round(practiceScore + accuracyScore + consistencyScore + feedbackScore);
        return Math.min(Math.max(total, 0), 100);
    }

    public String getReadinessLabel(int score) {
        if (score >= 85) return "Excellent — Ready to apply!";
        if (score >= 70) return "Good — Minor gaps remain";
        if (score >= 50) return "Average — Focused revision needed";
        if (score >= 30) return "Below Average — Consistent practice required";
        return "Beginner — Start from basics";
    }

    public String getStrengthLevel(int attempted, int correct) {
        if (attempted == 0) return "WEAK";
        double accuracy = (double) correct / attempted;
        if (accuracy >= 0.75) return "STRONG";
        if (accuracy >= 0.45) return "MEDIUM";
        return "WEAK";
    }
}

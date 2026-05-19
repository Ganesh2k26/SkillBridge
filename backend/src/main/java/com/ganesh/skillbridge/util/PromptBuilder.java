package com.ganesh.skillbridge.util;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PromptBuilder {

    public String buildAiFeedbackPrompt(String questionTitle, String questionDesc,
                                         String category, String userAnswer,
                                         String correctAnswer) {
        return """
            You are an expert placement trainer and technical interviewer with 15+ years of experience.
            
            Evaluate the following student answer for a placement interview question.
            
            Question: %s
            Category: %s
            Description: %s
            
            Correct/Expected Answer: %s
            
            Student's Answer: %s
            
            Provide your evaluation as ONLY a valid JSON object with this exact structure:
            {
              "score": <integer 0-100>,
              "feedback": "<2-3 sentence overall assessment>",
              "missingPoints": "<comma-separated list of key points missing from the answer>",
              "improvedAnswer": "<a complete, interview-level model answer the student should aim for>",
              "tips": "<2-3 specific actionable tips to improve>"
            }
            
            Be honest, constructive, and specific. Return ONLY the JSON object.
            """.formatted(questionTitle, category, questionDesc, correctAnswer, userAnswer);
    }

    public String buildStudyPlanPrompt(String companyName, int days,
                                        List<String> weakTopics, int readinessScore) {
        String weakTopicsStr = weakTopics == null || weakTopics.isEmpty()
                ? "General preparation needed"
                : String.join(", ", weakTopics);

        return """
            You are a placement preparation expert. Generate a detailed %d-day study plan for a student.
            
            Target Company: %s
            Current Readiness Score: %d%%
            Weak Areas: %s
            
            Create a practical day-wise plan in this EXACT JSON format:
            {
              "summary": "<2-sentence overview of the plan>",
              "days": [
                {
                  "day": 1,
                  "title": "<focus area title>",
                  "topics": ["<topic1>", "<topic2>"],
                  "tasks": ["<specific task 1>", "<specific task 2>", "<specific task 3>"],
                  "practiceCount": <number of questions to practice>,
                  "estimatedHours": <hours>
                }
              ],
              "tips": ["<tip1>", "<tip2>", "<tip3>"],
              "expectedScoreAfter": <predicted readiness score after completing the plan>
            }
            
            Make it realistic, specific to %s hiring patterns, and focused on the weak areas.
            Return ONLY the JSON object.
            """.formatted(days, companyName, readinessScore, weakTopicsStr, companyName);
    }
}

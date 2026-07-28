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

        // Build a clear description of weak areas
        String weakTopicsStr;
        String weakTopicNote;
        if (weakTopics == null || weakTopics.isEmpty()) {
            weakTopicsStr  = "No specific weak areas identified yet";
            weakTopicNote  = "Cover all core placement topics comprehensively.";
        } else {
            weakTopicsStr  = String.join(", ", weakTopics);
            weakTopicNote  = "Dedicate extra time to: " + weakTopicsStr + ". " +
                             "These should appear in the earliest days so the student has time to revise.";
        }

        // Readiness band description
        String readinessBand;
        if      (readinessScore >= 70) readinessBand = "Good — minor polishing needed";
        else if (readinessScore >= 45) readinessBand = "Average — focused revision required";
        else if (readinessScore >= 20) readinessBand = "Below average — needs structured rebuilding";
        else                           readinessBand = "Beginner — start from fundamentals";

        // Company-specific context hints
        String companyHint = buildCompanyHint(companyName);

        return """
            You are a senior placement preparation expert who coaches students for top Indian tech companies.

            Generate a detailed, personalised %d-day study plan for the student below.

            == Student Profile ==
            Target Company: %s
            Current Readiness Score: %d%% (%s)
            Weak Topics: %s

            == Instructions ==
            %s
            %s

            == Output Format ==
            Return ONLY a valid JSON object in this exact structure (no extra text, no markdown fences):
            {
              "summary": "<2-3 sentences describing the plan strategy and what the student will achieve>",
              "days": [
                {
                  "day": 1,
                  "title": "<concise focus area, e.g. 'SQL Joins & Subqueries'>",
                  "topics": ["<specific topic 1>", "<specific topic 2>"],
                  "tasks": [
                    "<concrete task, e.g. 'Solve 10 SQL JOIN questions on SkillBridge'>",
                    "<concrete task>",
                    "<concrete task>"
                  ],
                  "practiceCount": <integer number of questions to attempt>,
                  "estimatedHours": <realistic hours for the day, integer>
                }
              ],
              "tips": ["<specific actionable tip 1>", "<tip 2>", "<tip 3>"],
              "expectedScoreAfter": <predicted readiness %% after completing the plan, integer 0-100>
            }

            Rules:
            - Every day entry MUST have "day", "title", "topics", "tasks", "practiceCount", "estimatedHours"
            - practiceCount should be 8-20 depending on the topic difficulty
            - estimatedHours should be 2-5 (realistic for a student)
            - Make tasks specific to %s — mention actual question types, patterns and rounds used there
            - Return ONLY the JSON. No preamble, no explanation, no markdown.
            """.formatted(
                days,
                companyName, readinessScore, readinessBand,
                weakTopicsStr,
                weakTopicNote,
                companyHint,
                companyName
            );
    }

    private String buildCompanyHint(String company) {
        return switch (company.toUpperCase()) {
            case "TCS" -> "TCS uses the TCS NQT exam with sections: Cognitive Skills (Verbal, Reasoning, Numerical), " +
                          "Programming (C/C++/Java/Python logic), and Advanced Coding. Focus heavily on aptitude and " +
                          "pattern-based questions. The verbal section carries significant weight.";
            case "INFOSYS" -> "Infosys InfyTQ focuses on: Reasoning Ability, Mathematical Ability, Verbal Ability, " +
                              "and Coding (Python/Java). Logical reasoning and pseudocode/output-based questions are common.";
            case "WIPRO" -> "Wipro NLTH (National Level Talent Hunt) tests: Aptitude, Written English, and Online Test. " +
                            "Coding round requires two programs in 60 minutes. Focus on basic DSA patterns.";
            case "ZOHO" -> "Zoho recruitment has 5+ rounds including aptitude, programming in C/Java, " +
                           "advanced programming, and a final technical interview. Strong coding skills in loops, " +
                           "arrays, strings, and OOP are essential. Time complexity awareness is tested.";
            case "AMAZON" -> "Amazon focuses on: OOP design, DSA (Trees, Graphs, DP, HashMaps), Leadership " +
                             "Principles in HR rounds, and system design. Expect LeetCode-medium to hard questions.";
            case "ACCENTURE" -> "Accenture ATAP has: Cognitive & Technical Assessment (pseudo-code, basic algorithms), " +
                                "Communication test, and Coding challenge. Focus on time complexity and basic data structures.";
            case "COGNIZANT" -> "Cognizant GenC tests: Aptitude, Logical, Verbal, Automata (coding in any language). " +
                                "The coding section has 2 questions focusing on arrays, strings, and basic algorithms.";
            case "CAPGEMINI" -> "Capgemini has a Game-based Assessment, Pseudocode test, Behavioural Assessment, and " +
                                "Technical/HR interviews. Focus on pseudocode interpretation, OOP concepts, and SDLC.";
            default -> "Focus on the standard placement preparation: Aptitude, Core CS subjects (OOP, DBMS, OS, Networks), " +
                       "Data Structures, and HR behavioural questions.";
        };
    }
}

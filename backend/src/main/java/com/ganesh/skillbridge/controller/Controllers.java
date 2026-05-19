package com.ganesh.skillbridge.controller;

import com.ganesh.skillbridge.dto.*;
import com.ganesh.skillbridge.entity.*;
import com.ganesh.skillbridge.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ── Auth ─────────────────────────────────────────────────────────────────────
@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "SkillBridge API"));
    }
}

// ── Company ───────────────────────────────────────────────────────────────────
@Tag(name = "Companies")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<Company>> getAll() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }
}

// ── Questions ─────────────────────────────────────────────────────────────────
@Tag(name = "Questions")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
class QuestionController {
    private final QuestionService questionService;
    private final AuthService authService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<QuestionResponse>> getByCompany(
            @PathVariable Long companyId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(
            questionService.getByCompany(companyId, category, difficulty, user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(questionService.getById(id, user.getId()));
    }

    @PostMapping
    public ResponseEntity<Question> addQuestion(@Valid @RequestBody QuestionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.addQuestion(req));
    }
}

// ── Practice ──────────────────────────────────────────────────────────────────
@Tag(name = "Practice")
@RestController
@RequestMapping("/api/practice")
@RequiredArgsConstructor
class PracticeController {
    private final PracticeService practiceService;
    private final AuthService authService;

    @PostMapping("/submit")
    public ResponseEntity<PracticeSubmissionResponse> submit(
            @Valid @RequestBody PracticeSubmissionRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(practiceService.submitAnswer(req, user));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PracticeAttempt>> history(
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(practiceService.getUserHistory(user.getId()));
    }
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
@Tag(name = "Dashboard")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
class DashboardController {
    private final DashboardService dashboardService;
    private final AuthService authService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> summary(
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(dashboardService.getDashboard(user));
    }

    @GetMapping("/readiness/{companyId}")
    public ResponseEntity<Map<String, Object>> readiness(
            @PathVariable Long companyId,
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        int score = dashboardService.getReadinessScore(user, companyId);
        com.ganesh.skillbridge.util.ReadinessScoreCalculator calc =
            new com.ganesh.skillbridge.util.ReadinessScoreCalculator();
        return ResponseEntity.ok(Map.of("score", score, "label", calc.getReadinessLabel(score)));
    }
}

// ── AI ────────────────────────────────────────────────────────────────────────
@Tag(name = "AI Feedback & Study Plan")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
class AiFeedbackController {
    private final AiFeedbackService aiFeedbackService;
    private final StudyPlanService studyPlanService;
    private final AuthService authService;

    @PostMapping("/feedback")
    public ResponseEntity<AiFeedbackResponse> getFeedback(
            @Valid @RequestBody AiFeedbackRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(aiFeedbackService.getFeedback(req, user));
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<AiFeedbackResponse>> allFeedbacks(
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(aiFeedbackService.getUserFeedbacks(user.getId()));
    }

    @PostMapping("/study-plan")
    public ResponseEntity<StudyPlanResponse> generatePlan(
            @Valid @RequestBody StudyPlanRequest req,
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(studyPlanService.generatePlan(req, user));
    }

    @GetMapping("/study-plans")
    public ResponseEntity<List<StudyPlanResponse>> getPlans(
            @AuthenticationPrincipal UserDetails ud) {
        User user = authService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(studyPlanService.getUserPlans(user.getId()));
    }
}

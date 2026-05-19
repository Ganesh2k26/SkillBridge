package com.ganesh.skillbridge.service;

import com.ganesh.skillbridge.dto.QuestionRequest;
import com.ganesh.skillbridge.dto.QuestionResponse;
import com.ganesh.skillbridge.entity.Company;
import com.ganesh.skillbridge.entity.Question;
import com.ganesh.skillbridge.exception.ResourceNotFoundException;
import com.ganesh.skillbridge.repository.PracticeAttemptRepository;
import com.ganesh.skillbridge.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepo;
    private final PracticeAttemptRepository attemptRepo;
    private final CompanyService companyService;

    public List<QuestionResponse> getByCompany(Long companyId, String category,
                                                String difficulty, Long userId) {
        List<Question> questions;
        if (category != null && difficulty != null)
            questions = questionRepo.findByCompanyIdAndCategoryAndDifficulty(companyId, category, difficulty);
        else if (category != null)
            questions = questionRepo.findByCompanyIdAndCategory(companyId, category);
        else if (difficulty != null)
            questions = questionRepo.findByCompanyIdAndDifficulty(companyId, difficulty);
        else
            questions = questionRepo.findByCompanyId(companyId);

        return questions.stream().map(q -> mapToResponse(q, userId)).collect(Collectors.toList());
    }

    public QuestionResponse getById(Long id, Long userId) {
        Question q = questionRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + id));
        return mapToResponse(q, userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Question addQuestion(QuestionRequest req) {
        Company company = companyService.getById(req.getCompanyId());
        Question q = new Question();
        q.setTitle(req.getTitle());
        q.setDescription(req.getDescription());
        q.setCategory(req.getCategory());
        q.setTopic(req.getTopic());
        q.setDifficulty(req.getDifficulty());
        q.setCorrectAnswer(req.getCorrectAnswer());
        q.setExplanation(req.getExplanation());
        q.setQuestionType(req.getQuestionType() != null ? req.getQuestionType() : "MCQ");
        q.setOptionA(req.getOptionA());
        q.setOptionB(req.getOptionB());
        q.setOptionC(req.getOptionC());
        q.setOptionD(req.getOptionD());
        q.setPoints(req.getPoints() != null ? req.getPoints() : 10);
        q.setCompany(company);
        return questionRepo.save(q);
    }

    private QuestionResponse mapToResponse(Question q, Long userId) {
        String status = "NOT_STARTED";
        if (userId != null) {
            var attempt = attemptRepo.findByUserIdAndQuestionId(userId, q.getId());
            if (attempt.isPresent()) status = attempt.get().getStatus();
        }
        return QuestionResponse.builder()
            .id(q.getId())
            .title(q.getTitle())
            .description(q.getDescription())
            .category(q.getCategory())
            .topic(q.getTopic())
            .difficulty(q.getDifficulty())
            .questionType(q.getQuestionType())
            .points(q.getPoints())
            .optionA(q.getOptionA())
            .optionB(q.getOptionB())
            .optionC(q.getOptionC())
            .optionD(q.getOptionD())
            .companyId(q.getCompany().getId())
            .companyName(q.getCompany().getName())
            .status(status)
            .build();
    }
}

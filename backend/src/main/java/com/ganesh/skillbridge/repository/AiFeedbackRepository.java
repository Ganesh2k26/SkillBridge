package com.ganesh.skillbridge.repository;

import com.ganesh.skillbridge.entity.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {
    List<AiFeedback> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<AiFeedback> findByUserIdAndQuestionId(Long userId, Long questionId);
}

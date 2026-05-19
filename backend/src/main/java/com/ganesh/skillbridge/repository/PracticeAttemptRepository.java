package com.ganesh.skillbridge.repository;

import com.ganesh.skillbridge.entity.PracticeAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PracticeAttemptRepository extends JpaRepository<PracticeAttempt, Long> {
    List<PracticeAttempt> findByUserIdOrderByAttemptedAtDesc(Long userId);
    Optional<PracticeAttempt> findByUserIdAndQuestionId(Long userId, Long questionId);

    long countByUserId(Long userId);
    long countByUserIdAndIsCorrect(Long userId, boolean isCorrect);

    @Query("SELECT pa FROM PracticeAttempt pa WHERE pa.user.id = :userId AND pa.question.company.id = :companyId ORDER BY pa.attemptedAt DESC")
    List<PracticeAttempt> findByUserIdAndCompanyId(@Param("userId") Long userId,
                                                    @Param("companyId") Long companyId);

    @Query("SELECT COUNT(pa) FROM PracticeAttempt pa WHERE pa.user.id = :userId AND pa.question.company.id = :companyId")
    long countByUserIdAndCompanyId(@Param("userId") Long userId, @Param("companyId") Long companyId);

    @Query("SELECT COUNT(pa) FROM PracticeAttempt pa WHERE pa.user.id = :userId AND pa.question.company.id = :companyId AND pa.isCorrect = true")
    long countCorrectByUserIdAndCompanyId(@Param("userId") Long userId, @Param("companyId") Long companyId);

    @Query("SELECT pa.question.topic, COUNT(pa), SUM(CASE WHEN pa.isCorrect = true THEN 1 ELSE 0 END) FROM PracticeAttempt pa WHERE pa.user.id = :userId GROUP BY pa.question.topic")
    List<Object[]> findTopicStatsByUserId(@Param("userId") Long userId);
}

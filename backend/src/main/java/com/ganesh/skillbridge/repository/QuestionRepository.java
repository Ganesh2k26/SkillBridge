package com.ganesh.skillbridge.repository;

import com.ganesh.skillbridge.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCompanyId(Long companyId);
    List<Question> findByCompanyIdAndCategory(Long companyId, String category);
    List<Question> findByCategory(String category);
    List<Question> findByCompanyIdAndDifficulty(Long companyId, String difficulty);
    List<Question> findByCompanyIdAndCategoryAndDifficulty(Long companyId, String category, String difficulty);

    @Query("SELECT DISTINCT q.topic FROM Question q WHERE q.company.id = :companyId AND q.category = :category")
    List<String> findDistinctTopicsByCompanyAndCategory(@Param("companyId") Long companyId,
                                                        @Param("category") String category);

    long countByCompanyId(Long companyId);
}

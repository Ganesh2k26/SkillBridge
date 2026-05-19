package com.ganesh.skillbridge.repository;

import com.ganesh.skillbridge.entity.TopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TopicProgressRepository extends JpaRepository<TopicProgress, Long> {
    List<TopicProgress> findByUserId(Long userId);
    List<TopicProgress> findByUserIdAndStrengthLevel(Long userId, String strengthLevel);
    Optional<TopicProgress> findByUserIdAndTopicAndCategory(Long userId, String topic, String category);
}

package ru.itmo.saferoad.learning.domain.repository;

import java.util.List;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;
import ru.itmo.saferoad.learning.domain.UserTopicProgressId;

public interface UserTopicProgressRepository
    extends JpaRepository<UserTopicProgress, UserTopicProgressId> {
  List<UserTopicProgress> findByUserId(Long userId);

	@Query("""
			    SELECT COUNT(p) FROM UserTopicProgress p
			        JOIN Topic t on p.topicId = t.id
			    WHERE p.userId = :userId
			    AND t.section.id = :sectionId
			    AND p.status = 'COMPLETED'
			""")
	long countCompletedTopicsInSection(@NonNull Long userId, @NonNull Integer sectionId);
}

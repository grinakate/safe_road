package ru.itmo.saferoad.content.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.content.domain.Topic;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Integer> {

	List<Topic> findBySectionId(Integer sectionId);
	Optional<Topic> findFirstBySectionIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(Integer sectionId, Integer orderIndex);
}

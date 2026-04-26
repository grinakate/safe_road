package ru.itmo.saferoad.content.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.content.domain.Topic;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Integer> {

	List<Topic> findBySectionId(Integer sectionId);
}

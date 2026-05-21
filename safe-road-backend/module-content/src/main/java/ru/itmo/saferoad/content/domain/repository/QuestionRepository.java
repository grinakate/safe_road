package ru.itmo.saferoad.content.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.content.domain.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	List<Question> findByTopicId(Integer topicId);
	List<Question> findByTopicSectionId(Integer sectionId);
}

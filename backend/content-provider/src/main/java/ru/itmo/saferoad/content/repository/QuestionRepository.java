package ru.itmo.saferoad.content.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.content.domain.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
  List<Question> findByTopicId(Integer topicId);
}

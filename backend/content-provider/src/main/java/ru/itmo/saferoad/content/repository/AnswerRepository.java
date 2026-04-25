package ru.itmo.saferoad.content.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.content.domain.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
  List<Answer> findByQuestionId(Long questionId);
}

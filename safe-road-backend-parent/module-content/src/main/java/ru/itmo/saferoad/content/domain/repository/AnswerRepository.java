package ru.itmo.saferoad.content.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.content.domain.Answer;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

	List<Answer> findByQuestionId(Long questionId);

	Iterable<Integer> id(Integer id);
}

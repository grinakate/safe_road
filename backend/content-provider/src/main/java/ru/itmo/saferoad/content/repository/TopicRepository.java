package ru.itmo.saferoad.content.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.content.domain.Topic;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
  List<Topic> findBySectionId(Integer sectionId);
}

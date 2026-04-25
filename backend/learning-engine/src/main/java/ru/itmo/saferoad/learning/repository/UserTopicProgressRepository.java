package ru.itmo.saferoad.learning.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;
import ru.itmo.saferoad.learning.domain.UserTopicProgressId;

public interface UserTopicProgressRepository
    extends JpaRepository<UserTopicProgress, UserTopicProgressId> {
  List<UserTopicProgress> findByIdUserId(Long userId);
}

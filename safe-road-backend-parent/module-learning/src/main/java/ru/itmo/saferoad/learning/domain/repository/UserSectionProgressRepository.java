package ru.itmo.saferoad.learning.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.learning.domain.UserSectionProgress;
import ru.itmo.saferoad.learning.domain.UserSectionProgressId;

public interface UserSectionProgressRepository
    extends JpaRepository<UserSectionProgress, UserSectionProgressId> {
  List<UserSectionProgress> findByUserId(Long userId);
}

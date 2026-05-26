package ru.itmo.saferoad.learning.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.learning.domain.TestSession;

public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
}

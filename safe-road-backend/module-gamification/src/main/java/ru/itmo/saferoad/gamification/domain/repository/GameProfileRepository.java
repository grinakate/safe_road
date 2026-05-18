package ru.itmo.saferoad.gamification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.GameProfile;

public interface GameProfileRepository extends JpaRepository<GameProfile, Long> {
}

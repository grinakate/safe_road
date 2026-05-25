package ru.itmo.saferoad.gamification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Integer> {
}

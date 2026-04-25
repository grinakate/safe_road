package ru.itmo.saferoad.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.domain.entity.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}

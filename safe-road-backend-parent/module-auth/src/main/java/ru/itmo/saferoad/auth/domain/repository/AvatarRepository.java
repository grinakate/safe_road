package ru.itmo.saferoad.auth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.auth.domain.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Integer> {}

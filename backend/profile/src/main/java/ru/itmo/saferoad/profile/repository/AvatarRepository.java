package ru.itmo.saferoad.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.profile.domain.Avatar;

public interface AvatarRepository extends JpaRepository<Avatar, Integer> {}

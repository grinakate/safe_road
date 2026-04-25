package ru.itmo.saferoad.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.profile.domain.Level;

public interface LevelRepository extends JpaRepository<Level, Integer> {}

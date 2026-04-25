package ru.itmo.saferoad.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.content.domain.Section;

public interface SectionRepository extends JpaRepository<Section, Integer> {}

package ru.itmo.saferoad.content.domain.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.content.domain.Section;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Integer> {

	@NotNull
	@Query("SELECT s FROM Section s LEFT JOIN FETCH s.topics ORDER BY s.orderIndex ASC")
	List<Section> findAllWithTopics();

	@NotNull
	Optional<Section> findFirstByOrderByOrderIndexAsc();

	Optional<Section> findFirstByOrderIndexGreaterThanOrderByOrderIndexAsc(Integer orderIndex);

}

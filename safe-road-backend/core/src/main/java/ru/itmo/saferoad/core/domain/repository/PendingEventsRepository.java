package ru.itmo.saferoad.core.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;

import java.util.List;

public interface PendingEventsRepository extends JpaRepository<PendingEvent, Long> {

	List<PendingEvent> findByStatusAndTypeIn(@Param("status") ProgressStatus status,
											 @Param("type") List<EventType> types,
											 Pageable pageable);

	@Modifying
	@Query("UPDATE PendingEvent pe SET pe.status = :newStatus WHERE pe.id = :id")
	void updateStatus(@Param("id") Long id,
					  @Param("newStatus") ProgressStatus newStatus);
}

package ru.itmo.saferoad.core.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itmo.saferoad.core.domain.PendingEvents;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;

import java.util.List;

@Repository
public interface PendingEventsRepository extends JpaRepository<PendingEvents, Long> {

    @Query("SELECT pe FROM PendingEvents pe WHERE pe.status = :status AND pe.type = :type")
    List<PendingEvents> findByStatusAndType(@Param("status") ProgressStatus status, @Param("type") EventType type);
    
    @Modifying
    @Query("UPDATE PendingEvents pe SET pe.status = :newStatus WHERE pe.id = :id")
    void updateStatus(@Param("id") Long id, @Param("newStatus") ProgressStatus newStatus);
}

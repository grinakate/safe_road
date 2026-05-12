package ru.itmo.saferoad.notifications.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.notifications.domain.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserId(Long userId);

	List<Notification> findByUserIdAndIsReadFalse(Long userId);

	@Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
	List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

	long countByUserIdAndIsReadFalse(Long userId);
}


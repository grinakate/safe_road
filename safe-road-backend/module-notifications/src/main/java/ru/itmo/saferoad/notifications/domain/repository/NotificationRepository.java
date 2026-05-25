package ru.itmo.saferoad.notifications.domain.repository;

import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.notifications.domain.Notification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserId(Long userId);

	@NotNull
	@Query(value = "select * from notifications n " +
				   "where n.user_id in (:userIds) " +
				   "and n.is_read = false " +
				   "order by n.created_at asc " +
				   "for update skip locked", nativeQuery = true)
	List<Notification> findByUserIdInAndIsReadFalse(Set<Long> userIds);

	@NotNull
	@Query("select n.id from Notification n where n.userId in (:userIds) and n.isRead = false")
	List<Long> findIdsByUserIdInAndIsReadFalse(Set<Long> userIds);

	@Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
	List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

	long countByUserIdAndIsReadFalse(Long userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select n from Notification n where n.id = :notificationId")
	Optional<Notification> findByIdAndLock(@NotNull Long notificationId);
}


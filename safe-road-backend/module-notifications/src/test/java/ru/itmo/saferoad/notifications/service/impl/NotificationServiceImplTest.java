package ru.itmo.saferoad.notifications.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.domain.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

	@Mock
	private NotificationRepository repository;

	@Mock
	private CurrentTime currentTime;

	@InjectMocks
	private NotificationServiceImpl service;

	@Test
	void create_shouldPopulateFieldsAndSave() {
		when(currentTime.nowDateTime()).thenReturn(LocalDateTime.of(2025, 1, 1, 0, 0));
		when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

		Notification saved = service.create(11L, ru.itmo.saferoad.core.event.dto.notifications.NotificationType.SYSTEM_MESSAGE, "payload");

		assertEquals(11L, saved.getUserId());
		assertEquals("SYSTEM_MESSAGE", saved.getType());
		assertEquals("payload", saved.getContent());
		assertFalse(saved.getIsRead());
		assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), saved.getCreatedAt());
		verify(repository, times(1)).save(any(Notification.class));
	}

	@Test
	void markAsRead_whenExists_marksAndSaves() {
		Notification n = new Notification();
		n.setId(5L);
		n.setIsRead(false);
		when(repository.findById(5L)).thenReturn(Optional.of(n));
		when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		service.markAsRead(5L);

		assertTrue(n.getIsRead());
		verify(repository, times(1)).save(n);
	}

	@Test
	void markAllAsRead_shouldSaveAll() {
		Notification a = new Notification();
		a.setId(1L);
		a.setIsRead(false);
		Notification b = new Notification();
		b.setId(2L);
		b.setIsRead(false);
		when(repository.findByUserIdInAndIsReadFalse(Set.of(3L))).thenReturn(List.of(a, b));

		service.markAllAsRead(3L);

		assertTrue(a.getIsRead());
		assertTrue(b.getIsRead());
		verify(repository, times(1)).saveAll(List.of(a, b));
	}

	@Test
	void existingByIdAndLock_whenMissing_throws() {
		when(repository.findByIdAndLock(99L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> service.existingByIdAndLock(99L));
	}

	@Test
	void getByUserId_shouldReturnOrderedList() {
		Notification a = new Notification();
		a.setId(1L);
		Notification b = new Notification();
		b.setId(2L);
		when(repository.findByUserIdOrderByCreatedAtDesc(7L)).thenReturn(List.of(a, b));

		var res = service.getByUserId(7L);
		assertEquals(2, res.size());
		assertEquals(a, res.get(0));
		verify(repository, times(1)).findByUserIdOrderByCreatedAtDesc(7L);
	}

	@Test
	void getUnreadByUserId_shouldReturnUnread() {
		Notification a = new Notification();
		a.setId(3L);
		when(repository.findByUserIdInAndIsReadFalse(Set.of(8L))).thenReturn(List.of(a));

		var res = service.getUnreadByUserId(8L);
		assertEquals(1, res.size());
		assertEquals(a, res.get(0));
	}

	@Test
	void getUnreadIdsByUserIds_shouldReturnIds() {
		when(repository.findIdsByUserIdInAndIsReadFalse(Set.of(1L, 2L))).thenReturn(List.of(10L, 11L));

		var ids = service.getUnreadIdsByUserIds(Set.of(1L, 2L));
		assertEquals(2, ids.size());
		assertEquals(10L, ids.get(0));
	}

	@Test
	void getUnreadByUserIds_shouldReturnList() {
		Notification n = new Notification();
		n.setId(4L);
		when(repository.findByUserIdInAndIsReadFalse(Set.of(4L, 5L))).thenReturn(List.of(n));

		var res = service.getUnreadByUserIds(Set.of(4L, 5L));
		assertEquals(1, res.size());
	}

	@Test
	void getUnreadCount_shouldReturnCountFromRepository() {
		when(repository.countByUserIdAndIsReadFalse(9L)).thenReturn(7L);

		long count = service.getUnreadCount(9L);
		assertEquals(7L, count);
	}

	@Test
	void save_shouldDelegateToRepository() {
		Notification n = new Notification();
		n.setId(88L);
		when(repository.save(n)).thenReturn(n);

		var res = service.save(n);
		assertEquals(n, res);
	}
}


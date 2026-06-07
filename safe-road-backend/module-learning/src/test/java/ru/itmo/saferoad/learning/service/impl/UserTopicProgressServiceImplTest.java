package ru.itmo.saferoad.learning.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;
import ru.itmo.saferoad.learning.domain.UserTopicProgressId;
import ru.itmo.saferoad.learning.domain.repository.UserTopicProgressRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTopicProgressServiceImplTest {

	@Mock
	private UserTopicProgressRepository repository;

	@InjectMocks
	private UserTopicProgressServiceImpl service;

	@Captor
	private ArgumentCaptor<UserTopicProgress> progressCaptor;

	@BeforeEach
	void init() {
		// MockitoExtension takes care of initialization
	}

	@Test
	void getByUserId_delegatesToRepository() {
		UserTopicProgress p1 = new UserTopicProgress();
		p1.setUserId(1L);
		p1.setTopicId(10);
		when(repository.findByUserId(1L)).thenReturn(List.of(p1));

		var res = service.getByUserId(1L);

		assertEquals(List.of(p1), res);
		verify(repository).findByUserId(1L);
	}

	@Test
	void upsertStatus_whenNotExists_createsAndSaves() {
		when(repository.findById(any(UserTopicProgressId.class))).thenReturn(Optional.empty());
		when(repository.save(any(UserTopicProgress.class))).thenAnswer(i -> i.getArgument(0));

		UserTopicProgress res = service.upsertStatus(1L, 2, ProgressStatus.UNLOCKED);

		assertEquals(1L, res.getUserId());
		assertEquals(2, res.getTopicId());
		assertEquals(ProgressStatus.UNLOCKED, res.getStatus());
		verify(repository).save(res);
	}

	@Test
	void upsertStatus_updatesExisting() {
		UserTopicProgress existing = new UserTopicProgress();
		existing.setUserId(3L);
		existing.setTopicId(30);
		existing.setStatus(ProgressStatus.UNLOCKED);
		when(repository.findById(new UserTopicProgressId(3L, 30))).thenReturn(Optional.of(existing));
		when(repository.save(any(UserTopicProgress.class))).thenAnswer(i -> i.getArgument(0));

		var res = service.upsertStatus(3L, 30, ProgressStatus.IN_PROGRESS);

		assertEquals(ProgressStatus.IN_PROGRESS, res.getStatus());
		verify(repository).save(existing);
	}

	@Test
	void countCompletedTopicsInSection_delegates() {
		when(repository.countCompletedTopicsInSection(5L, 2)).thenReturn(7);

		int cnt = service.countCompletedTopicsInSection(5L, 2);

		assertEquals(7, cnt);
		verify(repository).countCompletedTopicsInSection(5L, 2);
	}

	@Test
	void unlockTopic_whenLocked_setsUnlockedAndSaves() {
		UserTopicProgress existing = new UserTopicProgress();
		existing.setStatus(ProgressStatus.LOCKED);
		when(repository.findById(new UserTopicProgressId(1L, 3))).thenReturn(Optional.of(existing));
		when(repository.save(any(UserTopicProgress.class))).thenAnswer(i -> i.getArgument(0));

		Topic topic = new Topic();
		topic.setId(3);
		service.unlockTopic(1L, topic);

		assertEquals(ProgressStatus.UNLOCKED, existing.getStatus());
		verify(repository).save(existing);
	}

	@Test
	void unlockTopic_whenNoExisting_createsUnlocked() {
		Topic t = new Topic();
		t.setId(77);
		when(repository.findById(new UserTopicProgressId(9L, 77))).thenReturn(Optional.empty());
		when(repository.save(any(UserTopicProgress.class))).thenAnswer(inv -> inv.getArgument(0));

		service.unlockTopic(9L, t);

		verify(repository).save(progressCaptor.capture());
		UserTopicProgress saved = progressCaptor.getValue();
		assertEquals(9L, saved.getUserId());
		assertEquals(77, saved.getTopicId());
		assertEquals(ProgressStatus.UNLOCKED, saved.getStatus());
	}

	@Test
	void unlockTopic_whenAlreadyUnlocked_doesNotChange() {
		Topic t = new Topic();
		t.setId(99);
		UserTopicProgress existing = new UserTopicProgress();
		existing.setUserId(11L);
		existing.setTopicId(99);
		existing.setStatus(ProgressStatus.UNLOCKED);
		when(repository.findById(new UserTopicProgressId(11L, 99))).thenReturn(Optional.of(existing));
		when(repository.save(any(UserTopicProgress.class))).thenAnswer(inv -> inv.getArgument(0));

		service.unlockTopic(11L, t);

		verify(repository).save(existing);
		assertEquals(ProgressStatus.UNLOCKED, existing.getStatus());
	}

	@Test
	void updateProgress_updatesStatusOnExisting() {
		UserTopicProgress existing = new UserTopicProgress();
		existing.setUserId(12L);
		existing.setTopicId(120);
		existing.setStatus(ProgressStatus.UNLOCKED);
		when(repository.findById(new UserTopicProgressId(12L, 120))).thenReturn(Optional.of(existing));

		service.updateProgress(12L, 120, ProgressStatus.COMPLETED);

		assertEquals(ProgressStatus.COMPLETED, existing.getStatus());
		verify(repository, never()).save(any()); // method does not save
	}

	@Test
	void upsertStatus_whenSaveThrows_propagatesException() {
		when(repository.findById(any(UserTopicProgressId.class))).thenReturn(Optional.empty());
		when(repository.save(any(UserTopicProgress.class))).thenThrow(new RuntimeException("boom"));

		org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
				() -> service.upsertStatus(2L, 3, ProgressStatus.IN_PROGRESS));
	}
}


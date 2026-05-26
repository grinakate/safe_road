package ru.itmo.saferoad.learning.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;
import ru.itmo.saferoad.learning.domain.UserTopicProgressId;
import ru.itmo.saferoad.learning.domain.repository.UserTopicProgressRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTopicProgressServiceImplTest {

	@Mock
	private UserTopicProgressRepository repository;

	@InjectMocks
	private UserTopicProgressServiceImpl service;

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
}


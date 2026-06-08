package ru.itmo.saferoad.gamification.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.saferoad.app.SafeRoadApplication;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.gamification.config.GamificationProperties;
import ru.itmo.saferoad.gamification.domain.Avatar;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.Level;
import ru.itmo.saferoad.gamification.domain.repository.AvatarRepository;
import ru.itmo.saferoad.gamification.domain.repository.GameProfileRepository;
import ru.itmo.saferoad.gamification.domain.repository.LevelRepository;
import ru.itmo.saferoad.gamification.domain.repository.UserMetricRepository;
import ru.itmo.saferoad.gamification.domain.repository.XpHistoryRepository;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Profile("test")
@Transactional
@Testcontainers
@SpringBootTest(classes = SafeRoadApplication.class)
public class TestCompletedEventProcessorIT {

	@Container
	protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
			.withDatabaseName("test")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
		registry.add("spring.jpa.ddl-auto", () -> "create-drop");
		registry.add("spring.sql.init.mode", () -> "always");
		registry.add("spring.sql.init.schema-locations", () -> "classpath:initial.sql");
		registry.add("spring.jpa.defer-datasource-initialization", () -> true);
		registry.add("jwt.secret-key", () -> "0123456789abcdef0123456789abcdef");
		registry.add("jwt.expiration-ms", () -> 86400000L);
	}

	@Autowired
	private JsonMapper jsonMapper;

	@Autowired
	private TestCompletedEventProcessor processor;

	@Autowired
	private LevelRepository levelRepository;

	@Autowired
	private AvatarRepository avatarRepository;

	@Autowired
	private GameProfileRepository gameProfileRepository;

	@Autowired
	private UserMetricRepository userMetricRepository;

	@Autowired
	private XpHistoryRepository xpHistoryRepository;

	@Autowired
	private PendingEventsRepository pendingEventsRepository;

	@Autowired
	private GamificationProperties gamificationProperties;

	@Autowired
	private CurrentTime currentTime;

	@Test
	@Sql(scripts = {"classpath:prepared-data.sql"})
	void processEvent_updatesGameProfileAndCreatesNotification() throws Exception {
		Long userId = 7L; // prepared-data.sql creates user id 7

		// Ensure levels and avatars exist in DB
		Level level1 = levelRepository.findByNumber(1).orElseGet(() -> {
			Level l = new Level();
			l.setNumber(1);
			l.setTitle("Novice");
			l.setXpThreshold(100);
			return levelRepository.save(l);
		});

		Level level2 = levelRepository.findByNumber(2).orElseGet(() -> {
			Level l = new Level();
			l.setNumber(2);
			l.setTitle("Apprentice");
			l.setXpThreshold(250);
			return levelRepository.save(l);
		});

		// Use an existing avatar if present, otherwise create one
		var avatars = avatarRepository.findAll();
		Avatar avatar;
		if (avatars.isEmpty()) {
			Avatar av = new Avatar();
			av.setUrl("/static/avatars/test.png");
			av.setMinLevel(1);
			avatar = avatarRepository.save(av);
		} else {
			avatar = avatars.get(0);
		}

		GameProfile profile = gameProfileRepository.findById(userId).orElseGet(() -> {
			GameProfile p = new GameProfile();
			p.setUserId(userId);
			p.setLevel(level1);
			p.setAvatar(avatar);
			p.setXp(0);
			p.setCurrentStreak(0);
			p.setTotalActiveDays(0);
			p.setIsLeaderboardParticipant(false);
			return gameProfileRepository.save(p);
		});

		// Configure gamification properties
		gamificationProperties.setBaseXpPerQuestion(10);
		gamificationProperties.getQuestionTypeCoefficients().put("CHOICE", 1.0);

		TestSessionCompletedEvent.QuestionDetail detail = TestSessionCompletedEvent.QuestionDetail.builder()
				.type("CHOICE")
				.difficulty(1)
				.isCorrect(true)
				.build();

		TestSessionCompletedEvent payload = TestSessionCompletedEvent.builder()
				.userId(userId)
				.details(List.of(detail))
				.build();

		PendingEvent event = new PendingEvent();
		event.setContent(jsonMapper.writeValueAsString(payload));

		// When: Process the event (triggers all listeners synchronously)
		processor.processEvent(event);

		// Then: Verify GameProfile was updated with XP and notification was created
		GameProfile updated = gameProfileRepository.findById(userId).orElseThrow();
		assertThat(updated.getXp()).isEqualTo(10); // XP increased by 10
		assertThat(updated.getLevel().getNumber()).isEqualTo(1); // Still level 1 (needs 100 XP to advance)

		// Verify pending notification was created
		var allPending = pendingEventsRepository.findAll();
		boolean hasNotification = allPending.stream()
				.anyMatch(pe -> pe.getType().name().equals("CREATE_NOTIFICATION"));
		assertThat(hasNotification).isTrue();
	}

	@Test
	@Sql(scripts = {"classpath:prepared-data.sql"})
	void processEvent_triggersLevelUp() throws Exception {
		Long userId = 7L;

		// Setup levels
		Level level1 = levelRepository.findByNumber(1).orElseGet(() -> {
			Level l = new Level();
			l.setNumber(1);
			l.setTitle("Novice");
			l.setXpThreshold(100);
			return levelRepository.save(l);
		});

		Level level2 = levelRepository.findByNumber(2).orElseGet(() -> {
			Level l = new Level();
			l.setNumber(2);
			l.setTitle("Apprentice");
			l.setXpThreshold(250);
			return levelRepository.save(l);
		});

		var avatars = avatarRepository.findAll();
		Avatar avatar = avatars.isEmpty() ? avatarRepository.save(new Avatar()) : avatars.get(0);

		GameProfile profile = gameProfileRepository.findById(userId).orElseGet(() -> {
			GameProfile p = new GameProfile();
			p.setUserId(userId);
			p.setLevel(level1);
			p.setAvatar(avatar);
			p.setXp(95); // Close to level 2 threshold (100)
			p.setCurrentStreak(0);
			p.setTotalActiveDays(0);
			p.setIsLeaderboardParticipant(false);
			return gameProfileRepository.save(p);
		});

		gamificationProperties.setBaseXpPerQuestion(10);
		gamificationProperties.getQuestionTypeCoefficients().put("CHOICE", 1.0);

		// Create an event that will earn 10 XP
		TestSessionCompletedEvent.QuestionDetail detail = TestSessionCompletedEvent.QuestionDetail.builder()
				.type("CHOICE")
				.difficulty(1)
				.isCorrect(true)
				.build();

		TestSessionCompletedEvent payload = TestSessionCompletedEvent.builder()
				.userId(userId)
				.details(List.of(detail))
				.build();

		PendingEvent event = new PendingEvent();
		event.setContent(jsonMapper.writeValueAsString(payload));

		// When: Process triggers level up
		processor.processEvent(event);

		// Then: Verify level up occurred
		GameProfile updated = gameProfileRepository.findById(userId).orElseThrow();
		assertThat(updated.getXp()).isEqualTo(105); // 95 + 10
		assertThat(updated.getLevel().getNumber()).isEqualTo(2); // Leveled up to 2

		// Verify notification about level up was created
		var allPending = pendingEventsRepository.findAll();
		boolean hasNotification = allPending.stream()
				.anyMatch(pe -> pe.getType().name().equals("CREATE_NOTIFICATION"));
		assertThat(hasNotification).isTrue();
	}
}

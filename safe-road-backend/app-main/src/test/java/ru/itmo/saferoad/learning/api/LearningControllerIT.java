package ru.itmo.saferoad.learning.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.saferoad.app.SafeRoadApplication;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;
import ru.itmo.saferoad.learning.domain.TestSession;
import ru.itmo.saferoad.learning.domain.repository.TestSessionRepository;
import ru.itmo.saferoad.learning.domain.repository.UserTopicProgressRepository;
import ru.itmo.saferoad.learning.dto.SubmitSessionAnswerRequest;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Profile("test")
@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = SafeRoadApplication.class)
class LearningControllerIT {

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

	protected MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Autowired
	private JsonMapper jsonMapper;

	@Autowired
	private UserTopicProgressRepository userTopicProgressRepository;

	@Autowired
	private TestSessionRepository testSessionRepository;

	@Autowired
	private PendingEventsRepository pendingEventsRepository;

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void submitSessionAnswer_shouldFinishSession_andPublishEvent_andUpdateCounts() throws Exception {
		// Given
		long initialEventsCount = pendingEventsRepository.count();
		long testSessionId = 400;
		long userId = 7;
		int topicId = 200;
		int nextTopicId = 201;
		long questionId = 300;
		var request = new SubmitSessionAnswerRequest(Map.of(questionId, 1));

		// When
		String response = mockMvc.perform(post("/api/v1/learning/test/{sessionId}/submit-answer", testSessionId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		// Then
		TestSession finished = testSessionRepository.findById(testSessionId).orElseThrow();
		assertThat(finished.getStatus()).isEqualTo(ProgressStatus.COMPLETED);
		assertThat(finished.getCorrectCount()).isEqualTo(1);
		assertThat(finished.getFinishedAt()).isNotNull();

		var dto = jsonMapper.readTree(response);
		assertThat(dto.get("totalQuestions").asInt()).isEqualTo(1);
		assertThat(dto.get("correctCount").asInt()).isEqualTo(1);

		assertThat(pendingEventsRepository.count()).isEqualTo(initialEventsCount + 1);
		var events = pendingEventsRepository.findByStatusAndTypeIn(
				ProgressStatus.PENDING,
				java.util.List.of(EventType.TEST_SESSIONS_COMPLETED),
				PageRequest.of(0, 20)
		);
		assertThat(events).isNotEmpty();
		assertThat(events).anyMatch(event ->
				event.getType() == EventType.TEST_SESSIONS_COMPLETED
				&& event.getStatus() == ProgressStatus.PENDING
				&& event.getContent().contains("\"userId\":" + userId)
				&& event.getContent().contains("\"topicId\":" + topicId)
		);

		var progresses = userTopicProgressRepository.findByUserId(userId);
		var current = progresses.stream().filter(p -> p.getTopicId().equals(topicId)).findFirst().orElseThrow();
		var next = progresses.stream().filter(p -> p.getTopicId().equals(nextTopicId)).findFirst().orElseThrow();
		assertThat(current.getStatus()).isEqualTo(ProgressStatus.COMPLETED);
		assertThat(next.getStatus()).isEqualTo(ProgressStatus.UNLOCKED);
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void startTest_shouldCreateSession() throws Exception {
		var req = Map.of("topicId", 200, "mode", "TOPIC");

		String resp = mockMvc.perform(post("/api/v1/learning/test/start")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(req)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(resp);
		assertThat(node.get("sessionId").asLong()).isPositive();
		assertThat(node.get("totalQuestions").asInt()).isGreaterThanOrEqualTo(1);
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getTestQuestions_shouldReturnQuestions() throws Exception {
		long sessionId = 400; // prepared-data.sql

		String qsResp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/learning/test/" + sessionId + "/questions")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var arr = jsonMapper.readTree(qsResp);
		assertThat(arr.isArray()).isTrue();
		assertThat(arr.size()).isEqualTo(1);
		assertThat(arr.get(0).get("id").asLong()).isEqualTo(300);
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getStatistics_shouldReturnSectionStats() throws Exception {
		String statsResp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/learning/me/statistics")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var statsArr = jsonMapper.readTree(statsResp);
		assertThat(statsArr.isArray()).isTrue();
		boolean foundSection = false;
		for (var sec : statsArr) {
			if (sec.get("sectionId").asInt() == 100) {
				foundSection = true;
				var topicStats = sec.get("topicStatistics");
				assertThat(topicStats.isArray()).isTrue();
			}
		}
		assertThat(foundSection).isTrue();
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getRoadMap_shouldReturnMap() throws Exception {
		String mapResp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/learning/roadmap")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var mapArr = jsonMapper.readTree(mapResp);
		assertThat(mapArr.isArray()).isTrue();
		boolean found = false;
		for (var s : mapArr) {
			if (s.get("id").asInt() == 100) {
				found = true;
				var topics = s.get("topics");
				assertThat(topics.isArray()).isTrue();
				assertThat(topics.size()).isGreaterThanOrEqualTo(2);
			}
		}
		assertThat(found).isTrue();
	}
}


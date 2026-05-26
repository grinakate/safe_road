package ru.itmo.saferoad.gamification.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Profile;
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
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Profile("test")
@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = SafeRoadApplication.class)
class GamificationControllerIT {
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

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getLeaderboard_shouldReturnList_andContainCurrentUser() throws Exception {
		String resp = mockMvc.perform(get("/api/v1/gamification/leaderboard").param("timeframe", "all")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var arr = jsonMapper.readTree(resp);
		assertThat(arr.isArray()).isTrue();
		boolean hasCurrent = false;
		for (var n : arr) {
			if (n.has("isCurrentUser") && n.get("isCurrentUser").asBoolean(false)) {
				hasCurrent = true;
			}
		}
		assertThat(hasCurrent).isTrue();
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getMe_shouldReturnGameProfile() throws Exception {
		String resp = mockMvc.perform(get("/api/v1/gamification/me").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(resp);
		assertThat(node.get("name").textValue()).isNotBlank();
		assertThat(node.get("level").asInt()).isGreaterThanOrEqualTo(1);
		assertThat(node.get("avatar").get("id").asInt()).isGreaterThanOrEqualTo(10);
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void changeMyAvatar_shouldReturnNewAvatarDto() throws Exception {
		var payload = Map.of("avatarId", 10);
		String resp = mockMvc.perform(post("/api/v1/gamification/me/avatar")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(payload)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(resp);
		assertThat(node.get("id").asInt()).isEqualTo(10);
		assertThat(node.get("isAvailable").asBoolean()).isTrue();
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getMyAchievements_shouldReturnList() throws Exception {
		String resp = mockMvc.perform(get("/api/v1/gamification/me/achievements").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var arr = jsonMapper.readTree(resp);
		assertThat(arr.isArray()).isTrue();
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getAvatars_shouldReturnList() throws Exception {
		String resp = mockMvc.perform(get("/api/v1/gamification/avatars").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var arr = jsonMapper.readTree(resp);
		assertThat(arr.isArray()).isTrue();
		assertThat(arr.size()).isGreaterThanOrEqualTo(2);
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void updateProfileSettings_shouldToggleLeaderboard() throws Exception {
		var payload = Map.of("leaderboardEnabled", true);
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/gamification/me/settings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(payload)))
				.andExpect(status().isOk());

		String resp = mockMvc.perform(get("/api/v1/gamification/me").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(resp);
		assertThat(node.get("leaderboardEnabled").asBoolean()).isTrue();
	}
}




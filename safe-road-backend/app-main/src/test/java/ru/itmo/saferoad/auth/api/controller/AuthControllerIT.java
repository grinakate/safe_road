package ru.itmo.saferoad.auth.api.controller;

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
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.domain.repository.UsersRepository;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Profile("test")
@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = SafeRoadApplication.class)
class AuthControllerIT {

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
	private UsersRepository usersRepository;

	@Test
	void register_shouldCreateUser_andReturnToken() throws Exception {
		// arrange
		String email = "newuser@example.com";
		String nickname = "new_user";
		String birthDate = "2000-01-01";
		String password = "password123";

		Map<String, Object> payload = Map.of(
				"nickname", nickname,
				"email", email,
				"birthDate", birthDate,
				"password", password
		);

		// act
		String response = mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(payload)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(response);
		assertThat(node.has("token")).isTrue();
		String token = node.get("token").asString();
		assertThat(token).isNotBlank();

		// assert DB state
		var userOpt = usersRepository.findByEmail(email);
		assertThat(userOpt).isPresent();
		Users created = userOpt.get();
		assertThat(created.getNickname()).isEqualTo(nickname);
		assertThat(created.getBirthDate().toLocalDate()).isEqualTo(LocalDate.parse(birthDate));
		assertThat(created.getPasswordHash()).isNotBlank();
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getMe_authenticated_shouldReturnProfile() throws Exception {
		String meResp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/me")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(meResp);
		assertThat(node.get("email").asString()).isEqualTo("test@test.ru");
		assertThat(node.get("nickname").asString()).isEqualTo("ТестТестович");
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void updatePassword_authenticated_shouldChangePassword() throws Exception {
		var oldPassword = "qwerty12345";
		var newPassword = "new_password";
		var email = "test@test.ru";

		var changePayload = Map.of(
				"oldPassword", oldPassword,
				"newPassword", newPassword
		);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/me/password")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(changePayload)))
				.andExpect(status().isOk());

		// login with old password should fail
		var oldLogin = Map.of("email", email, "password", oldPassword);
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(oldLogin)))
				.andExpect(status().isUnauthorized());

		// login with new password should succeed
		var newLogin = Map.of("email", email, "password", newPassword);
		String loginResp = mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(newLogin)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(loginResp);
		assertThat(node.has("token")).isTrue();
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	void login_success_shouldReturnToken() throws Exception {
		// register first to have a known user
		String email = "login_success@example.com";
		String nickname = "login_success";
		String birthDate = "1995-05-05";
		String password = "StrongPass1";

		Map<String, Object> registerPayload = Map.of(
				"nickname", nickname,
				"email", email,
				"birthDate", birthDate,
				"password", password
		);

		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(registerPayload)))
				.andExpect(status().isOk());

		// attempt login
		Map<String, Object> loginPayload = Map.of(
				"email", email,
				"password", password
		);

		String loginResp = mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(loginPayload)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(loginResp);
		assertThat(node.has("token")).isTrue();
		String token = node.get("token").asString();
		assertThat(token).isNotBlank();
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	void login_userNotFound_shouldReturnUnauthorized() throws Exception {
		Map<String, Object> loginPayload = Map.of(
				"email", "no_such_user@example.com",
				"password", "whatever"
		);

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(loginPayload)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	void login_wrongPassword_shouldReturnUnauthorized() throws Exception {
		// register user
		String email = "wrong_pass@example.com";
		String password = "CorrectPass1";
		Map<String, Object> registerPayload = Map.of(
				"nickname", "wp_user",
				"email", email,
				"birthDate", "1990-01-01",
				"password", password
		);
		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(registerPayload)))
				.andExpect(status().isOk());

		// try to login with wrong password
		Map<String, Object> loginPayload = Map.of(
				"email", email,
				"password", "WrongPassword"
		);

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsString(loginPayload)))
				.andExpect(status().isUnauthorized());
	}
}



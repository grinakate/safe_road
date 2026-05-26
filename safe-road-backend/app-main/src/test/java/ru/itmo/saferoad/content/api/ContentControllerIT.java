package ru.itmo.saferoad.content.api;


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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.saferoad.app.SafeRoadApplication;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Profile("test")
@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = SafeRoadApplication.class)
class ContentControllerIT {

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
	void getTopicsForSection_shouldReturnTopicsList() throws Exception {
		String resp = mockMvc.perform(get("/api/v1/content/sections/100/topics")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var arr = jsonMapper.readTree(resp);
		assertThat(arr.isArray()).isTrue();
		assertThat(arr.size()).isGreaterThanOrEqualTo(2);
		boolean found = false;
		for (var node : arr) {
			if (node.get("id").asInt() == 200 && "T".equals(node.get("title").textValue())) {
				found = true;
				break;
			}
		}
		assertThat(found).isTrue();
	}

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	@WithUserDetails(value = "test@test.ru", userDetailsServiceBeanName = "userDetailsServiceImpl")
	void getTopic_shouldReturnTopic() throws Exception {
		String resp = mockMvc.perform(get("/api/v1/content/topics/200")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		var node = jsonMapper.readTree(resp);
		assertThat(node.get("id").asInt()).isEqualTo(200);
		assertThat(node.get("title").asString()).isEqualTo("T");
		assertThat(node.get("content").asString()).isEqualTo("{}");
		assertThat(node.get("orderIndex").asInt()).isEqualTo(1);
	}
}


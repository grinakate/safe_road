package ru.itmo.saferoad.learning.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.learning.dto.SectionStatisticsResponse;
import ru.itmo.saferoad.learning.dto.StartTestRequest;
import ru.itmo.saferoad.learning.dto.StartTestResponse;
import ru.itmo.saferoad.learning.dto.SubmitSessionAnswerRequest;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;
import ru.itmo.saferoad.learning.dto.TestResultResponse;
import ru.itmo.saferoad.learning.dto.TestSessionSubmitResponse;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;
import ru.itmo.saferoad.learning.service.TestSessionService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class LearningControllerTest {

	@Mock
	private MapService mapService;

	@Mock
	private TestSessionService testSessionService;

	@Mock
	private ru.itmo.saferoad.learning.service.impl.StatisticsService statisticsService;

	private LearningController controller;

	@BeforeEach
	void setUp() {
		controller = new LearningController(mapService, testSessionService, statisticsService);
	}

	private AppUserDetails userWithId(long id) {
		return new AppUserDetails() {
			@Override
			public Long getId() {
				return id;
			}

			@Override
			public String getNickname() {
				return "u" + id;
			}

			@Override
			public String getPassword() {
				return "";
			}

			@Override
			public String getUsername() {
				return "user" + id;
			}

			@Override
			public boolean isAccountNonExpired() {
				return true;
			}

			@Override
			public boolean isAccountNonLocked() {
				return true;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public java.util.Collection<org.springframework.security.core.GrantedAuthority> getAuthorities() {
				return Collections.emptyList();
			}
		};
	}

	@Test
	void startTest_delegatesToService_andReturnsResponse() {
		StartTestRequest req = StartTestRequest.builder().sectionId(1).build();
		StartTestResponse resp = StartTestResponse.builder().sessionId(99L).totalQuestions(5).build();
		when(testSessionService.startTest(req, 42L)).thenReturn(resp);

		ResponseEntity<StartTestResponse> result = controller.startTest(req, userWithId(42L));

		assertEquals(OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals(99L, result.getBody().getSessionId());
		verify(testSessionService).startTest(req, 42L);
	}

	@Test
	void startTest_propagatesServiceException() {
		StartTestRequest req = StartTestRequest.builder().sectionId(1).build();
		when(testSessionService.startTest(req, 42L)).thenThrow(new IllegalStateException("boom"));

		assertThrows(IllegalStateException.class, () -> controller.startTest(req, userWithId(42L)));
	}

	@Test
	void getTestQuestions_delegatesToService_andReturnsList() {
		List<TestQuestionResponse> list = List.of(TestQuestionResponse.builder().id(1L).build());
		when(testSessionService.getTestQuestions(10L, 2L)).thenReturn(list);

		ResponseEntity<List<TestQuestionResponse>> result = controller.getTestQuestions(10L, userWithId(2L));

		assertEquals(OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals(1, result.getBody().size());
		verify(testSessionService).getTestQuestions(10L, 2L);
	}

	@Test
	void submitSessionAnswer_delegatesToService_andReturnsResponse() {
		SubmitSessionAnswerRequest req = SubmitSessionAnswerRequest.builder().build();
		TestSessionSubmitResponse resp = TestSessionSubmitResponse.builder().totalQuestions(1).correctCount(1).build();
		when(testSessionService.submitSessionAnswer(5L, req, 3L)).thenReturn(resp);

		ResponseEntity<TestSessionSubmitResponse> result = controller.submitSessionAnswer(5L, req, userWithId(3L));

		assertEquals(OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals(1, result.getBody().getCorrectCount());
		verify(testSessionService).submitSessionAnswer(5L, req, 3L);
	}

	@Test
	void getTestResult_delegatesToService_andReturns() {
		TestResultResponse resp = TestResultResponse.builder().correctCount(2).totalQuestions(2).build();
		when(testSessionService.getTestResult(7L, 4L)).thenReturn(resp);

		ResponseEntity<TestResultResponse> result = controller.getTestResult(7L, userWithId(4L));

		assertEquals(OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals(2, result.getBody().getCorrectCount());
		verify(testSessionService).getTestResult(7L, 4L);
	}

	@Test
	void getStatistics_callsStatisticsService_andReturns() {
		List<SectionStatisticsResponse> stats = List.of(SectionStatisticsResponse.builder()
				.sectionId(1).title("s").completedTopics(0).totalTopics(0).topicStatistics(Collections.emptyList()).build());
		when(statisticsService.getStatisticsForUser(8L)).thenReturn(stats);

		ResponseEntity<List<SectionStatisticsResponse>> result = controller.getStatistics(userWithId(8L));

		assertEquals(OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals(1, result.getBody().size());
		verify(statisticsService).getStatisticsForUser(8L);
	}

	@Test
	void getRoadMap_callsMapService_andReturns() {
		List<UserSectionResponse> map = List.of(UserSectionResponse.builder()
				.id(1).description("").url("").title("t").progressPercent(0).topics(Collections.emptyList()).build());
		when(mapService.getMapForUser(9L)).thenReturn(map);

		ResponseEntity<List<UserSectionResponse>> result = controller.getRoadMap(userWithId(9L));

		assertEquals(OK, result.getStatusCode());
		assertNotNull(result.getBody());
		assertEquals(1, result.getBody().size());
		verify(mapService).getMapForUser(9L);
	}
}





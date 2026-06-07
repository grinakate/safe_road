package ru.itmo.saferoad.learning.api;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.content.domain.QuestionType;
import ru.itmo.saferoad.learning.dto.TestAnswerOptionResponse;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class QuestionMapperTest {

    private final QuestionMapper mapper = new QuestionMapperImpl();

    @Test
    void toTestQuestionResponse_mapsFieldsAndFindsCorrectOption() {
        // Given
        Long qid = Instancio.create(Long.class);
        Integer correctNumber = 2;

        QuestionContent.AnswerOption opt1 = Instancio.of(QuestionContent.AnswerOption.class)
                .set(field(QuestionContent.AnswerOption::getNumber), 1)
                .set(field(QuestionContent.AnswerOption::getText), Instancio.create(String.class))
                .set(field(QuestionContent.AnswerOption::getFeedback), Instancio.create(String.class))
                .set(field(QuestionContent.AnswerOption::getIsCorrect), false)
                .create();

        QuestionContent.AnswerOption opt2 = Instancio.of(QuestionContent.AnswerOption.class)
                .set(field(QuestionContent.AnswerOption::getNumber), correctNumber)
                .set(field(QuestionContent.AnswerOption::getText), Instancio.create(String.class))
                .set(field(QuestionContent.AnswerOption::getFeedback), Instancio.create(String.class))
                .set(field(QuestionContent.AnswerOption::getIsCorrect), true)
                .create();

        QuestionContent content = Instancio.of(QuestionContent.class)
                .set(field(QuestionContent::getQuestionText), Instancio.create(String.class))
                .set(field(QuestionContent::getImageUrlPlaceholder), Instancio.create(String.class))
                .set(field(QuestionContent::getOptions), List.of(opt1, opt2))
                .create();

        Question question = Instancio.of(Question.class)
                .set(field(Question::getId), qid)
                .set(field(Question::getContent), content)
                .set(field(Question::getType), QuestionType.CHOICE)
                .create();

        // When
        TestQuestionResponse dto = mapper.toTestQuestionResponse(question);

        // Then
        assertNotNull(dto);
        assertEquals(qid, dto.getId());
        assertEquals(content.getQuestionText(), dto.getQuestionText());
        assertEquals(content.getImageUrlPlaceholder(), dto.getImageUrlPlaceholder());
        assertNotNull(dto.getOptions());
        assertEquals(2, dto.getOptions().size());
        TestAnswerOptionResponse mappedOpt = dto.getOptions().stream().filter(o -> o.getId().equals(correctNumber)).findFirst().orElse(null);
        assertNotNull(mappedOpt);
        assertEquals(opt2.getText(), mappedOpt.getText());
        assertEquals(opt2.getFeedback(), mappedOpt.getFeedback());
        assertEquals(opt2.getIsCorrect(), mappedOpt.getIsCorrect());
        assertEquals(correctNumber, dto.getCorrectAnswerNumber());
    }

	@Test
	void toTestQuestionResponseList_mapsAllQuestions() {
		QuestionContent.AnswerOption option = Instancio.of(QuestionContent.AnswerOption.class)
				.set(field(QuestionContent.AnswerOption::getNumber), 1)
				.set(field(QuestionContent.AnswerOption::getIsCorrect), true)
				.create();

		QuestionContent content = Instancio.of(QuestionContent.class)
				.set(field(QuestionContent::getOptions), List.of(option))
				.create();

		Question q1 = Instancio.of(Question.class).set(field(Question::getId), 1L).set(field(Question::getContent), content).create();
		Question q2 = Instancio.of(Question.class).set(field(Question::getId), 2L).set(field(Question::getContent), content).create();

		List<TestQuestionResponse> result = mapper.toTestQuestionResponseList(List.of(q1, q2));

		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).getId());
		assertEquals(2L, result.get(1).getId());
	}

	@Test
	void toOption_mapsNumberToIdAndCopiesFields() {
		QuestionContent.AnswerOption source = Instancio.of(QuestionContent.AnswerOption.class)
				.set(field(QuestionContent.AnswerOption::getNumber), 5)
				.set(field(QuestionContent.AnswerOption::getText), "text")
				.set(field(QuestionContent.AnswerOption::getFeedback), "feedback")
				.set(field(QuestionContent.AnswerOption::getIsCorrect), false)
				.create();

		TestAnswerOptionResponse result = mapper.toOption(source);

		assertEquals(5, result.getId());
		assertEquals("text", result.getText());
		assertEquals("feedback", result.getFeedback());
		assertFalse(result.getIsCorrect());
	}

	@Test
	void findCorrectOptionNumber_returnsNullWhenOptionsNullOrWithoutCorrect() {
		assertNull(mapper.findCorrectOptionNumber(null));

		QuestionContent.AnswerOption option = Instancio.of(QuestionContent.AnswerOption.class)
				.set(field(QuestionContent.AnswerOption::getNumber), 10)
				.set(field(QuestionContent.AnswerOption::getIsCorrect), false)
				.create();

		assertNull(mapper.findCorrectOptionNumber(List.of(option)));
	}

	@Test
	void findCorrectOptionNumber_returnsFirstCorrectWhenSeveralPresent() {
		QuestionContent.AnswerOption firstCorrect = Instancio.of(QuestionContent.AnswerOption.class)
				.set(field(QuestionContent.AnswerOption::getNumber), 2)
				.set(field(QuestionContent.AnswerOption::getIsCorrect), true)
				.create();
		QuestionContent.AnswerOption secondCorrect = Instancio.of(QuestionContent.AnswerOption.class)
				.set(field(QuestionContent.AnswerOption::getNumber), 3)
				.set(field(QuestionContent.AnswerOption::getIsCorrect), true)
				.create();

		Integer result = mapper.findCorrectOptionNumber(List.of(firstCorrect, secondCorrect));

		assertEquals(2, result);
	}
}


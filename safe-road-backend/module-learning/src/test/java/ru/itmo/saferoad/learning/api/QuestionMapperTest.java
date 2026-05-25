package ru.itmo.saferoad.learning.api;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.content.domain.QuestionType;
import ru.itmo.saferoad.learning.dto.TestAnswerOptionResponse;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
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
}


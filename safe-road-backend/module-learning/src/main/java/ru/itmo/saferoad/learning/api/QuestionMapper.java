package ru.itmo.saferoad.learning.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.learning.dto.TestAnswerOptionResponse;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "questionText", source = "content.questionText")
    @Mapping(target = "imageUrlPlaceholder", source = "content.imageUrlPlaceholder")
    @Mapping(target = "options", source = "content.options")
    @Mapping(target = "correctAnswerNumber", expression = "java(findCorrectOptionNumber(question.getContent().getOptions()))")
    TestQuestionResponse toTestQuestionResponse(Question question);

    List<TestQuestionResponse> toTestQuestionResponseList(List<Question> questions);

    @Mapping(target = "id", source = "number")
    TestAnswerOptionResponse toOption(QuestionContent.AnswerOption option);

    default Integer findCorrectOptionNumber(List<QuestionContent.AnswerOption> options) {
        if (options == null) return null;
        return options.stream()
                .filter(QuestionContent.AnswerOption::getIsCorrect)
                .findFirst()
                .map(QuestionContent.AnswerOption::getNumber)
                .orElse(null);
    }
}



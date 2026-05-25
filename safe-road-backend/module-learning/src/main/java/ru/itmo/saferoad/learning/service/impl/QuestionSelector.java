package ru.itmo.saferoad.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.learning.config.LearningProperties;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class QuestionSelector {

    private final LearningProperties learningProperties;
    private final CurrentTime currentTime;

    public List<Question> selectQuestionsAdaptively(List<Question> allQuestions,
                                                     List<UserQuestionStats> userStats,
                                                     int targetCount) {
        LocalDateTime now = currentTime.nowDateTime();

        Map<Long, UserQuestionStats> statsMap = new HashMap<>();
        for (UserQuestionStats stat : userStats) {
            statsMap.put(stat.getQuestionId(), stat);
        }

        List<Question> needsReview = new ArrayList<>();
        List<Question> newQuestions = new ArrayList<>();
        List<Question> knownQuestions = new ArrayList<>();

        for (Question q : allQuestions) {
            UserQuestionStats stat = statsMap.get(q.getId());
            if (stat == null) {
                newQuestions.add(q);
            } else if (!stat.getNextReviewAt().isAfter(now)) {
                needsReview.add(q);
            } else {
                knownQuestions.add(q);
            }
        }

        needsReview.sort(Comparator.comparing(q -> statsMap.get(q.getId()).getNextReviewAt()));
        Collections.shuffle(newQuestions);
        Collections.shuffle(knownQuestions);

        int maxReview = (int) Math.floor(targetCount * learningProperties.getReviewRatio());
        int maxNew = (int) Math.floor(targetCount * learningProperties.getNewRatio());
        int maxKnown = targetCount - (maxReview + maxNew);

        int actualReview = Math.min(maxReview, needsReview.size());
        List<Question> selected = new ArrayList<>(needsReview.subList(0, actualReview));
        int shortageReview = maxReview - actualReview;

        int targetNew = maxNew + shortageReview;
        int actualNew = Math.min(targetNew, newQuestions.size());
        selected.addAll(newQuestions.subList(0, actualNew));
        int shortageNew = targetNew - actualNew;

        int targetKnown = maxKnown + shortageNew;
        int actualKnown = Math.min(targetKnown, knownQuestions.size());
        selected.addAll(knownQuestions.subList(0, actualKnown));
        int shortageKnown = targetKnown - actualKnown;

        if (shortageKnown > 0) {
            int remainingReviewTarget = shortageKnown;
            int remainingReviewActual = Math.min(remainingReviewTarget, needsReview.size() - actualReview);
            selected.addAll(needsReview.subList(actualReview, actualReview + remainingReviewActual));
            shortageKnown -= remainingReviewActual;

            if (shortageKnown > 0) {
                int remainingNewActual = Math.min(shortageKnown, newQuestions.size() - actualNew);
                selected.addAll(newQuestions.subList(actualNew, actualNew + remainingNewActual));
            }
        }

        return selected;
    }
}


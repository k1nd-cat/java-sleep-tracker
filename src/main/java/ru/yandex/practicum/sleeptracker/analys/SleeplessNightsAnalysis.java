package ru.yandex.practicum.sleeptracker.analys;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.Session;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

public class SleeplessNightsAnalysis implements Function<List<Session>, SleepAnalysisResult<Long>> {


    @Override
    public SleepAnalysisResult<Long> apply(List<Session> sessions) {
        String description = "Количество бессонных ночей";
        if (sessions.isEmpty())
            return new SleepAnalysisResult<>(description, 0L);

        var begin = sessions.getFirst().begin();
        var end = sessions.getLast().end();

        LocalDate firstNight = begin.toLocalDate();

        if (!begin.toLocalTime().isBefore(LocalTime.NOON))
            firstNight = firstNight.plusDays(1);

        LocalDate endExclusive = end.toLocalDate();

        if (!end.toLocalTime().equals(LocalTime.MIDNIGHT))
            endExclusive = endExclusive.plusDays(1);

        long count = firstNight.datesUntil(endExclusive)
                .filter(date -> {
                    LocalDateTime nightBegin = date.atStartOfDay();
                    LocalDateTime nightEnd = date.atTime(6, 0);

                    return sessions.stream().noneMatch(session ->
                            session.begin().isBefore(nightEnd) && session.end().isAfter(nightBegin));
                }).count();

        return new SleepAnalysisResult<>(description, count);
    }
}

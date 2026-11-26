package ru.yandex.practicum.sleeptracker.analys;

import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.Session;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeAnalysis implements Function<List<Session>, SleepAnalysisResult<Chronotype>> {

    @Override
    public SleepAnalysisResult<Chronotype> apply(List<Session> sessions) {

        Map<Chronotype, Long> counts = sessions.stream()
                .flatMap(session -> session.begin().toLocalDate()
                        .datesUntil(session.end().toLocalDate().plusDays(1))
                        .filter(date -> session.begin().isBefore(date.atTime(6, 0))
                                && session.end().isAfter(date.atStartOfDay())))
                .distinct()
                .map(date -> classifyNight(date, sessions))
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        long owls = counts.getOrDefault(Chronotype.OWL, 0L);
        long larks = counts.getOrDefault(Chronotype.LARK, 0L);
        long pigeons = counts.getOrDefault(Chronotype.PIGEON, 0L);

        Chronotype result = Chronotype.PIGEON;

        if (owls > larks && owls > pigeons) {
            result = Chronotype.OWL;
        } else if (larks > owls && larks > pigeons) {
            result = Chronotype.LARK;
        }

        return new SleepAnalysisResult<>("Хронотип пользователя", result);
    }

    private static Chronotype classifyNight(LocalDate date, List<Session> sessions) {
        List<Session> nightSessions = sessions.stream()
                .filter(session -> session.begin().isBefore(date.atTime(6, 0))
                        && session.end().isAfter(date.atStartOfDay()))
                .toList();

        LocalDateTime begin = nightSessions.stream()
                .map(Session::begin)
                .min(LocalDateTime::compareTo)
                .orElseThrow();

        LocalDateTime end = nightSessions.stream()
                .map(Session::end)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        LocalDate evening = date.minusDays(1);

        if (begin.isAfter(evening.atTime(23, 0))
                && end.isAfter(date.atTime(9, 0)))
            return Chronotype.OWL;

        if (begin.isBefore(evening.atTime(22, 0))
                && end.isBefore(date.atTime(7, 0)))
            return Chronotype.LARK;

        return Chronotype.PIGEON;
    }
}

package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analys.ChronotypeAnalysis;
import ru.yandex.practicum.sleeptracker.analys.SessionCountAnalysis;
import ru.yandex.practicum.sleeptracker.analys.SleeplessNightsAnalysis;
import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.Session;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {

//    Подсчёт сессий
    @Test
    void sessionCountReturnsZeroForEmptyList() {
        var analysis = new SessionCountAnalysis();

        var result = analysis.apply(List.of());

        assertEquals(0, result.value().intValue());
    }

    @Test
    void sessionCountIncludesNightAndDaySessions() {
        var analysis = new SessionCountAnalysis();
        List<Session> sessions = List.of(
                session("2025-10-01T22:00", "2025-10-02T08:00"),
                session("2025-10-02T14:00", "2025-10-02T15:00"),
                session("2025-10-02T23:00", "2025-10-03T07:00")
        );

        var result = analysis.apply(sessions);

        assertEquals(3, result.value().intValue());
    }

//    Бессонные ночи
    @Test
    void sleeplessNightsReturnsZeroForEmptyList() {
        var analysis = new SleeplessNightsAnalysis();

        var result = analysis.apply(List.of());

        assertEquals(0L, result.value().longValue());
    }

    @Test
    void sleepStartingAtSixDoesNotCountAsNightSleep() {
        var analysis = new SleeplessNightsAnalysis();
        List<Session> sessions = List.of(
                session("2025-10-02T06:00", "2025-10-02T11:00")
        );

        var result = analysis.apply(sessions);

        assertEquals(1L, result.value().longValue());
    }

    @Test
    void multipleSessionsInOneNightDoNotCreateSleeplessNights() {
        var analysis = new SleeplessNightsAnalysis();
        List<Session> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T02:00"),
                session("2025-10-02T03:00", "2025-10-02T07:00")
        );

        var result = analysis.apply(sessions);

        assertEquals(0L, result.value().longValue());
    }

    @Test
    void sleeplessNightsCountsMissingNightAcrossMonths() {
        var analysis = new SleeplessNightsAnalysis();
        List<Session> sessions = List.of(
                session("2025-10-31T23:00", "2025-11-01T08:00"),
                session("2025-11-02T23:00", "2025-11-03T08:00")
        );

        var result = analysis.apply(sessions);

        assertEquals(1L, result.value().longValue());
    }

//    Определение хронотипа
    @Test
    void chronotypeReturnsOwlForMajorityOfLateNights() {
        var analysis = new ChronotypeAnalysis();
        List<Session> sessions = List.of(
                session("2025-10-02T01:00", "2025-10-02T10:00"),
                session("2025-10-02T14:00", "2025-10-02T15:00"),
                session("2025-10-02T23:30", "2025-10-03T10:00"),
                session("2025-10-03T22:30", "2025-10-04T08:00")
        );

        var result = analysis.apply(sessions);

        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void chronotypeReturnsLarkForEarlySleepAndWakeUp() {
        var analysis = new ChronotypeAnalysis();
        List<Session> sessions = List.of(
                session("2025-10-01T21:00", "2025-10-02T06:00")
        );

        var result = analysis.apply(sessions);

        assertEquals(Chronotype.LARK, result.value());
    }

    @Test
    void chronotypeReturnsPigeonAtExactTimeBoundaries() {
        var analysis = new ChronotypeAnalysis();
        List<Session> sessions = List.of(
                session("2025-10-01T23:00", "2025-10-02T09:00"),
                session("2025-10-02T22:00", "2025-10-03T07:00")
        );

        var result = analysis.apply(sessions);

        assertEquals(Chronotype.PIGEON, result.value());
    }

    @Test
    void chronotypeReturnsPigeonWhenOwlAndLarkCountsAreEqual() {
        var analysis = new ChronotypeAnalysis();
        List<Session> sessions = List.of(
                session("2025-10-01T23:30", "2025-10-02T10:00"),
                session("2025-10-03T21:00", "2025-10-04T06:00")
        );

        var result = analysis.apply(sessions);
        assertEquals(Chronotype.PIGEON, result.value());
    }

    private static Session session(String begin, String end) {
        return new Session(
                LocalDateTime.parse(begin),
                LocalDateTime.parse(end),
                Session.Quality.NORMAL
        );
    }
}
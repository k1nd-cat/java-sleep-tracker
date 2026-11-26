package ru.yandex.practicum.sleeptracker.analys;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.Session;

import java.util.List;
import java.util.function.Function;

public class SessionCountAnalysis implements Function<List<Session>, SleepAnalysisResult<Integer>> {

    @Override
    public SleepAnalysisResult<Integer> apply(List<Session> sessions) {
        return new SleepAnalysisResult<>("Количество сессий сна", sessions.size());
    }
}

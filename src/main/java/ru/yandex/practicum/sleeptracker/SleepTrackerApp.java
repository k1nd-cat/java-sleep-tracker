package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analys.ChronotypeAnalysis;
import ru.yandex.practicum.sleeptracker.analys.SessionCountAnalysis;
import ru.yandex.practicum.sleeptracker.analys.SleeplessNightsAnalysis;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class SleepTrackerApp {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private static final List<Function<List<Session>, ? extends SleepAnalysisResult<?>>> ANALYSES = List.of(
            new SessionCountAnalysis(),
            new SleeplessNightsAnalysis(),
            new ChronotypeAnalysis()
    );

    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Необходимо указать путь к файлу");

        File file = new File(args[0]);
        if (!file.isFile())
            throw new IllegalArgumentException("Неверно указан путь к файлу");

        List<Session> sessions;
        try (Stream<String> lines = Files.lines(Path.of(args[0]))) {
            sessions = lines.filter(line -> !line.isBlank())
                    .map(it -> {
                        String[] parts = it.split(";", -1);

                        if (parts.length != 3)
                            throw new IllegalArgumentException("Неверный формат записи " + it);

                        return new Session(
                                LocalDateTime.parse(parts[0].trim(), FORMATTER),
                                LocalDateTime.parse(parts[1].trim(), FORMATTER),
                                Session.Quality.valueOf(parts[2].trim().toUpperCase())
                        );
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ANALYSES.forEach(analysis -> {
            SleepAnalysisResult<?> result = analysis.apply(sessions);
            System.out.println(result.description() + ": " + result.value());
        });
    }
}
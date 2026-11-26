package ru.yandex.practicum.sleeptracker.model;

import java.time.LocalDateTime;

public record Session(
        LocalDateTime begin,
        LocalDateTime end,
        Quality quality
) {

    public enum Quality {
        GOOD, NORMAL, BAD
    }
}

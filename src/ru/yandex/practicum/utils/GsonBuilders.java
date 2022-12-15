package ru.yandex.practicum.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.adapter.DurationAdapter;
import ru.yandex.practicum.adapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonBuilders {
    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

    }
}

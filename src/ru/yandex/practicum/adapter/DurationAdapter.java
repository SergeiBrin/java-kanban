package ru.yandex.practicum.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(duration.toSeconds());
        } else {
            jsonWriter.nullValue();
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        try {
            long second = Long.parseLong(jsonReader.nextString());
            return Duration.ofSeconds(second);
        } catch (NumberFormatException e) {
            throw new RemoteException("Вы ввели не число");
        }
    }
}

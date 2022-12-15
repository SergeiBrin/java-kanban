package ru.yandex.practicum.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    // Сократил до секунд, чтобы клиенту было удобней отправлять запрос и получать ответ.
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss.nnnnnnnnn");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime dateTime) throws IOException {
        if (dateTime != null) {
            jsonWriter.value(dateTime.format(formatter));
        } else {
           jsonWriter.nullValue();
        }
    }

    // Разобратьcя после с этим исключением. Сделать метод с gson и внедрить туда эти исключения.
    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        try {
            return LocalDateTime.parse(jsonReader.nextString(), formatter);
        } catch (DateTimeParseException e) {
            throw new RemoteException("Неправильный формат LDT");
        }
    }
}

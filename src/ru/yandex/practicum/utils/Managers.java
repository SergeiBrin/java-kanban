package ru.yandex.practicum.utils;

import ru.yandex.practicum.managers.*;

import java.nio.file.Path;

public class Managers {
    // Создание нового объекта HttpTaskManager.
    public static TaskManager getDefault() {
        return HTTPTaskManager.load();
    }

    // Создание нового объекта FileBackedTaskManager.
    public static TaskManager getFileBackedTaskManager() {
        return FileBackedTasksManager.loadFromFile(Path.of("resources/backed_file.csv"));
    }

    // Создание нового объекта типа HistoryManager. Создается автоматически
    // при создании нового объекта типа InMemoryTaskManager().
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}




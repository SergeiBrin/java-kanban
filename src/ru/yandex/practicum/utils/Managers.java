package ru.yandex.practicum.utils;

import ru.yandex.practicum.managers.*;

import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() { // Создание нового объекта HttpTaskManager.
        return HTTPTaskManager.load();
    }

    public static TaskManager getFileBackedTaskManager() { // Создание нового объекта FileBackedTaskManager.
        return FileBackedTasksManager.loadFromFile(Path.of("resources/backed_file.csv"));
    }

    public static HistoryManager getDefaultHistory() { // Создание нового объекта типа HistoryManager. Создается автоматически
        return new InMemoryHistoryManager();           // при создании нового объекта типа InMemoryTaskManager().
    }
}




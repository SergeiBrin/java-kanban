package ru.yandex.practicum.managers;

public class Managers {
    public static TaskManager getDefault() { // Создание нового объекта типа TaskManager.
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() { // Создание нового объекта типа HistoryManager. Создается автоматически
        return new InMemoryHistoryManager();           // при создании нового объекта типа InMemoryTaskManager().
    }
}




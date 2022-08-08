package ru.yandex.practicum.managers;

public class Managers {
    public static TaskManager getDefault() { // Создание нового объекта типа TaskManager.
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory() { // Создание нового объекта типа HistoryManager. Создается автоматически
        return new InMemoryHistoryManager();    // при создании нового объекта типа InMemoryTaskManager() –
    }                                           // в его конструкторе. Модификатор сделал Default, чтобы до метода
}                                               // нельзя было дотянуться из Main.




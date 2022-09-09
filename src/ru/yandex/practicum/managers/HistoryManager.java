package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task); // Метод, который добавляет просмотренную задачу в лист истории.

    List<Task> getHistory(); // Метод, который возвращает лист истории просмотренных задач.

    void remove(int id); // Если задачу удалили из taskManager, то метод удалит эту задачу из истории просмотров.
}

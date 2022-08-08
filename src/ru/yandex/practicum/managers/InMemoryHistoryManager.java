package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> lastTenTasks;

    public InMemoryHistoryManager() {
        lastTenTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) { // // Метод, который добавляет просмотренную задачу в лист истории.
        lastTenTasks.add(task);
    }

    @Override
    public List<Task> getHistory() { // Метод, который возвращает лист истории просмотренных задач.
        return lastTenTasks;
    }

    public void checkSizeLastTenTasks() { // Метод проверяет размер списка просмотренных задач.
        if (lastTenTasks.size() > 10) {    // Если список больше 10, то метод удаляет
            lastTenTasks.remove(0);  // самую первую задачу из списка.
        }
    }
}

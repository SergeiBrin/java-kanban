package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyManager = new LinkedList<>(); // Сделал LinkedList. Проинициализировал сразу.

    @Override
    public void add(Task task) { // // Метод, который добавляет просмотренную задачу в лист истории.
        historyManager.add(task);
        checkSizeLastTenTasks(); // теперь проверка на 10 значений в листе вызывается из этого класса.
    }

    @Override
    public List<Task> getHistory() { // Метод, который возвращает лист истории просмотренных задач.
        return historyManager;
    }

    private void checkSizeLastTenTasks() { // Метод проверяет размер списка просмотренных задач сделал private.
        if (historyManager.size() > 10) {    // Если список больше 10, то метод удаляет
            historyManager.remove(0);  // самую первую задачу из списка.
        }
    }
}

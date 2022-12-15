package ru.yandex.practicum.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask1;

    @BeforeEach
    public void createNewHistoryManager() {
        historyManager = new InMemoryHistoryManager();

        task = new Task("Test Task name",
                        "Test Task Description",
                        TaskStatus.NEW,
                        LocalDateTime.now(),
                        Duration.ZERO);

        epic = new Epic("Test Epic name",
                        "Test Epic Description",
                        TaskStatus.NEW,
                        null,
                        null);

        subtask1 = new Subtask("Test Subtask1 name",
                               "Test Subtask Description",
                               TaskStatus.NEW,
                               LocalDateTime.now(),
                               Duration.ZERO);
    }

    @Test
    public void checkThatNoEmptyHistoryListIsNotReturned() {
        // Возвращаю исторический список, проверяю, что он пуст
        final List<Task> historyList = historyManager.getHistory();

        assertNotNull(historyList, "Список не должен быть null");
        assertTrue(historyList.isEmpty(), "Список должен быть пуст");
    }

    @Test
    public void shouldAddTasksToHistoryList() {
        // Добавляю задачи в исторический список
        task.setId(1);
        epic.setId(2);
        subtask1.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask1);

        // Возвращаю список
        final List<Task> historyList = historyManager.getHistory();

        // Проверяю до и после
        assertNotNull(historyList, "Список не должен быть null");
        assertFalse(historyList.isEmpty(), "Список не должен быть пуст");
        assertEquals(task, historyList.get(0), "Задачи не совпадают");
        assertEquals(epic, historyList.get(1), "Эпик задачи не совпадают");
        assertEquals(subtask1, historyList.get(2), "Подзадачи не совпадают");
    }

    @Test
    void checkThatTasksAreNotDuplicatedInHistoryList() {
        // Передаю одинаковые задачи историю
        task.setId(1);
        epic.setId(2);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(epic);

        // Возвращаю, проверяю, чтоб не было дубликатов
        final List<Task> historyList = historyManager.getHistory();

        assertNotNull(historyList, "Список не должен быть null");
        assertFalse(historyList.isEmpty(), "Список не должен быть пуст");
        assertEquals(2, historyList.size(), "В списке должно быть 2 задачи");
        assertEquals(task, historyList.get(0), "Задача не совпадает");
        assertEquals(epic, historyList.get(1), "Эпик задача не совпадает");
    }

    @Test
    void checkThatTaskIsDeletedFromTheBeginningOfHistoryList() {
        // Добавляю задачи в исторический список, после удаляю первую
        task.setId(1);
        epic.setId(2);
        subtask1.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask1);

        historyManager.remove(1);

        // Возвращаю и проверяю
        final List<Task> historyList = historyManager.getHistory();

        assertNotNull(historyList, "Список не должен быть null");
        assertFalse(historyList.isEmpty(), "Список не должен быть пуст");
        assertEquals(2, historyList.size(), "В списке должно остаться 2 задачи");
        assertEquals(epic, historyList.get(0), "Эпик задача не совпадает или не на своем месте");
        assertEquals(subtask1, historyList.get(1), "Подзадача не совпадает или не на своем месте");
    }

    @Test
    void checkThatTaskIsDeletedFromTheMiddleOfHistoryList() {
        // Добавляю задачи в исторический список, после удаляю среднюю
        task.setId(1);
        epic.setId(2);
        subtask1.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask1);

        historyManager.remove(2);

        // Возвращаю и проверяю
        final List<Task> historyList = historyManager.getHistory();

        assertNotNull(historyList, "Список не должен быть null");
        assertFalse(historyList.isEmpty(), "Список не должен быть пуст");
        assertEquals(2, historyList.size(), "В списке должно остаться 2 задачи");
        assertEquals(task, historyList.get(0), "Задача не совпадает или не на своем месте");
        assertEquals(subtask1, historyList.get(1), "Подадача не совпадает или не на своем месте");
    }

    @Test
    void checkThatTaskIsDeletedFromTheEndOfHistoryList() {
        // Добавляю задачи в исторический список, после удаляю последнюю
        task.setId(1);
        epic.setId(2);
        subtask1.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask1);

        historyManager.remove(3);

        // Возвращаю и проверяю
        final List<Task> historyList = historyManager.getHistory();

        assertNotNull(historyList, "Список не должен быть null");
        assertFalse(historyList.isEmpty(), "Список не должен быть пуст");
        assertEquals(2, historyList.size(), "В списке должно остаться 2 задачи");
        assertEquals(task, historyList.get(0), "Задача не совпадает или не на своем месте");
        assertEquals(epic, historyList.get(1), "Эпик задача не совпадает или не на своем месте");
    }
}
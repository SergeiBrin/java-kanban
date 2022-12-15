package ru.yandex.practicum.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.server.KVServer;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {
    private KVServer kvServer;

    @BeforeEach
    public void createNewTaskManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HTTPTaskManager("http://localhost:8078");
    }

    @Test
    public void checkThatEpicWithoutSubtasksIsLoadingCorrectlyFromServer() {
        // Создаю Эпик без подзадачи
        final int epicId = taskManager.createEpic(epic);

        // Создаю новый менеджер. Загружаю разными способами эпик,
        // считанный с сервера и проверяем
        taskManager = HTTPTaskManager.load();

        final Epic savedEpic = taskManager.getEpicById(epicId);

        final Map<Integer, Epic> epicMap = taskManager.getEpics();
        final List<Integer> subtaskIdForEpic = savedEpic.getSubtaskIdForEpic();
        final List<Subtask> subtasksList = taskManager.getSubTasksList();
        final Map<Integer, Subtask> subtaskMap = taskManager.getSubTasks();

        assertNotNull(epicMap, "Эпик задача не загрузилась c сервера в Map");
        assertEquals(1, epicMap.size(), "С сервера должна была загрузиться только одна задача");
        assertEquals(epic, savedEpic, "Эпик задачи до и после не совпадают");
        assertTrue(subtaskIdForEpic.isEmpty(), "С сервера не должен загружаться id подзадачи");
        assertTrue(subtasksList.isEmpty(), "Подзадача не должна загружаться с сервера");
    }

    @Test
    public void checkThatEpicWithSubtasksIsLoadingCorrectlyFromServer() {
        // Создаю задачу с подзадачами
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(epic, subtask1);

        // Создаю новый менеджер, возвращаю разными способами задачи,
        // загруженные с сервера.
        taskManager = HTTPTaskManager.load();

        final Epic savedEpic = taskManager.getEpicById(epicId);
        final Subtask savedSubtask1 = taskManager.getSubtaskById(subtaskId);

        final Map<Integer, Epic> epicMap = taskManager.getEpics();
        final List<Integer> subtaskIdForEpic = savedEpic.getSubtaskIdForEpic();
        final int epicIdForSubtask = savedSubtask1.getEpicIdForSubtask();
        final List<Subtask> subtasksList = taskManager.getSubTasksList();
        final Map<Integer, Subtask> subtaskMap = taskManager.getSubTasks();

        // Проверяю задачи до и после.
        assertFalse(epicMap.isEmpty(), "Эпик задача не загрузилась с сервера в Map");
        assertEquals(1, epicMap.size(), "С сервера должна была загрузиться только одна задача");
        assertEquals(epic, savedEpic, "Эпик задачи до и после не совпадают");
        assertFalse(subtaskIdForEpic.isEmpty(), "С сервера не загрузился id подзадачи");
        assertEquals(1, epicIdForSubtask, "id эпика не загрузился в список id эпика для подзадачи");
        assertEquals(2, subtaskIdForEpic.get(0), "id подзадачи не загрузился в поле id подзадачи для эпика");
        assertFalse(subtaskMap.isEmpty(), "Подзадача не загрузилась в Map");
        assertFalse(subtasksList.isEmpty(), "Подзадача не возвращается через метод getSubTasksList()");
        assertEquals(subtask1, savedSubtask1, "Подзадача до и после не совпадает");
        assertEquals(subtask1, subtasksList.get(0), "Подзадача до и после не совпадает");
        assertEquals(subtask1, subtaskMap.get(2), "Подзадача до и после не совпадает");
    }

    @Test
    public void checkThatIfThereIsNoHistoryItIsNotLoadedFromServer() {
        // Создаю задачи, но не вызываю их по id
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);

        // Создаю новый менеджер, подгружаю историю
        taskManager = HTTPTaskManager.load();

        final List<Task> taskHistory = taskManager.getHistory();

        // Проверяю, что история пустая
        assertTrue(taskHistory.isEmpty(), "История должна быть пустая");
    }

    @Test
    public void checkThatIfThereIsHistoryItIsLoadedFromServer() {
        // Создаю задачи, вызываю по id
        final int taskId = taskManager.createTask(task);
        final int epicId = taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);

        final Task savedTask = taskManager.getTaskById(taskId);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        // Создаю новый менеджер, подгружаю историю
        taskManager = HTTPTaskManager.load();

        final List<Task> taskHistory = taskManager.getHistory();

        // Проверяю историю до и после
        assertFalse(taskHistory.isEmpty(), "История просмотров c сервера не загружается");
        assertEquals(savedTask, taskHistory.get(0), "История просмотров загружается c сервера неправильно");
        assertEquals(savedEpic, taskHistory.get(1), "История просмотров загружается c сервера неправильно");
    }

    @Test
    public void checkThatTaskTimesCorrectlyLoadedFromServer() {
        // Создаем задачи, передаем время, делаем апдейт
        final int taskId = taskManager.createTask(task);
        task.setStartTime(LocalDateTime.now().minusHours(10));
        taskManager.updateTask(task);
        final LocalDateTime taskStartTime = task.getStartTime();
        final Duration taskDuration = task.getDuration();

        final int epicId = taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.now().plusHours(3));
        taskManager.updateEpic(epic);
        LocalDateTime epicStartTime = epic.getStartTime();
        Duration epicDuration = epic.getDuration();

        final int subtaskId = taskManager.createSubtask(epic, subtask1);
        subtask1.setStartTime(null);
        taskManager.updateSubtask(subtask1);
        final LocalDateTime subtaskStartTime = subtask1.getStartTime();
        final Duration subtaskDuration = subtask1.getDuration();

        // Создаем новый менеджер, возвращаем загруженные c сервера задачи
        taskManager = HTTPTaskManager.load();

        final Task loadTask = taskManager.getTaskById(taskId);
        final Epic loadEpic = taskManager.getEpicById(epicId);
        final Subtask loadSubtask = taskManager.getSubtaskById(subtaskId);

        // Проверяем время задач до и после.
        assertEquals(taskStartTime, loadTask.getStartTime(), "Время с сервера загружается неправильно");
        assertNull(loadEpic.getStartTime(), "Время с сервера загружается неправильно");
        assertEquals(taskDuration, loadTask.getDuration(), "Время с сервера загружается неправильно");
        assertEquals(epicDuration, loadEpic.getDuration(), "Время с сервера загружается неправильно");
        assertEquals(subtaskStartTime, loadSubtask.getStartTime(), "Время с сервера загружается неправильно");
        assertEquals(subtaskDuration, loadSubtask.getDuration(), "Время с сервера загружается неправильно");
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

}

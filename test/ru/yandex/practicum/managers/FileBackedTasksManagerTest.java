package ru.yandex.practicum.managers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private final static Path OF = Path.of("resources/test_file.csv");

    @BeforeEach
    public void createNewTaskManager() {
        taskManager = new FileBackedTasksManager(OF);
    }

    @Test
    public void checkThatEpicWithoutSubtasksIsLoadingCorrectlyFromCsvFile() {
        // Создаю Эпик без подзадачи
        final int epicId = taskManager.createEpic(epic);

        // Создаю новый менеджер. Загружаю разными способами эпик,
        // считанный из файла и проверяем
        taskManager = FileBackedTasksManager.loadFromFile(OF);

        final Epic savedEpic = taskManager.getEpicById(epicId);

        final Map<Integer, Epic> epicMap = taskManager.getEpics();
        final List<Integer> subtaskIdForEpic = savedEpic.getSubtaskIdForEpic();
        final List<Subtask> subtasksList = taskManager.getSubTasksList();
        final Map<Integer, Subtask> subtaskMap = taskManager.getSubTasks();

        assertNotNull(epicMap, "Эпик задача не загрузилась из файла в Map");
        assertEquals(1, epicMap.size(), "Из файла должна была загрузиться только одна задача");
        assertEquals(epic, savedEpic, "Эпик задачи до и после не совпадают");
        assertTrue(subtaskIdForEpic.isEmpty(), "Из файла не должен загружаться id подзадачи");
        assertTrue(subtasksList.isEmpty(), "Подзадача не должна загружаться из файла");
    }

    @Test
    public void checkThatEpicWithSubtasksIsLoadingCorrectlyFromCsvFile() {
        // Создаю задачу с подзадачами
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(epic, subtask1);

        // Создаю новый менеджер, возвращаю разными способами задачи,
        // загруженные из файла.
        taskManager = FileBackedTasksManager.loadFromFile(OF);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        final Subtask savedSubtask1 = taskManager.getSubtaskById(subtaskId);

        final Map<Integer, Epic> epicMap = taskManager.getEpics();
        final List<Integer> subtaskIdForEpic = savedEpic.getSubtaskIdForEpic();
        final int epicIdForSubtask = savedSubtask1.getEpicIdForSubtask();
        final List<Subtask> subtasksList = taskManager.getSubTasksList();
        final Map<Integer, Subtask> subtaskMap = taskManager.getSubTasks();

        // Проверяю задачи до и после.
        assertFalse(epicMap.isEmpty(), "Эпик задача не загрузилась из файла в Map");
        assertEquals(1, epicMap.size(), "Из файла должна была загрузиться только одна задача");
        assertEquals(epic, savedEpic, "Эпик задачи до и после не совпадают");
        assertFalse(subtaskIdForEpic.isEmpty(), "Из файла не загрузился id подзадачи");
        assertEquals(1, epicIdForSubtask, "id эпика не загрузился в список id эпика для подзадачи");
        assertEquals(2, subtaskIdForEpic.get(0), "id подзадачи не загрузился в поле id подзадачи для эпика");
        assertFalse(subtaskMap.isEmpty(), "Подзадача не загрузилась в Map");
        assertFalse(subtasksList.isEmpty(), "Подзадача не возвращается через метод getSubTasksList()");
        assertEquals(subtask1, savedSubtask1, "Подзадача до и после не совпадает");
        assertEquals(subtask1, subtasksList.get(0),"Подзадача до и после не совпадает");
        assertEquals(subtask1, subtaskMap.get(2),"Подзадача до и после не совпадает");
    }

    @Test
    public void checkThatIfThereIsNoHistoryItIsNotLoadedFromCsvFile() {
        // Создаю задачи, но не вызываю их по id
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);

        // Создаю новый менеджер, подгружаю историю
        taskManager = FileBackedTasksManager.loadFromFile(OF);

        final List<Task> taskHistory = taskManager.getHistory();

        // Проверяю, что история пустая
        assertTrue(taskHistory.isEmpty(), "История должна быть пустая");
    }

    @Test
    public void checkThatIfThereIsHistoryItIsLoadedFromCsvFile() {
        // Создаю задачи, вызываю по id
        final int taskId = taskManager.createTask(task);
        final int epicId = taskManager.createEpic(epic);

        taskManager.createSubtask(epic, subtask1);

        final Task savedTask = taskManager.getTaskById(taskId);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        // Создаю новый менеджер, подгружаю историю
        taskManager = FileBackedTasksManager.loadFromFile(OF);

        final List<Task> taskHistory = taskManager.getHistory();

        // Проверяю историю до и после
        assertFalse(taskHistory.isEmpty(), "История просмотров из файла не загружается");
        assertEquals(savedTask, taskHistory.get(0), "История просмотров загружается из файла неправильно");
        assertEquals(savedEpic, taskHistory.get(1), "История просмотров загружается из файла неправильно");
    }

    @Test
    public void checkThatTaskTimesCorrectlyLoadedFromCsvFile() {
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

        // Создаем новый менеджер, возвращаем загруженные из файла задачи
        taskManager = FileBackedTasksManager.loadFromFile(OF);

        final Task loadTask = taskManager.getTaskById(taskId);
        final Epic loadEpic = taskManager.getEpicById(epicId);
        final Subtask loadSubtask = taskManager.getSubtaskById(subtaskId);

        // Проверяем время задач до и после.
        assertEquals(taskStartTime, loadTask.getStartTime(), "Время из файла загружается неправильно");
        assertEquals(taskDuration, loadTask.getDuration(), "Время из файла загружается неправильно");
        assertEquals(epicStartTime, loadEpic.getStartTime(), "Время из файла загружается неправильно");
        assertEquals(epicDuration, loadEpic.getDuration(), "Время из файла загружается неправильно");
        assertEquals(subtaskStartTime, loadSubtask.getStartTime(), "Время из файла загружается неправильно");
        assertEquals(subtaskDuration, loadSubtask.getDuration(), "Время из файла загружается неправильно");
    }

    @AfterAll
    public static void deleteTestFile() {
        try {
            Files.delete(OF);
        } catch (IOException e) {
            System.out.println("Не получилось удалить файл");
            throw new RuntimeException(e.getMessage());
        }
    }
}


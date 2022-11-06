package ru.yandex.practicum.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    public void createTasks() {
        task = new Task("Test Task name",
                        "Test Task Description",
                        TaskStatus.NEW,
                        LocalDateTime.now(),
                        Duration.ZERO);

        epic = new Epic("Test Epic name",
                        "Test Epic Description",
                        TaskStatus.NEW,
                        LocalDateTime.now(),
                        Duration.ZERO);

        subtask1 = new Subtask("Test Subtask1 name",
                               "Test Subtask Description",
                               TaskStatus.NEW,
                               LocalDateTime.now(),
                               Duration.ZERO);

        subtask2 = new Subtask("Test Subtask2 name",
                               "Test Subtask1 Description",
                               TaskStatus.NEW,
                               LocalDateTime.now(),
                               Duration.ZERO);
    }

    @Test
    public void setIdToTaskManagerAndCheckThatItIsSaved() {
        // Передаём id в менеджер
        taskManager.setTaskId(1);

        // Возвращаем id из менеджера
        final int savedId = taskManager.getTaskId();

        // Сверяемся
        assertEquals(1, savedId, "id не совпадают");
    }

    // Тесты для простых задач
    @Test
    public void createTaskAndCheckItExistsInTaskManager() {
        // Создаём задачу
        final int taskId = taskManager.createTask(task);

        // Возвращаем задачу по id и проверяем
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена по Id");
        assertEquals(task, savedTask, "Задачи должны совпадать");

        // Возвращаем лист задач и проверяем
        final List<Task> tasksList = taskManager.getTasksList();

        assertFalse(tasksList.isEmpty(), "Список задач не должен быть пустым");
        assertEquals(1, tasksList.size(), "В Map должна быть 1 задача");
        assertEquals(task, tasksList.get(0), "Задачи не совпадают");

        // Возвращаем Map задач и проверяем
        final Map<Integer, Task> tasksMap = taskManager.getTasks();

        assertNotNull(tasksMap, "Map задач не должен быть null");
        assertTrue(tasksMap.containsKey(taskId), "Ключ в Map должен совпадать с id задачи");
        assertEquals(task, tasksMap.get(taskId), "Задачи не совпадают");
    }

    @Test
    public void setTaskToMapAndCheckThatItIsSavedInMap() {
        // Передаём задачу в Map
        task.setId(1);
        taskManager.setTasksMap(task);

        // Возвращаем Map задач
        final Map<Integer, Task> tasksMap = taskManager.getTasks();

        // Проверяем, что задачи совпадают
        assertEquals(task, tasksMap.get(1), "Задачи не совпадают");
    }

    @Test
    public void updateTaskStatusAndMakeSureItHasChanged() {
        // Создаём задачу
        final int taskId = taskManager.createTask(task);

        // Меняем статус задачи, делаем апдейт
        task.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task);

        // Возвращаем задачу из менеджера по Id проверяем статус
        final Task checkTaskStatus = taskManager.getTaskById(taskId);
        final TaskStatus taskStatus = checkTaskStatus.getStatus();

        assertEquals(TaskStatus.DONE, taskStatus, "Статусы не совпадают");
    }

    @Test
    public void deleteTaskByIdAndCheckItsAbsenceInTaskManager() {
        // Создаём задачу
        final int taskId = taskManager.createTask(task);

        // Удаляем задачу по id, а потом пытаемся её вернуть всевозможными методами и проверяем
        taskManager.deleteTaskById(taskId);

        final Task deleteTask = taskManager.getTaskById(taskId);
        final List<Task> deleteTasksList = taskManager.getTasksList();
        final Map<Integer, Task> deleteTasksMap = taskManager.getTasks();

        assertNull(deleteTask, "Задача не удалена");
        assertTrue(deleteTasksList.isEmpty(), "Задача не удалена");
        assertTrue(deleteTasksMap.isEmpty(), "Задача не удалена");
    }

    @Test
    public void deleteAllTasksAndCheckTheirAbsenceInTaskManager() {
        // Создаём 2 задачи
        final int taskId1 = taskManager.createTask(task);
        final int taskId2 = taskManager.createTask(new Task("Test Task1 name",
                                                            "Test Task1 Description",
                                                            TaskStatus.NEW,
                                                            null,
                                                            null));

        // Удаляем все задачи, а потом пытаемся их вернуть всевозможными методами и проверяем
        taskManager.clearTasks();

        final Task deleteTask1 = taskManager.getTaskById(taskId1);
        final Task deleteTask2 = taskManager.getTaskById(taskId2);
        final List<Task> deleteTasksList = taskManager.getTasksList();
        final Map<Integer, Task> deleteTasksMap = taskManager.getTasks();

        assertNull(deleteTask1, "Задача не удалена");
        assertNull(deleteTask2, "Задача не удалена");
        assertTrue(deleteTasksList.isEmpty(), "Задачи не удалены полностью");
        assertTrue(deleteTasksMap.isEmpty(), "Задачи не удалены полностью");
    }

    // Тесты со временем для задач
    @Test
    public void setNullToTaskShouldReturnThisTime() {
        // Передаю в поле startTime null.
        task.setStartTime(null);

        // Проверяю, что возвращаются null и Duration.Zero
        assertNull(task.getStartTime(), "startTime должен быть null");
        assertEquals(Duration.ZERO, task.getDuration(), "Поле Duration должно быть Duration.Zero");

        // Создаю задачу с null временем
        final Task task = new Task("Test Task1 name",
                                   "Test Task1 Description",
                                   TaskStatus.NEW,
                                   null,
                                   null);

        // Проверяю, что возвращаются null и Duration.Zero
        assertNull(task.getStartTime(), "Поле startTime должно быть null");
        assertEquals(Duration.ZERO, task.getDuration(), "Поле Duration должно быть Duration.Zero");
    }

    @Test
    public void setTimeToTaskShouldReturnThisTime() {
        // Проверяю, что время в startTime и duration не null
        assertNotNull(task.getStartTime(), "В startTime не должен быть null");
        assertNotNull(task.getDuration(), "В поле duration не должен быть null");

        // Изменяю время, передаю его в Task.
        final LocalDateTime startTime = LocalDateTime.now();
        final Duration duration = Duration.ofMinutes(10);

        task.setStartTime(startTime);
        task.setDuration(duration);

        // Проверяю
        assertEquals(startTime, task.getStartTime(), "В startTime не должен быть null");
        assertEquals(duration, task.getDuration(), "В поле duration не должен быть null");
    }

    @Test
    public void setInDurationNullShouldReturnDurationZero() {
        // Передаю в Duration null
        LocalDateTime startTime = task.getStartTime();
        task.setDuration(null);

        // Проверяю, что возвращается Duration.Zero, а startTime не меняется.
        assertEquals(Duration.ZERO, task.getDuration(), "При переданном в поле duration - null, должно вернуться Duration.Zero");
        assertEquals(startTime, task.getStartTime(), "При переданном в duration null, startTime не должен измениться");
    }

    @Test
    public void setTimeOrNullToTaskAndCheckEndTime() {
        // Возвращаю startTime и duration, рассчитываю endTime.
        LocalDateTime startTime = task.getStartTime();
        Duration duration = task.getDuration();
        LocalDateTime endTime = startTime.plus(duration);

        // Проверяю endTime
        assertEquals(endTime, task.getEndTime(), "endTime не совпадают");

        // Передаю в задачу время, возвращаю, рассчитываю endTime
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofDays(10));

        startTime = task.getStartTime();
        duration = task.getDuration();
        endTime = startTime.plus(duration);

        // Проверяю
        assertEquals(endTime, task.getEndTime(), "endTime не совпадают");

        // Передаю в startTime и duration null
        task.setStartTime(null);
        task.setDuration(null);

        // Проверяю endTime
        assertNull(task.getEndTime(), "endTime должен быть null");
    }

    // Тесты для Эпик задач
    @Test
    public void createEpicAndCheckItExistsInTaskManager() {
        // Создаём Эпик задачу. Также создаем подзадачу. Она нам пригодится, чтобы проверить id подзадачи для эпика
        final int epicId = taskManager.createEpic(epic);

        // Возвращаем Эпик задачу по id и проверяем
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена по Id");
        assertEquals(epic, savedEpic, "Епик задачи не совпадают");

        // Возвращаем лист Эпик задач и проверяем
        final List<Epic> epics = taskManager.getEpicsList();

        assertNotNull(epics, "Список Эпик задач не должен быть null");
        assertEquals(1, epics.size(), "В Map должна быть 1 задача");
        assertEquals(epic, epics.get(0), "Задачи не совпадают");

        // Возвращаем Map Эпик задач и проверяем
        final Map<Integer, Epic> epicsMap = taskManager.getEpics();

        assertNotNull(epicsMap, "Map задач не должен быть null");
        assertTrue(epicsMap.containsKey(epicId), "Ключ в Map должен совпадать с id задачи");
        assertEquals(epic, epicsMap.get(epicId), "Задачи не совпадают");

    }

    @Test
    public void createEpicWithSubtasksAndCheckSubtasksForEpic() {
        // Создаём Эпик задачу и подзадачу.
        taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(epic, subtask1);

        // Возвращаем лист подзадач Эпика и проверяем
        final List<Subtask> subtasksForEpic = taskManager.getSubtaskForEpic(epic);

        assertFalse(subtasksForEpic.isEmpty(), "Список с подзадачами Эпика не должен быть пустым");
        assertEquals(1, subtasksForEpic.size(), "В списке должна быть 1 подзадача");
        assertEquals(subtask1, subtasksForEpic.get(0), "Подзадачи не совпадают");

        // Возвращаем лист id подзадач для Эпика и проверяем
        final List<Integer> subtasksIdForEpic = epic.getSubtaskIdForEpic();

        assertFalse(subtasksIdForEpic.isEmpty(), "Список Id подзадач Эпика не должен быть пустым");
        assertEquals(1, subtasksIdForEpic.size(), "В списке должнен быть 1 id");
        assertEquals(subtaskId, subtasksIdForEpic.get(0), "Id не совпадают");
    }

    @Test
    public void setEpicToMapAndCheckThatItIsSavedInMap() {
        // Передаём Эпик задачу в Map
        epic.setId(1);
        taskManager.setEpicsMap(epic);

        // Возвращаем Map Эпик задач
        final Map<Integer, Epic> epicsMap = taskManager.getEpics();

        // Проверяем, что Эпик задачи совпадают
        assertEquals(epic, epicsMap.get(1), "Эпик задачи не совпадают");
    }

    @Test
    public void checkThatForAnEmptyListOfSubtasksEpicStatusIsNew() {
        // Создаём Эпик задачу
        final int epicId = taskManager.createEpic(epic);

        // Возвращаем Эпик задачу и его лист подзадач из менеджера.
        final Epic savedEpic = taskManager.getEpicById(epicId);
        final List<Integer> subtaskIdForEpic = savedEpic.getSubtaskIdForEpic();

        // Проверяем, что список подзадач пуст. Потом проверяем статус Епик задачи
        assertTrue(subtaskIdForEpic.isEmpty(), "Список id подзадач должен быть пуст");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void checkThatForListOfSubtasksWithNewStatusEpicStatusIsNew() {
        // Создаём Эпик задачу и две подзадачи
        final int epicId = taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic, subtask2);

        // Делаем апдейт, возвращаем Эпик задачу из менеджера и проверяем ее статус
        taskManager.updateEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void checkThatForListOfSubtasksWithDoneStatusEpicStatusIsDone() {
        // Создаём Эпик задачу и две подзадачи
        final int epicId = taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic, subtask2);

        // Меняем статусы подзадач
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        // Делаем апдейт, возвращаем Эпик задачу из менеджера и проверяем ее статус
        taskManager.updateEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.DONE, savedEpic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void checkThatForListOfSubtasksWithNewAndDoneStatusEpicStatusIsDone() {
        // Создаём Эпик задачу и две подзадачи
        final int epicId = taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic, subtask2);

        // Меняем статус подзадачи
        subtask1.setStatus(TaskStatus.DONE);

        // Делаем апдейт, возвращаем Эпик задачу из менеджера и проверяем ее статус
        taskManager.updateEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void checkThatForListOfSubtasksWithInProgressStatusEpicStatusIsDone() {
        // Создаём Эпик задачу и две подзадачи
        final int epicId = taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic, subtask2);

        // Меняем статусы подзадач
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        // Делаем апдейт, возвращаем Эпик задачу из менеджера и проверяем ее статус
        taskManager.updateEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void deleteEpicByIdAndCheckItsAbsenceInTaskManager() {
        // Создаём Эпик задачу и подзадачу
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId1 = taskManager.createSubtask(epic, subtask1);

        // Удаляем Эпик задачу по id, а потом пытаемся её вернуть всевозможными методами
        taskManager.deleteEpicById(epicId);

        final Epic deleteEpic = taskManager.getEpicById(epicId);
        final List<Epic> deleteEpicList = taskManager.getEpicsList();
        final Map<Integer, Epic> deleteEpicMap = taskManager.getEpics();
        final List<Integer> deleteSubtaskIdForEpic = epic.getSubtaskIdForEpic();
        final Subtask deleteSubtask1 = taskManager.getSubtaskById(subtaskId1);

        // Проверяем отсутствие Эпик задачи, ее подзадачи и листа подзадач для этого Эпика
        assertNull(deleteEpic, "Епик должен быть null");
        assertTrue(deleteEpicList.isEmpty(), "Список с Эпик задачами должен быть пуст");
        assertTrue(deleteEpicMap.isEmpty(), "Map с Эпик задачами должен быть пуст");
        assertNull(deleteSubtask1, "Подзадача Эпика должна быть null");
        assertTrue(deleteSubtaskIdForEpic.isEmpty(), "Список id подзадач Эпика должен быть пуст");
    }

    @Test
    public void deleteAllEpicsAndCheckTheirAbsenceInTaskManager() {
        // Создаём 2 Эпик задачи
        final int epicId1 = taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic, subtask2);

        final int epicId2 = taskManager.createEpic(new Epic("Test Epic1 name",
                                                            "Test Epic1 Description",
                                                            TaskStatus.NEW,
                                                            null,
                                                            null));

        // Удаляем все Эпик задачи, а потом пытаемся их вернуть всевозможными методами
        // Так же возвращаю Map подзадач Эпик задачи.
        taskManager.clearEpics();

        final Epic deleteEpic1 = taskManager.getEpicById(epicId1);
        final Epic deleteEpic2 = taskManager.getEpicById(epicId2);
        final List<Epic> deleteEpicList = taskManager.getEpicsList();
        final Map<Integer, Epic> deleteEpicMap = taskManager.getEpics();
        final Map<Integer, Subtask> deleteSubtaskMap = taskManager.getSubTasks();

        // Проверяем, что эпик задачи и их подзадачи удалены.
        assertNull(deleteEpic1, "Эпик задача должна быть null");
        assertNull(deleteEpic2, "Эпик задача должна быть null");
        assertTrue(deleteEpicList.isEmpty(), "Лист Эпик задач должен быть пустым");
        assertTrue(deleteEpicMap.isEmpty(), "Map Эпик задач должен быть пуст");
        assertTrue(deleteSubtaskMap.isEmpty(), "Map подзадач задач должен быть пуст");
    }

    // Тесты со временем для Эпика
    @Test
    public void setNullToEpicShouldReturnThisTime() {
        // Передаю в поле startTime null.
        epic.setStartTime(null);

        // Проверяю, что возвращаются null и Duration.Zero
        assertNull(epic.getStartTime(), "startTime должен быть null");
        assertEquals(Duration.ZERO, epic.getDuration(), "duration должно быть Duration.Zero");

        // Создаю задачу с null временем
        epic = new Epic("Test Epic name",
                        "Test Epic Description",
                        TaskStatus.NEW,
                        null,
                        null);

        // Проверяю, что возвращаются null и Duration.Zero
        assertNull(epic.getStartTime(), "startTime должен быть null");
        assertEquals(Duration.ZERO, epic.getDuration(), "duration должно быть Duration.Zero");
    }

    @Test
    public void setTimeToEpicShouldReturnThisTime() {
        // Проверяю, что время в startTime и duration не null
        assertNotNull(epic.getStartTime(), "В startTime должно быть время");
        assertNotNull(epic.getDuration(), "В duration должно быть время");

        // Изменяю время, передаю его в Epic.
        final LocalDateTime startTime = LocalDateTime.now();
        final Duration duration = Duration.ofMinutes(10);

        epic.setStartTime(startTime);
        epic.setDuration(duration);

        // Проверяю
        assertEquals(startTime, epic.getStartTime(), "В startTime должно быть время");
        assertEquals(duration, epic.getDuration(), "В duration должно быть время");
    }

    @Test
    public void setInDurationNullToEpicShouldReturnDurationZero() {
        // Передаю в Duration null
        final LocalDateTime startTime = epic.getStartTime();
        epic.setDuration(null);

        // Проверяю, что возвращается Duration.Zero, а startTime не меняется.
        assertEquals(Duration.ZERO, epic.getDuration(), "При переданном в duration - null, должно вернуться Duration.Zero");
        assertEquals(startTime, epic.getStartTime(), "При переданном в duration null, startTime не должен измениться");
    }

    @Test
    public void setTimeOrNullToEpicAndCheckEndTime() {
        // Возвращаю startTime и duration, рассчитываю endTime.
        LocalDateTime startTime = epic.getStartTime();
        Duration duration = epic.getDuration();
        LocalDateTime endTime = startTime.plus(duration);

        // Проверяю endTime
        assertEquals(endTime, epic.getEndTime(), "Созданный endTime и возвращенный endTime не совпадают");

        // Передаю в Эпик задачу время, возвращаю, рассчитываю endTime
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ofDays(10));

        startTime = epic.getStartTime();
        duration = epic.getDuration();
        endTime = startTime.plus(duration);

        // Проверяю
        assertEquals(endTime, epic.getEndTime(), "Созданный endTime и возвращенный endTime не совпадают");

        // Передаю в startTime и duration null
        epic.setStartTime(null);
        epic.setDuration(null);

        // Проверяю endTime
        assertNull(epic.getEndTime(), "В Эпик задачу передано время null. endTime должен стать null");
    }

    @Test
    public void createSubtasksForEpicAndCheckEpicTime() {
        // Создаем подзадачи для Эпика
        final Subtask subtask = new Subtask("Test Subtask name",
                                            "Test Subtask description",
                                            TaskStatus.NEW,
                                            LocalDateTime.now().minusDays(1),
                                            Duration.ZERO);

        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic, subtask2);

        // Проверяем время эпика из расчета его подзадач
        assertEquals(subtask.getStartTime(), epic.getStartTime(), "Время Эпика на основе его подзадач рассчитано неверно");
        assertEquals(subtask2.getEndTime(), epic.getEndTime(), "Время Эпика на основе его подзадач рассчитано неверно");

        // Перетасовываем время подзадач
        subtask1.setDuration(Duration.ofDays(1));
        subtask2.setStartTime(LocalDateTime.now().minusDays(2));

        // Делаем апдейт эпика
        taskManager.updateEpic(epic);

        // Снова проверяем
        assertEquals(subtask2.getStartTime(), epic.getStartTime(), "Время Эпика на основе его подзадач рассчитано неверно");
        assertEquals(subtask1.getEndTime(), epic.getEndTime(), "Время Эпика на основе его подзадач рассчитано неверно");

        // Снова перетасовываем время подзадач. У одной подзадачи стираем время.
        subtask2.setStartTime(null);
        subtask1.setStartTime(LocalDateTime.now().plusDays(1));

        // Апдейт
        taskManager.updateEpic(epic);

        // Финальная проверка
        assertEquals(subtask.getStartTime(), epic.getStartTime(), "Время Эпика на основе его подзадач рассчитано неверно");
        assertEquals(subtask1.getEndTime(), epic.getEndTime(), "Время Эпика на основе его подзадач рассчитано неверно");
    }

    // Тесты для подзадач
    @Test
    public void createSubtaskAndCheckItExistsInTaskManager() {
        // Создаём подзадачу.
        taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(epic, subtask1);

        // Возвращаем подзадачу по id и проверяем ее наличие
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена по Id");
        assertEquals(subtask1, savedSubtask, "Задачи должны совпадать");

        // Возвращаем лист подзадач и проверяем его наличие
        final List<Subtask> subtasks = taskManager.getSubTasksList();

        assertFalse(subtasks.isEmpty(), "Список задач не должен быть пустым");
        assertEquals(1, subtasks.size(), "В Map должна быть 1 задача");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают");

        // Возвращаем Map подзадач и проверяем ее наличие
        final Map<Integer, Subtask> subtaskMap = taskManager.getSubTasks();

        assertNotNull(subtaskMap, "Map задач не должен быть null");
        assertTrue(subtaskMap.containsKey(subtaskId), "Ключ в Map должен совпадать с id задачи");
        assertEquals(subtask1, subtaskMap.get(subtaskId), "Задачи не совпадают");
    }

    @Test
    public void setSubtaskToMapAndCheckThatItIsSavedInMap() {
        // Передаём подзадачу в Map
        subtask1.setId(1);
        taskManager.setSubtasksMap(subtask1);

        // Возвращаем Map подзадач
        final Map<Integer, Subtask> subtasksMap = taskManager.getSubTasks();

        // Проверяем, что подзадачи совпадают
        assertEquals(subtask1, subtasksMap.get(1), "Подзадачи не совпадают");
    }

    @Test
    public void updateSubtaskStatusAndMakeSureItHasChanged() {
        // Создаём подзадачу.
        taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(epic, subtask1);

        // Передаем новый статус и делаем апдейт
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        // Возвращаем задачу из менеджера по Id проверяем статус
        final Subtask checkSubtaskStatus = taskManager.getSubtaskById(subtaskId);
        final TaskStatus subtaskStatus = checkSubtaskStatus.getStatus();

        assertEquals(TaskStatus.DONE, subtaskStatus, "Статус подзадачи не изменился после апдейта");
    }

    @Test
    public void checkThatEpicIdIsAttachedToSubtask() {
        // Создаём подзадачу.
        taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(epic, subtask1);

        // Возвращаем id Эпика для подзадачи и проверяем
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        final int epicIdForSubtask = savedSubtask.getEpicIdForSubtask();

        assertTrue(epicIdForSubtask != 0, "Id Эпик задачи не добавлен в поле подзадачи");
        assertEquals(1, epicIdForSubtask, "id Эпик задачи должен быть 1");
    }

    @Test
    public void deleteSubtaskByIdAndCheckItsAbsenceInTaskManager() {
        // Создаём подзадачу.
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(epic, subtask1);

        // Удаляем подзадачу по id, а потом пытаемся её вернуть всевозможными методами
        // Также возвращаем лист подзадач Эпик задачи
        taskManager.deleteSubtaskById(subtaskId);

        final Subtask deleteSubtask = taskManager.getSubtaskById(subtaskId);
        final List<Subtask> deleteSubtaskList = taskManager.getSubTasksList();
        final Map<Integer, Subtask> deleteSubtaskMap = taskManager.getSubTasks();
        final int deleteEpicIdForSubtask = subtask1.getEpicIdForSubtask();
        final Epic savedEpic = taskManager.getEpicById(epicId);
        final List<Integer> subtaskIdForEpic = savedEpic.getSubtaskIdForEpic();

        // Проверяем, что подзадача удалена, и её Id удален и списка Id Эпик задачи
        assertNull(deleteSubtask, "Подзадача должна быть null");
        assertTrue(deleteSubtaskList.isEmpty(), "Список с этой подзадачей должен быть пуст");
        assertTrue(deleteSubtaskMap.isEmpty(), "Map c подзадачей должен быть пуст");
        assertEquals(0, deleteEpicIdForSubtask, "id Эпика подзадачи должен быть 0");
        assertTrue(subtaskIdForEpic.isEmpty(), "Id подзадачи в списке Эпика быть не должно.");
    }

    // todo
    @Test
    public void createEpicAndSubtaskTimesOfEpicAndSubtasksMustMatch() {
        // Создаю Эпик и первую подзадачу. startTime подзадачи раньше, endTime – позже
        final int epicId = taskManager.createEpic(epic);
        subtask1.setStartTime(LocalDateTime.now().minusDays(1));
        subtask1.setDuration(Duration.ofMinutes(25));
        final int subtaskId1 = taskManager.createSubtask(epic, subtask1);

        Epic savedEpic = taskManager.getEpicById(epicId);
        Subtask savedSubtask1 = taskManager.getSubtaskById(subtaskId1);

        // Проверяю, что время у Эпика переназначилось
        assertEquals(savedEpic.getStartTime(), savedSubtask1.getStartTime(), "Время Эпика и его первой задачи должны совпадать");
        assertEquals(savedEpic.getEndTime(), savedSubtask1.getEndTime(), "Время Эпика и его первой задачи должны совпадать");

        // Создаю вторую подзадачу с более ранним startTime
        subtask2.setStartTime(LocalDateTime.now().minusDays(10));
        subtask2.setDuration(Duration.ofDays(2));

        final int subtaskId2 = taskManager.createSubtask(epic, subtask2);

        Subtask savedSubtask2 = taskManager.getSubtaskById(subtaskId2);

        // Проверяю, что startTime у Эпика стал раньше, а endTime не изменился.
        assertEquals(savedEpic.getStartTime(), savedSubtask2.getStartTime(), "startTime Эпика должен быть равен самому раннему startTime всех подзадач");
        assertEquals(savedEpic.getEndTime(), savedSubtask1.getEndTime(), "endTime Эпика должен быть равен самому позднему endTime всех подзадач");
    }

    @Test
    public void deleteAllSubtasksAndCheckTheirAbsenceInTaskManager() {
        // Создаём 2 подзадачи
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId1 = taskManager.createSubtask(epic, subtask1);
        final int subtaskId2 = taskManager.createSubtask(epic, subtask2);

        // Удаляем все подзадачи, а потом пытаемся их вернуть всевозможными методами
        // Также возвращаем лист подзадач Эпик задачи
        taskManager.clearSubtasks();

        // Проверяем, что все подзадачи удалены, и их Id удалены и списка Id Эпик задачи
        final Subtask deleteSubtask1 = taskManager.getSubtaskById(subtaskId1);
        final Subtask deleteSubtask2 = taskManager.getSubtaskById(subtaskId2);
        final List<Subtask> deleteSubtaskList = taskManager.getSubTasksList();
        final Map<Integer, Subtask> deleteSubtaskMap = taskManager.getSubTasks();
        final Epic savedEpic = taskManager.getEpicById(epicId);
        final List<Integer> subtaskIdForEpic = savedEpic.getSubtaskIdForEpic();

        // Проверяем, что эпик задачи и их подзадачи удалены.
        assertNull(deleteSubtask1, "Подзадача должна быть null");
        assertNull(deleteSubtask2, "Подзадача должна быть null");
        assertTrue(deleteSubtaskList.isEmpty(), "Список подзадач должен быть пуст");
        assertTrue(deleteSubtaskMap.isEmpty(), "Map подзадач должен быть пуст");
        assertTrue(subtaskIdForEpic.isEmpty(), "Список id подзадач эпика должен быть пустой");
    }

    @Test
    public void deleteAllTasksEpicsSubtasksAndCheckTheirAbsenceInTaskManager() {
        // Создаем все виды задач
        final int taskId = taskManager.createTask(task);
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId1 = taskManager.createSubtask(epic, subtask1);
        final int subtaskId2 = taskManager.createSubtask(epic, subtask2);

        // Удаляем все задачи
        taskManager.clearAllTasks();

        // Пытаемся вернуть задачи разными методами
        final Task deleteTask = taskManager.getTaskById(taskId);
        final List<Task> deleteTasksList = taskManager.getTasksList();
        final Map<Integer, Task> deleteTasksMap = taskManager.getTasks();

        // Проверяем, что задачи удалены
        assertNull(deleteTask, "Задача должна быть null");
        assertTrue(deleteTasksList.isEmpty(), "Список задач должен быть пустым");
        assertTrue(deleteTasksMap.isEmpty(), "Map задач должен быть пустым");

        // Пытаемся вернуть Эпик задачи и подзадачи разными методами
        final Epic deleteEpic1 = taskManager.getEpicById(epicId);
        final List<Epic> deleteEpicList = taskManager.getEpicsList();
        final Map<Integer, Epic> deleteEpicMap = taskManager.getEpics();

        final Subtask deleteSubtask1 = taskManager.getSubtaskById(subtaskId1);
        final Subtask deleteSubtask2 = taskManager.getSubtaskById(subtaskId2);
        final List<Subtask> deleteSubtaskList = taskManager.getSubTasksList();
        final Map<Integer, Subtask> deleteSubtaskMap = taskManager.getSubTasks();

        // Проверяем, что Эпик задачи и их подзадачи удалены.
        assertNull(deleteEpic1, "Эпик задача должна быть null");
        assertTrue(deleteEpicList.isEmpty(), "Список Эпик задач должен быть пустым");
        assertTrue(deleteEpicMap.isEmpty(), "Map задач должен быть пустым");
        assertNull(deleteSubtask1, "Подзадача должна быть null");
        assertNull(deleteSubtask2, "Подзадача должна быть null");
        assertTrue(deleteSubtaskList.isEmpty(), "Список подзадач должен быть пустым");
        assertTrue(deleteSubtaskMap.isEmpty(), "Map подзадач должен быть пустым");
    }

    // Тесты со временем для задач
    @Test
    public void setNullToSubtaskShouldReturnThisTime() {
        // Передаю в поле startTime null.
        subtask1.setStartTime(null);

        // Проверяю, что возвращаются null и Duration.Zero
        assertNull(subtask1.getStartTime(), "startTime должен быть null");
        assertEquals(Duration.ZERO, subtask1.getDuration(), "duration должно быть Duration.Zero");

        // Создаю задачу с null временем
        subtask1 = new Subtask("Test Subtask1 name",
                               "Test Subtask1 Description",
                               TaskStatus.NEW,
                               null,
                               null);

        // Проверяю, что возвращаются null и Duration.Zero
        assertNull(subtask1.getStartTime(), "startTime должен быть null");
        assertEquals(Duration.ZERO, subtask1.getDuration(), "duration должно быть Duration.Zero");
    }

    @Test
    public void setTimeToSubtaskShouldReturnThisTime() {
        // Проверяю, что время в startTime и duration не null
        assertNotNull(subtask1.getStartTime(), "В startTime должно быть время");
        assertNotNull(subtask1.getDuration(), "В duration должно быть время");

        // Изменяю время, передаю его в Task.
        final LocalDateTime startTime = LocalDateTime.now();
        final Duration duration = Duration.ofMinutes(10);

        subtask1.setStartTime(startTime);
        subtask1.setDuration(duration);

        // Проверяю
        assertEquals(startTime, subtask1.getStartTime(), "В startTime должно быть время");
        assertEquals(duration, subtask1.getDuration(), "В duration должно быть время");
    }

    @Test
    public void setInDurationNullToSubtaskShouldReturnDurationZero() {
        // Передаю в Duration null
        final LocalDateTime startTime = subtask1.getStartTime();
        subtask1.setDuration(null);

        // Проверяю, что возвращается Duration.Zero, а startTime не меняется.
        assertEquals(Duration.ZERO, subtask1.getDuration(), "При переданном duration null, должно вернуться Duration.Zero");
        assertEquals(startTime, subtask1.getStartTime(), "При переданном в duration null, startTime не должен измениться");
    }

    @Test
    public void setTimeOrNullToSubtaskAndCheckEndTime() {
        // Возвращаю startTime и duration, рассчитываю endTime.
        LocalDateTime startTime = subtask1.getStartTime();
        Duration duration = subtask1.getDuration();
        LocalDateTime endTime = startTime.plus(duration);

        // Проверяю endTime
        assertEquals(endTime, subtask1.getEndTime(), "endTimes не совпадают");

        // Передаю в задачу время, возвращаю, рассчитываю endTime
        subtask1.setStartTime(LocalDateTime.now());
        subtask1.setDuration(Duration.ofDays(10));

        startTime = subtask1.getStartTime();
        duration = subtask1.getDuration();
        endTime = startTime.plus(duration);

        // Проверяю
        assertEquals(endTime, subtask1.getEndTime(), "endTimes не совпадают");

        // Передаю в startTime и duration null
        subtask1.setStartTime(null);
        subtask1.setDuration(null);

        // Проверяю endTime
        assertNull(subtask1.getEndTime(), "endTime должен быть null");
    }

    // Тест для всех задач
    @Test
    public void createTasksAndCheckPrioritizedTasks() {
        // Создаем задачи
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic, subtask2);

        // Тасуем время
        task.setStartTime(LocalDateTime.now().plusDays(1));
        subtask1.setStartTime(LocalDateTime.now().plusDays(2));
        subtask2.setStartTime(LocalDateTime.now().minusHours(2));

        // Возвращаем приоритетный список
        List<Task> prioritizedTasks = new ArrayList<>(taskManager.getPrioritizedTasks());

        Task task1 = prioritizedTasks.get(0);
        Task task2 = prioritizedTasks.get(1);
        Task task3 = prioritizedTasks.get(2);

        // Сравниваем
        assertEquals(subtask2, task1, "Приоритетный список неправильно отсортировал задачи");
        assertEquals(task, task2, "Приоритетный список неправильно отсортировал задачи");
        assertEquals(subtask1, task3, "Приоритетный список неправильно отсортировал задачи");

        // Время одной задачи делаем null
        subtask2.setStartTime(null);

        // Снова возвращаем приоритетный список
        prioritizedTasks = new ArrayList<>(taskManager.getPrioritizedTasks());

        task1 = prioritizedTasks.get(0);
        task2 = prioritizedTasks.get(1);
        task3 = prioritizedTasks.get(2);

        // Проверяем
        assertEquals(task, task1, "Приоритетный список неправильно отсортировал задачи");
        assertEquals(subtask1, task2, "Приоритетный список неправильно отсортировал задачи");
        assertEquals(subtask2, task3, "Приоритетный список неправильно отсортировал задачи");
    }
}


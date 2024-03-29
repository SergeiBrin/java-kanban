package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskManager {
    Integer createTask(Task task); // Присваивает простой задаче Id, добавляет её в Map и возвращает Id.

    Integer createEpic(Epic epic); // Присваивает Эпик-задаче Id, добавляет её в Map и возвращает Id.

    Integer createSubtask(Epic epic, Subtask subtask); // Присваивает подзадаче Id, добавляет её в Map и возвращает Id.

    Map<Integer, Task> getTasks(); // Возвращает Map простых задач.

    Map<Integer, Epic> getEpics(); // Возвращает Map епик задач.

    Map<Integer, Subtask> getSubTasks(); // Возвращает Map подзадач.

    int getTaskId(); // Возвращает id менеджера задач.

    void setTaskId(int taskId); // Передает id.

    void setTasksMap(Task task); // Передает простую задачу в Map.

    void setEpicsMap(Task task); // Передает эпик задачу в Map.

    void setSubtasksMap(Task task); // Передает подзадачу в Map.

    List<Task> getAllTaskList(); // Возвращает список всех задач.

    List<Task> getTasksList(); // Возвращает список Tasks.Task-объектов из Map.

    List<Epic> getEpicsList(); // Возвращает список Tasks.Epic-объектов из Map

    List<Subtask> getSubTasksList(); // Возвращает список подзадач-объектов из Map.

    List<Subtask> getSubtaskForEpic(Epic epic); // Возвращает список подзадач-объектов определенного эпика.

    void clearAllTasks(); // Удаляет задачи из всех Map.

    void clearTasks(); // Удаляет простые задачи.

    void clearEpics(); // Удаляет Епик-задачи и подзадачи, так как подзадачи

    void clearSubtasks(); // Удаляет подзадачи и связь подзадач с Эпиками – Id подадач в объектах Эпик.

    Task getTaskById(int idTask); // Возвращает простую задачу по Id. Если такой задачи нет, возвращает Null.

    Epic getEpicById(int idEpic); // Возвращает Епик-задачу по Id. Если такой задачи нет, возвращает Null.

    Subtask getSubtaskById(int idSubtask); // Возвращает подзадачу по Id. Если такой задачи нет, возвращает Null.

    void deleteTaskById(int removeTask); // Удаление задачи по идентификатору.

    void deleteEpicById(int removeEpic); // Удаление Епик-задачи по идентификатору.

    void deleteSubtaskById(int removeSubtask); // // Удаление подзадачи по идентификатору.

    void printTaskList(); // Выводит список простых задач в консоль.

    void printEpicList(); // Выводит список эпик-задач в консоль.

    void printSubtaskList(); // Выводит список подзадач в консоль.

    void updateTask(Task newTask); // Обновление простых задач и их статусов.

    void updateEpic(Epic newEpic); // Обновление Эпик-задач и их статусов.

    void updateSubtask(Subtask newSubtask); // Обновление подзадач и их статусов.

    List<Task> getHistory(); // Возвращает историю

    Set<Task> getPrioritizedTasks(); // Возвращает отсортированный по startTime список задач

    boolean getIsDeleteTask();
}


package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void createTask(Task task); // Присваивает простой задаче Id и добавляет её в Map.

    void createEpic(Epic epic); // Присваивает Эпик-задаче Id и добавляет её в Map.

    void createSubtask(Epic epic, Subtask subtask); // Присваивает подзадаче Id и добавляет её в Map.

    ArrayList<Task> getTasksList(); // Возвращает список Tasks.Task-объектов из Map.

    ArrayList<Epic> getEpicsList(); // Возвращает список Tasks.Epic-объектов из Map

    ArrayList<Subtask> getSubTasksList(); // Возвращает список подзадач-объектов из Map.

    ArrayList<Subtask> getSubtaskForEpic(Epic epic); // Возвращает список подзадач-объектов определенного эпика.

    String clearAllTasks(); // Удаляет задачи из всех Map.

    void clearTasks(); // Удаляет простые задачи.

    void clearEpics(); // Удаляет Епик-задачи и подзадачи, так как подзадачи

    void clearSubtasks(); // Удаляет подзадачи и связь подзадач с Эпиками – Id подадач в объектах Эпик.

    Task getTaskById(int idTask); // Возвращает простую задачу по Id. Если такой задачи нет, возвращает Null.

    Epic getEpicById(int idEpic); // Возвращает Епик-задачу по Id. Если такой задачи нет, возвращает Null.

    Subtask getSubtaskById(int idSubtask); // Возвращает подзадачу по Id. Если такой задачи нет, возвращает Null.

    String deleteTaskById(int removeTask); // Удаление задачи по идентификатору.

    String deleteEpicById(int removeEpic); // Удаление Епик-задачи по идентификатору.

    String deleteSubtaskById(int removeSubtask); // // Удаление подзадачи по идентификатору.

    void printTaskList(); // Выводит список простых задач в консоль.

    void printEpicList(); // Выводит список эпик-задач в консоль.

    void printSubtaskList(); // Выводит список подзадач в консоль.

    void updateTask(Task newTask); // Обновление простых задач и их статусов.

    void updateEpic(Epic newEpic); // Обновление Эпик-задач и их статусов.

    void updateSubtask(Subtask newSubtask); // Обновление подзадач и их статусов.

    List<Task> getHistory(); // Метод нужен, чтобы вернуть исторический лист задач из класса InMemoryHistoryManager(),
}                            // используя при этом объект типа TaskManager, созданный в классе Managers.


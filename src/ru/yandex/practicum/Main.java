package ru.yandex.practicum;

import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Let's go!");
        TaskManager taskManager = Managers.getDefault();
        testProgram(taskManager);

        /* Привет, Эркин :). Замечания я устранил.

        1. Теперь после удаления подзадачи автоматически происходит
        обновление эпика. А ещё раскомментировал и применил код в методе удаления подзадачи,
        который ты одобрил.

        2. Переписал код, чтобы сравнение enum происходило через equals.
        Это сделано в классе InMemoryTaskManager; для твоего удобства я поставил закладку
        на 311 строчку кода. Старый код я закомментировал. Это я сделал для того,
        чтобы ты посмотрел и помог мне понять, с какой версией реализации лучше.

        (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧
        */
    }

    private static void testProgram(TaskManager taskManager) {
        Task task = new Task("Переехать", "Денег болт, поэтому поеду в Чехию цыганом", TaskStatus.NEW);
        Task task1 = new Task("Купить машину", "Хотя бы Жигули", TaskStatus.NEW);

        Epic epic = new Epic("Найти время, чтоб отдохнуть", "Совсем ничего не делать", TaskStatus.NEW);
        Subtask subtask = new Subtask("Лечь на кровать", "Аккуратно, чтоб не прихватило спину", TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Уснуть", "Закрыть глазки и шёпотом считать овечек ", TaskStatus.NEW);

        Epic epic1 = new Epic("Проснуться с утра ", "Будильник включится в 6:00", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Вставить спички в глаза", "По три штуки на глаз", TaskStatus.NEW);

        taskManager.createTask(task);
        taskManager.createTask(task1);

        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic1, subtask2);

        System.out.println(taskManager.getTasksList() + "\n");
        System.out.println(taskManager.getEpicsList() + "\n");
        System.out.println(taskManager.getSubTasksList() + "\n");

        task.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task);
        taskManager.printTaskList();

        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        taskManager.printEpicList();
        taskManager.printSubtaskList();

        subtask1.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subtask1);
        taskManager.updateEpic(epic);
        taskManager.printEpicList();

        taskManager.deleteSubtaskById(5);
        taskManager.printEpicList();

        taskManager.updateEpic(epic);
        taskManager.updateTask(task);

        taskManager.printEpicList();
        taskManager.printSubtaskList();
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistory());

        Task task2 = null;
        taskManager.updateTask(task2); // Проверка на передачу null объекта на апдейт.
    }
}


package ru.yandex.practicum;

import ru.yandex.practicum.logic.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Let's go!");
        TaskManager manager = new TaskManager();
        testProgram(manager);

        /* Почти все замечания исправил, кроме рекомендации по entrySet() и рефакторингу метода
        updateEpicWithSubtask() с добавлением параметра int – тут я просто не понимаю, что мне нужно сделать – это
        плохая новость. Но вероятно я пойму это чуть позже, когда наберусь новых знаний – это хорошая новость :)

        Также я добавил 3 метода в TaskManager, которые выводят информацию по простым задачам,
        эпик-задачам и подзадачам – в консоль. Методы называются:
        printTaskList(), printEpicList(), printSubtaskList();

        Вопрос: в какой пакет по правилам нужно положить класс Main – в какой-то специальный пакет или, может быть,
        его лучше оставить в src?
        */
    }

    private static void testProgram(TaskManager manager) {

        Task task = new Task("Переезд", "По приезду в Прагу уничтожить обратный билет", "NEW");
        Task task1 = new Task("Купить машину", "Хотя бы Жигули", "NEW");

        Epic epic = new Epic("Найти время, чтоб отдохнуть", "Совсем ничего не делать", "NEW");
        Subtask subtask = new Subtask("Лечь на кровать", "Аккуратно, чтоб не прихватило спину", "NEW");
        Subtask subtask1 = new Subtask("Уснуть", "Закрыть глазки и шёпотом посчитать овечек ", "NEW");

        Epic epic1 = new Epic("Проснуться с утра ", "Будильник включится в 6:00", "NEW");
        Subtask subtask2 = new Subtask("Вставить спички в глаза", "По три штуки на глаз", "NEW");

        manager.createTask(task);
        manager.createTask(task1);

        manager.createEpic(epic);
        manager.createEpic(epic1);

        manager.createSubtask(epic, subtask);
        manager.createSubtask(epic, subtask1);
        manager.createSubtask(epic1, subtask2);

        System.out.println(manager.getTasksList() + "\n");
        System.out.println(manager.getEpicsList() + "\n");
        System.out.println(manager.getSubTasksList() + "\n");

        task.setStatus("DONE");
        manager.updateTask(task);
        System.out.println(manager.getTasksList() + "\n");

        subtask.setStatus("DONE");
        manager.updateSubtask(subtask);

        System.out.println(manager.getSubTasksList() + "\n");
        System.out.println(manager.getEpicsList() + "\n");

        subtask1.setStatus("DONE");
        manager.updateSubtask(subtask1);
        System.out.println(manager.getEpicsList() + "\n");

        manager.deleteTaskById(1);
        manager.deleteSubtaskById(5);

        manager.updateSubtask(subtask);
        manager.updateTask(task);

        manager.printTaskList();
        manager.printEpicList();
        manager.printSubtaskList();
    }
}


package ru.yandex.practicum;

import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Let's go!");
        TaskManager manager = new TaskManager();
        testProgram(manager);

        /* Все замечания устранил, но для этого пришлось переработать все методы update...();
        Теперь эпики обновляются по Id эпика или подзадачи. Но для того, чтобы апдейт случился,
        необходимо передать в один из методов update...() объект эпика или подзадачи с существующим в Map Id.

        Про entrySet() почитал, но не применил :(. Мне показалось, что в этой реализации методов update(),
        от entrySet() – кода меньше не станет. Эркин, я ведь не ошибаюсь? Или я опять парю немножко?
        */
    }

    private static void testProgram(TaskManager manager) {
        Task task = new Task("Переезд", "Денег болт, поэтому поеду в Чехию цыганом", "NEW");
        Task task1 = new Task("Купить машину", "Хотя бы Жигули", "NEW");
        Task task2 = null;
        
        manager.updateTask(task2);

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

        manager.deleteTaskById(2);
        manager.deleteSubtaskById(5);

        manager.updateEpic(epic);
        manager.updateTask(task);

        manager.printTaskList();
        manager.printEpicList();
        manager.printSubtaskList();
    }
}


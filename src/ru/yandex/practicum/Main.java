package ru.yandex.practicum;

import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Let's go!");
        TaskManager taskManager = Managers.getDefault();

        testProgram(taskManager);

        /* Привет, Эркин.
        Сегодня буду немногословен, потому что мне очень нехорошо.
        Из-за того, что один человек решил утащить нас всех в пропасть, а многих и на тот свет,
        я плохо сплю и думаю совершенно не о программировании. Очень тяжело собраться с мыслями.

        Но я всё же собрался и дописал код 6 спринта. После много раз продебажил и, вроде бы,
        все ошибки устранил.

        Объяснения работы кода оставил прям в новых классах – по ходу кода.
        Так тебе будет легче разобрать мои каракули :).

        Код для проверки FileBackedTasksManager лежит в нём.

        До встречи. Береги себя.
        */
    }

    private static void testProgram(TaskManager taskManager) {
        Task task = new Task("Переехать", "Денег болт поэтому поеду в Чехию цыганом", TaskStatus.NEW);
        Task task1 = new Task("Купить машину", "Хотя бы Жигули", TaskStatus.NEW);

        Epic epic = new Epic("Найти время чтоб отдохнуть", "Совсем ничего не делать", TaskStatus.NEW);
        Subtask subtask = new Subtask("Лечь на кровать", "Аккуратно чтоб не прихватило спину", TaskStatus.NEW);
        Subtask subtask1 = new Subtask("Уснуть", "Закрыть глазки и шёпотом считать овечек", TaskStatus.NEW);

        Epic epic1 = new Epic("Проснуться с утра ", "Будильник включится в 6:00", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Вставить спички в глаза", "По три штуки на глаз", TaskStatus.NEW);

        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.createTask(task1);
        taskManager.printTaskList();
        System.out.println();


        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic1, subtask2);

        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);

        taskManager.getTaskById(1);
        taskManager.getEpicById(3);

        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);
        taskManager.deleteSubtaskById(5);
        taskManager.deleteSubtaskById(6);

        taskManager.clearSubtasks();
        taskManager.clearEpics();
        taskManager.clearTasks();
        taskManager.clearAllTasks();


        List<Task> list = taskManager.getHistory();
        for (Task tasks : list) {
            System.out.println(tasks);
        }
    }
}


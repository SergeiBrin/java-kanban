package ru.yandex.practicum;

import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Let's go!");
        TaskManager taskManager = Managers.getDefault();
        testProgram(taskManager);

        /* Привет, Эркин :). Я тут немножко приболел, и возможно даже смертельно :( (Это не шутка)...
        Так получилось, что уже до начала 5 спринта я целыми днями бегал по больницам, а по вечерам никак не мог
        собраться с мыслями. И, несмотря на происходящий с моим здоровьем пздц, я всё равно старался и стараюсь учиться –
        просто потому что мне это нравится.

        К сожалению, проблемы все-таки пошатнули меня, и я отстал. Финальную работу 5 спринта я закончил только сейчас.
        Но есть и хорошие новости: c сегодняшнего дня у меня будет много времени, плюс я собрался с мыслями, поэтому
        я всё наверстаю к концу 8 спринта.

        Теперь по проекту.
        Двусвязный список реализовал. Класс Node закинул в отдельный пакет. Поля класса Node сделал приватными
        и добавил к ним get и set. В классе Main оставил закомментированные строки для проверки работоспособности
        программы.

        Жду твоих строгих комментариев на мою работу :)
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

        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);

        // taskManager.getTaskById(1);
        // taskManager.getEpicById(3);
        // taskManager.deleteTaskById(1);
        // taskManager.deleteEpicById(3);
        // taskManager.deleteSubtaskById(5);

        List<Task> list = taskManager.getHistory();
        System.out.println(list);
    }
}


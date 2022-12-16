package ru.yandex.practicum;

import ru.yandex.practicum.http.server.HttpTaskServer;
import ru.yandex.practicum.http.server.KVServer;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.enums.TaskStatus;
import ru.yandex.practicum.utils.Managers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Main {
    /* Привет, Эркин :)

       Я внёс правки по твоим рекомендациям.
       Спасибо, так действительно стало лучше :)

       Представляешь, вчера ехал в поезде и весь вечер ловил интернет,
       чтоб отправить тебе задание на проверку. Просто был непрерывный null по связи.
       Я даже, в какой-то момент уснул)))

       Но потом поезд подъехал к Нижнему Новгороду, и я всё быстренько отправил;
       даже, по ходу дела, чуть-чуть улучшил код. Такая вот история.
       Даже не знаю, зачем я её вообще рассказал)))). Но да ладно.

       Работу отправляю. Теперь на очереди 9 спринт. Говорят, что он легче заходит, чем 8.
       Верю, скоро проверю)))).

       Хорошего дня :)
     */

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
        new KVServer().start();

        TaskManager manager = Managers.getDefault();
        testProgram(manager);
    }

    private static void testProgram(TaskManager taskManager) {
        Task task = new Task(
                "Task name",
                "Task description",
                TaskStatus.NEW,
                null,
                Duration.ofDays(1));

        Epic epic = new Epic(
                "Epic name",
                "Epic description",
                TaskStatus.NEW,
                null,
                null);

        Subtask subtask1 = new Subtask(
                "Subtask1 name",
                "Subtask1 description",
                TaskStatus.NEW,
                LocalDateTime.now(),
                Duration.ofDays(2));

        Subtask subtask2 = new Subtask(
                "Subtask2 name",
                "Subtask2 description",
                TaskStatus.NEW,
                LocalDateTime.now().minusDays(2),
                Duration.ZERO);

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask1);
        taskManager.createSubtask(epic, subtask2);

        taskManager.getEpicById(2);

        TaskManager newTaskManager = Managers.getDefault();

        List<Task> tasksList = newTaskManager.getAllTaskList();
        Set<Task> prioritizedTasksList = newTaskManager.getPrioritizedTasks();
        List<Task> historyList = newTaskManager.getHistory();

        for (Task task1 : tasksList) {
            System.out.println(task1);
        }

        System.out.println();

        for (Task task1 : prioritizedTasksList) {
            System.out.println(task1);
        }

        System.out.println();

        for (Task task1 : historyList) {
            System.out.println(task1);
        }
    }
}


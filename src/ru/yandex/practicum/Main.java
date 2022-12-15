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

       В общем, я все-таки задержался в отпуске, потому что
       решил вернуться к конкретному наставнику. Это все потому, что люди для меня очень важны.

       Эта работа оказалась для меня ооооооочень сложной для понимания и трудозатратной, –
       но я ее сделал, правда очень сильно устал.

       Надеюсь, что все будет окей, так как времени у меня осталось до понедельника.

       Ну все, отправляю на ревью))))
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


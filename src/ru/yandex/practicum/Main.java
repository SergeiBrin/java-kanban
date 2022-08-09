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

        /* Привет, Эркин. Правки отправил не сразу, потому что собирался по делам в Москву и ничего не успевал.
        Но как только я сел в поезд, у меня тут же появилось время, чтобы всё привести в подобающий вид.

        В итоге я устранил все замечения, кроме одного – рекомендательного замечания по созданию подзадачи.
        Почему его нельзя устранить в данной реализации я описал перед самим методом
        createSubtask(Epic epic, Subtask subtask); – в классе InMemoryTaskManager, 41 строчка кода.

        Enum через equals я сравниваю в классе InMemoryTaskManager – 322 строчка кода. Как всё-таки правильнее
        сравнивать Enum – через equals или через == ?

        В методе deleteSubtaskById(int removeSubtask) я немножко упростил реализацию, получилось меньше кода.
        Но применять ее пока не стал (она закомментирована), хочу сначала услышать твоё мнение. Вдруг
        это решение нехорошее.

        Ну вот вроде бы и всё, что я хотел написать (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧
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
        taskManager.printEpicList();

        taskManager.deleteSubtaskById(5);

        taskManager.updateEpic(epic);
        taskManager.updateTask(task);

        taskManager.printEpicList();
        taskManager.printSubtaskList();
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getTaskById(2);
        taskManager.getHistory();

        Task task2 = null;
        taskManager.updateTask(task2); // Проверка на передачу null объекта на апдейт.
    }
}


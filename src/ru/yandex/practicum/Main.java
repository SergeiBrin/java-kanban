package ru.yandex.practicum;

import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Let's go!");
        TaskManager taskManager = Managers.getDefault();
        testProgram(taskManager);

        /* Эркин, привет :) Ты видишь этот код, а это значит, что я выполнил (очень конечно на это надеюсь)
           поставленную задачу 4 спринта.

           В этот раз дело пошло быстрее, чем в прошлый раз (возможно просто становлюсь умнее))), хотя и без трудностей
           не обошлось. Загвостка заключалась вот в чём:

           в самом конце задания, когда в ТЗ попросили создать в классе Managers
           статический метод HistoryManager getDefaultHistory(), который будет возвращать новый объект
           типа HistoryManager, я сначала завис, а потом скатился на пол полежать. И всё это из-за того,
           что я нет мог понять, как это будет работать, ведь если оставить возможность бесконтрольно
           создавать новые объекты типа HistoryManager, то List задач в классе InMemoryHistoryManager
           постоянно будет обнуляться. А если предположить, что будет создан только один объект типа HistoryManager,
           то чтобы взаимодействовать с ним в классе InMemoryTaskManager, нужно будет ещё и передать его в этот класс
           в нужные методы – в виде параметра, что тоже казалось мне некорректным решением.

           Я долго думал об этом, пока лежал на полу, но ничего дельного не придумал...
           В итоге, на помощь мне пришёл одногруппник и дал подсказку, по которой я и реализовал код. Логика такая:
           у пользователя нет возможности, благодаря модификатору Default, из класса Main обратиться к
           Managers.getDefaultHistory(), чтобы создать новый объект HistoryManager. Вместо этого новый объект типа
           HistoryManager создается в конструкторе класса InMemoryTaskManager. Тем самым отсекается возможность
           бесконтрольно создавать пустые, не привязанные к задачам, объекты типа HistoryManager. А также появляется
           возможность прямо из класса InMemoryTaskManager обращаться к методам класса InMemoryHistoryManager.
           Такая вот длинная и скучная история :))))

           А теперь мелочи:
           В классе InMemoryTaskManager есть 3 метода (я их убрал на самое дно), которые я не вынес в интерфейс,
           потому что они работают только внутри класса и закрыты модификатором private. Если я их вынесу
           в интерфейс, то мне придётся их открыть и сделать Public, что делать не нужно.

           Статусы Enum я сравнивал через equals. Практикум говорит, что можно сравнивать через equals и через == –
           и так и так правильно. Но как всё-таки правильнее? Подскажешь?

           Все что связвно с менеджерами, я положил в один пакет managers, а всё, что связано с задачами –
           в пакет tasks.

           Ну вот вроде бы и всё, что я хотел тебе рассказать, Эркин. На дворе уже утро,
           а это значит, что пора ложиться спать))). Но сперва отправлю работу тебе на проверку.

           push */
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
        System.out.println(taskManager.getHistory());

        Task task2 = null;
        taskManager.updateTask(task2); // Проверка на передачу null объекта на апдейт.
    }
}


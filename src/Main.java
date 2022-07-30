public class Main {

    public static void main(String[] args) {
        System.out.println("Let's go!");
        Manager manager = new Manager();
        testProgram(manager);

        /* Привет, Эркин. Рад новой встрече :)

        Это моя третья и, пожалуй, самая сложная работа. Над ней я так много думал,
        что даже разболелась голова. Но я всё превозмог!

        Как обычно, я оставил для тебя комментарии в коде: тут я конечно постарался, что возможно
        даже переборщил. Но это не страшно, так как в будущем я лишнее удалю. Я просто делаю для твоего удобства,
        ну и потому что самому нравится:)

        Из мелочей: в классах я не стал добавлять все гетеры и сеттеры - прописал только те, которые задействованы
        в коде. А в методе deleteTaskById(int removeTask) я вернул String. Возможно это неправильно,
        но я хотел оповестить человека на той стороне, как сработал метод)

        Ну вот и всё. Делаю add, commit, push. */
    }

    private static void testProgram(Manager manager) {

        Task task = new Task("Переезд", "По приезду в Милан выкинуть обратный билет", "NEW");
        Task task1 = new Task("Купить машину", "Желательно Ламборджини Галлардо", "NEW");

        Epic epic = new Epic("Найти время, чтоб отдохнуть", "Совсем ничего не делать", "NEW");
        Subtask subtask = new Subtask("Лечь на кровать", "Аккуратно, чтоб не прихватило спину", "NEW");
        Subtask subtask1 = new Subtask("Уснуть", "Закрыть глазки и шёпотом посчитать овечек ", "NEW");

        Epic epic1 = new Epic("Проснуться с утра ", "Будильник включится в 6:00", "NEW");
        Subtask subtask2 = new Subtask("Вставить спички в глаза", "По три штуки на глаз", "NEW");

        manager.createTask(task);
        manager.createTask(task1);

        manager.createEpic(epic);
        manager.createEpic(epic1);

        manager.createSubTask(epic, subtask);
        manager.createSubTask(epic, subtask1);
        manager.createSubTask(epic1, subtask2);

        System.out.println(manager.getTasksList() + "\n");
        System.out.println(manager.getEpicsList() + "\n");
        System.out.println(manager.getSubTasksList() + "\n");

        task.setStatus("DONE");
        manager.updateTask(task);
        System.out.println(manager.getTasksList() + "\n");

        subtask.setStatus("DONE");
        manager.updateTask(subtask);

        System.out.println(manager.getSubTasksList() + "\n");
        System.out.println(manager.getEpicsList() + "\n");

        subtask1.setStatus("DONE");
        manager.updateTask(subtask1);
        System.out.println(manager.getEpicsList() + "\n");

        manager.deleteTaskById(1);
        manager.deleteTaskById(5);
        manager.updateTask(task);

        System.out.println(manager.getTasksList());
        System.out.println(manager.getSubTasksList());
        System.out.println(manager.getEpicsList());
    }
}


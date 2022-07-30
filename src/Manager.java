import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int taskId;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subTasks;

    public Manager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        taskId = 1;
    }

    public void createTask(Task task) { // Присваивает простой задаче Id и добавляет её в Map.
        if (task == null) {
            return;
        }

        task.setId(taskId);
        tasks.put(taskId, task);
        taskId++;
    }

    public void createEpic(Epic epic) { // Присваивает Эпик-задаче Id и добавляет её в Map.
        if (epic == null) {
            return;
        }

        epic.setId(taskId);
        epics.put(taskId, epic);
        taskId++;
    }

    public void createSubTask(Epic epic, Subtask subtask) { // Присваивает подзадаче Id и добавляет её в Map.
        if (epic == null || subtask == null) {
            return;
        }

        if (epic.getId() == 0) { // Если у объекта Epic id = 0, значит, что он ещё не был идентифицирован менеджером.
            createEpic(epic);    // Поэтому отправляем объект для идентификации и добавления в Map.
        }

        subtask.setId(taskId);

        epic.setSubtasksIdForEpic(taskId);
        subtask.setEpicIdForSubtask(epic.getId());

        subTasks.put(taskId, subtask);
        taskId++;
    }

    public ArrayList<Task> getTasksList() { // Возвращает список Task-объектов из Map.
        ArrayList<Task> tasksList = new ArrayList<>();
        for (int keyTask : tasks.keySet()) {
            Task task = tasks.get(keyTask);
            tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<Epic> getEpicsList() { // Возвращает список Epic-объектов из Map
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (int keyEpic : epics.keySet()) {
            epicsList.add(epics.get(keyEpic));
        }
        return epicsList;
    }

    public ArrayList<Subtask> getSubTasksList() { // Возвращает список подзадач-объектов из Map.
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (int keySt : subTasks.keySet()) {
            subtasksList.add(subTasks.get(keySt));
        }
        return subtasksList;
    }

    public ArrayList<Subtask> getSubtaskForEpic(Epic epic) { // Возвращает список подзадач-объектов определенного эпика.
        if (epic == null) {
            return null;
        }

        ArrayList<Subtask> subtasks = new ArrayList<>();

        for (int keySt : subTasks.keySet()) {
            if (epic.getStIdForEpic().contains(keySt)) {
                subtasks.add(subTasks.get(keySt));
            }
        }
        return subtasks;
    }

    public String clearAllTasks() { // Удаляет задачи из всех Map.
        tasks.clear();
        epics.clear();
        subTasks.clear();
        return "Все списки задач очищены, и у вас больше нет ни одной задачи. " +
                "Наконец-то настал тот день, когда Вы сможете полежать :)";
    }

    public void clearTasks() { // Удаляет простые задачи.
        tasks.clear();
    }

    public void clearEpics() { // Удаляет Епик-задачи и подзадачи, так как подзадачи
        epics.clear();         // являются частью эпик-задач и связаны с ними.
        subTasks.clear();
    }

    public void clearSubtasks() { // Удаляет подзадачи и связь подзадач с Эпиками – Id подадач в объектах Эпик.
        subTasks.clear();
        for (int keyEpic : epics.keySet()) {
            epics.get(keyEpic).clearStIdForEpic();
        }
    }

    public Task getTaskById(int idTask) {    // Получение задачи по идендификатору.
        for (int keyTask : tasks.keySet()) { // Так как Id у всех задач (простых, епик и подзадач) точно разный,
            if (idTask == keyTask) {         // то метод для поиска задачи по идентификатору – один для всех.
                return tasks.get(keyTask);
            }
        }

        for (int keyEpic : epics.keySet()) {
            if (idTask == keyEpic) {
                return epics.get(keyEpic);
            }
        }

        for (int keySt : subTasks.keySet()) {
            if (idTask == keySt) {
                return subTasks.get(keySt);
            }
        }

        return null;
    }

    public String deleteTaskById(int removeTask) { // Удаление задачи по идентификатору. Метод один для всех задач.
        for (int keyTask : tasks.keySet()) {
            if (removeTask == keyTask) {
                tasks.remove(keyTask);
                return "Задача с идентификатором " + keyTask + " удалена.";
            }
        }

        for (int keyEpic : epics.keySet()) {
            if (removeTask == keyEpic) {
                epics.remove(keyEpic);
                return "Эпик-задача с идентификатором " + keyEpic + " удалена.";
            }
        }

        for (int keySt : subTasks.keySet()) {
            if (removeTask == keySt) {
                /* Перед удалением подзадачи удаляю связь подзадачи с эпиком.
                   Так как связь лежит в ArrayList объекта эпик в виде id подзадачи,
                   удаляю её по заведомо известному значению ↓ */
                for (int keyEpic : epics.keySet()) {
                    boolean isTrue = epics.get(keyEpic).getStIdForEpic().contains(keySt);
                    if (isTrue) {
                        epics.get(keyEpic).removeStIdForEpic(keySt);
                    }
                }
                subTasks.remove(keySt);
                return "Подзадача с идентификатором " + keySt + " удалена.";
            }
        }
        return "Задачи с идентификатором " + removeTask + " нет :(";
    }

    public void updateTask(Task newTask) { // Обновление задач и их статусов. Метод один для всех задач.
        if (newTask == null) {
            return;
        }

        if (newTask.getId() == 0) {
            return;
        }

        for (int keyTask : tasks.keySet()) {
            if (newTask.getId() == keyTask) {
                tasks.put(keyTask, newTask);
            }
        }

        /* Тут кроме апдейта Епик в Map происходит проверка эпиков на наличие у них подзадач.
        Если окажется, что у какого-то из эпиков их нет, например, потому что их удалили через метод
        Task getTaskById(int idTask), то статус Эпика должен измениться на NEW,
        так как по условию задачи Эпик без подзадач - это NEW */
        for (int keyEpic : epics.keySet()) {
            if (newTask.getId() == keyEpic) {
                epics.put(keyEpic, (Epic) newTask);
            }
            boolean isEmpty = epics.get(keyEpic).getStIdForEpic().isEmpty();
            if (isEmpty) {
                epics.get(keyEpic).setStatus("NEW");
            }
        }

        for (int keySt : subTasks.keySet()) {
            if (newTask.getId() == keySt) {
                subTasks.put(keySt, (Subtask) newTask);
            }
        }
        updateEpicWithSubtask();
    }

    /* Это метод, в котором проверяются статусы подзадач – для решения о статусе Эпика,
       связанного с этими подзадачами. Вынес в отдельный приват метод, чтобы глаза не мозолил
       в методе апдейт. Возможно я это зря конечно... Если будет нужно, то я верну его обратно */
    private void updateEpicWithSubtask() {
        for (int keySt : subTasks.keySet()) {
            int epicId = subTasks.get(keySt).getEpicIdForSubtask();
            ArrayList<Integer> subtaskIdForEpic = epics.get(epicId).getStIdForEpic();
            ArrayList<String> subtaskStatus = new ArrayList<>();

            for (int keyStForEpic : subtaskIdForEpic) {
                subtaskStatus.add(subTasks.get(keyStForEpic).getStatus());
            }

            if ((subtaskStatus.contains("NEW")) && (subtaskStatus.contains("DONE"))) {
                epics.get(epicId).setStatus("IN_PROGRESS");
            } else if (subtaskStatus.contains("DONE")) {
                epics.get(epicId).setStatus("DONE");
            } else {
                epics.get(epicId).setStatus("NEW");
            }
        }
    }
}






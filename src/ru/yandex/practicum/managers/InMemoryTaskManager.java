package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.TaskStatus;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int taskId;
    private final HashMap<Integer, Task> tasks; // Fix: сделал final
    private final HashMap<Integer, Epic> epics; // Fix: сделал final
    private final HashMap<Integer, Subtask> subTasks; // Fix: сделал final
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory(); // Возвращает новый объект типа HistoryManager.
    }

    @Override
    public void createTask(Task task) { // Присваивает простой задаче Id и добавляет её в Map.
        if (task == null) {
            return;
        }

        task.setId(++taskId);
        tasks.put(taskId, task);
    }

    @Override
    public void createEpic(Epic epic) { // Присваивает Эпик-задаче Id и добавляет её в Map.
        if (epic == null) {
            return;
        }

        epic.setId(++taskId);
        epics.put(taskId, epic);
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) { // Присваивает подзадаче Id и добавляет её в Map.
        if (epic == null || subtask == null) {
            return;
        }

        if (epic.getId() == 0) { // Если у объекта Tasks.Epic id = 0, значит, что он ещё не был идентифицирован менеджером.
            createEpic(epic);    // Поэтому отправляем объект для идентификации и добавления в Map.
        }

        subtask.setId(++taskId);

        epic.setSubtasksIdForEpic(taskId);
        subtask.setEpicIdForSubtask(epic.getId());

        subTasks.put(taskId, subtask);
    }

    @Override
    public ArrayList<Task> getTasksList() { // Возвращает список Tasks.Task-объектов из Map.
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() { // Возвращает список Tasks.Epic-объектов из Map
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubTasksList() { // Возвращает список подзадач-объектов из Map.
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskForEpic(Epic epic) { // Возвращает список подзадач-объектов определенного эпика.
        if (epic == null) {
            return null;
        }

        ArrayList<Subtask> subtasks = new ArrayList<>();

        for (int keySt : subTasks.keySet()) {
            if (epic.getSubtaskIdForEpic().contains(keySt)) {
                subtasks.add(subTasks.get(keySt));
            }
        }
        return subtasks;
    }

    @Override
    public String clearAllTasks() { // Удаляет задачи из всех Map.
        clearTasks();
        clearEpics();
        return "Все списки задач очищены, и у вас больше нет ни одной задачи. " +
                "Наконец-то настал тот день, когда Вы сможете полежать :)";
    }

    @Override
    public void clearTasks() { // Удаляет простые задачи.
        tasks.clear();
    }

    @Override
    public void clearEpics() { // Удаляет Епик-задачи и подзадачи, так как подзадачи
        epics.clear();         // являются частью эпик-задач и связаны с ними.
        subTasks.clear();
    }

    @Override
    public void clearSubtasks() { // Удаляет подзадачи и связь подзадач с Эпиками – Id подадач в объектах Эпик.
        subTasks.clear();
        for (int keyEpic : epics.keySet()) {
            epics.get(keyEpic).clearStIdForEpic();
        }
    }

    @Override
    public Task getTaskById(int idTask) { // Возвращает простую задачу по Id и отправляет эту задачу в метод, который
        if (tasks.get(idTask) != null) {  // добавляет её в исторический список задач.
            add(tasks.get(idTask));
            return tasks.get(idTask);
        }
        return null;
    }

    @Override
    public Epic getEpicById(int idEpic) { // Возвращает Епик-задачу по Id и отправляет эту задачу в метод, который
        if (epics.get(idEpic) != null) {  // добавляет её в исторический список задач.
            add(epics.get(idEpic));
            return epics.get(idEpic);
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int idSubtask) { // Возвращает подзадачу по Id и отправляет эту задачу в метод, который
        if (subTasks.get(idSubtask) != null) {     // добавляет её в исторический список задач.
            add(subTasks.get(idSubtask));
            return subTasks.get(idSubtask);
        }
        return null;
    }

    @Override
    public String deleteTaskById(int removeTask) { // Удаление задачи по идентификатору.
        if (tasks.get(removeTask) != null) {
            tasks.remove(removeTask);
            return "Задача с идентификатором " + removeTask + " удалена.";
        }
        return "Задачи с идентификатором " + removeTask + " нет :(";
    }

    @Override
    public String deleteEpicById(int removeEpic) { // Удаление Епик-задачи по идентификатору.
        if (epics.get(removeEpic) != null) {
            epics.remove(removeEpic);
            return "Задача с идентификатором " + removeEpic + " удалена.";
        }
        return "Задачи с идентификатором " + removeEpic + " нет :(";
    }

    @Override
    public String deleteSubtaskById(int removeSubtask) {
        if (subTasks.get(removeSubtask) != null) {
                /* Перед удалением подзадачи удаляю связь подзадачи с эпиком.
                   Так как связь лежит в ArrayList объекта эпик в виде id подзадачи,
                   удаляю её по заведомо известному значению ↓ */
            for (int keyEpic : epics.keySet()) {
                boolean isTrue = epics.get(keyEpic).getSubtaskIdForEpic().contains(removeSubtask);
                if (isTrue) {
                    epics.get(keyEpic).removeStIdForEpic(removeSubtask);
                }
            }
            subTasks.remove(removeSubtask);
            return "Подзадача с идентификатором " + removeSubtask + " удалена.";
        }
        return "Задачи с идентификатором " + removeSubtask + " нет :(";
    }

    @Override
    public void printTaskList() { // Выводит список простых задач в консоль.
        for (Integer key : tasks.keySet()) {
            Task task = tasks.get(key);
            System.out.println(task);
        }
    }

    @Override
    public void printEpicList() { // Выводит список эпик-задач в консоль.
        for (Integer key : epics.keySet()) {
            Epic epic = epics.get(key);
            System.out.println(epic);
        }
    }

    @Override
    public void printSubtaskList() { // Выводит список подзадач в консоль.
        for (Integer key : subTasks.keySet()) {
            Subtask subtask = subTasks.get(key);
            System.out.println(subtask);
        }
    }

    @Override
    public void updateTask(Task newTask) { // Обновление простых задач и их статусов.
        boolean isNotNull = checkTaskForNull(newTask); // Проверка на Null

        if (isNotNull) {
            boolean isTaskIdTrue = tasks.containsKey(newTask.getId()); // Проверка на соответствие ключей простой задачи.
            if (isTaskIdTrue) {
                tasks.put(newTask.getId(), newTask);
            }
        } else {
            System.out.println("Не получилось сделать апдейт простой задачи. Для того, чтобы сделать апдейт – " +
                    "передайте простую задачу с правильным Id");
        }
    }

    /* Тут кроме апдейта Епик в Map происходит проверка эпиков на наличие у них подзадач.
    Если окажется, что у какого-то из эпиков их нет, например, потому что их полностью удалили через метод
    deleteSubtaskById(int removeSubtask), то статус Эпика должен измениться на NEW,
    так как по условию задачи Эпик без подзадач - это NEW */
    @Override
    public void updateEpic(Epic newEpic) { // Обновление Эпик-задач и их статусов.
        boolean isNotNull = checkTaskForNull(newEpic); // Проверка на Null.

        if (isNotNull) {
            boolean isEpicIdTrue = epics.containsKey(newEpic.getId()); // Проверка на соответствие ключей Эпик-задачи.
            if (isEpicIdTrue) {
                epics.put(newEpic.getId(), newEpic);
                updateEpicWithSubtask(newEpic.getId());

                for (int keyEpic : epics.keySet()) { // Проверка Эпиков на наличие подзадач. Если их нет, то Эпик – NEW.
                    boolean isSubtaskForEpicEmpty = epics.get(keyEpic).getSubtaskIdForEpic().isEmpty();
                    if (isSubtaskForEpicEmpty) {
                        epics.get(keyEpic).setStatus(TaskStatus.NEW);
                    }
                }
            }
        } else {
            System.out.println("Не получилось сделать апдейт эпик-задачи. Для того, чтобы сделать апдейт – " +
                    "передайте эпик-задачу с правильным Id");
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask) { // Обновление подзадач и их статусов.
        boolean isNotNull = checkTaskForNull(newSubtask); // Проверка на Null.

        if (isNotNull) {
            boolean isSubtaskIdTrue = subTasks.containsKey(newSubtask.getId()); // Проверка на соответствие ключей подзадачи.
            if (isSubtaskIdTrue) {
                subTasks.put(newSubtask.getId(), newSubtask);
                updateEpicWithSubtask(newSubtask.getId());
            }
        } else {
            System.out.println("Не получилось сделать апдейт подзадачи. Для того, чтобы сделать апдейт – " +
                    "передайте подзадачу с правильным Id");
        }
    }

    @Override
    public List<Task> getHistory() { // Метод, который возвращает лист истории просмотренных задач из класса InMemoryHistoryManager()
        InMemoryHistoryManager manager = (InMemoryHistoryManager) historyManager;
        return manager.getHistory();
    }

    /* Это метод, в котором проверяются статусы подзадач – для решения о статусе Эпика,
       связанного с этими подзадачами. В метод передаётся Id эпика иди подзадачи.

       Вызывается из updateEpic и из updateSubtask. Сделал так для того, чтобы обновление
       Епика к подзадачам менялось как при обновлении Эпика, так и при обновлении
       подзадачи.

       !!! Этот метод я не добавил в Интерфейс, потому что в таком случае придётся
       убирать модификатор private. Но у пользователя не должно быть прямого доступа
       к этому методу. Такая же история с методами ниже ↓ */
    private void updateEpicWithSubtask(int idNumber) {
        int epicId = 0;

        if (epics.containsKey(idNumber)) { // Проверка на то, что передан Id епик-задачи
            epicId = idNumber;
        }

        if (subTasks.containsKey(idNumber)) { // Проверка на то, что передан Id подзадачи.
            epicId = subTasks.get(idNumber).getEpicIdForSubtask();
        }

        ArrayList<Integer> subtaskIdForEpic = epics.get(epicId).getSubtaskIdForEpic();
        ArrayList<TaskStatus> subtaskStatus = new ArrayList<>(); // Изменил тип на Enum – TaskStatus

        for (int subTaskId : subtaskIdForEpic) {
            subtaskStatus.add(subTasks.get(subTaskId).getStatus());
        }

        if ((subtaskStatus.contains(TaskStatus.NEW)) && (subtaskStatus.contains(TaskStatus.DONE))) {
            epics.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
        } else if (subtaskStatus.contains(TaskStatus.DONE)) {
            epics.get(epicId).setStatus(TaskStatus.DONE);
        } else {
            epics.get(epicId).setStatus(TaskStatus.NEW);
        }
    }

    private void add(Task task) { // Метод, который добавляет просмотренную задачу в лист истории класса
        historyManager.add(task); // InMemoryHistoryManager(). Private, чтобы случайно не надобавлять сюда задач из Main.
        InMemoryHistoryManager manager = (InMemoryHistoryManager) historyManager;
        manager.checkSizeLastTenTasks();
    }

    private boolean checkTaskForNull(Task task) {
        return (task != null);
    }
}





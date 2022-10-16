package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int taskId;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory(); // Возвращает новый объект типа HistoryManager.

    @Override
    public Integer createTask(Task task) { // Присваивает простой задаче Id и добавляет её в Map
        if (task == null) {                // и возвращает Id простой задачи.
            return null;
        }

        task.setId(++taskId);
        tasks.put(taskId, task);
        return taskId;
    }

    @Override
    public Integer createEpic(Epic epic) { // Присваивает Эпик-задаче Id и добавляет её в Map
        if (epic == null) {                // и возвращает Id эпик задачи.
            return null;
        }
        epic.setId(++taskId);
        epics.put(taskId, epic);
        return taskId;
    }

    @Override
    public Integer createSubtask(Epic epic, Subtask subtask) { // Присваивает подзадаче Id, добавляет её в Map
        if (epic == null || subtask == null) {                 // и возвращает Id подзадачи.
            return null;
        }

        if (epic.getId() == 0) { // Если у объекта Epic – id = 0, значит, что он ещё не был идентифицирован менеджером.
            createEpic(epic);    // Поэтому отправляем объект для идентификации и добавления в Map.
        }

        subtask.setId(++taskId);

        epic.setSubtasksIdForEpic(taskId);
        subtask.setEpicIdForSubtask(epic.getId());

        subTasks.put(taskId, subtask);
        return taskId;
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubTasks() {
        return subTasks;
    }

    @Override
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public void setTasksMap(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void setEpicsMap(Task task) {
        epics.put(task.getId(), (Epic) task);
    }

    @Override
    public void setSubtasksMap(Task task) {
        subTasks.put(task.getId(), (Subtask) task);
    }

    @Override
    public List<Task> getTasksList() { // Возвращает список Tasks.Task-объектов из Map.
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicsList() { // Возвращает список Tasks.Epic-объектов из Map
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubTasksList() { // Возвращает список подзадач-объектов из Map.
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Subtask> getSubtaskForEpic(Epic epic) { // Возвращает список подзадач-объектов определенного эпика.
        if (epic == null) {
            return null;
        }

        List<Subtask> subtasks = new ArrayList<>();

        for (int keySt : subTasks.keySet()) {
            if (epic.getSubtaskIdForEpic().contains(keySt)) {
                subtasks.add(subTasks.get(keySt));
            }
        }
        return subtasks;
    }

    /* Метод удаляет задачи из всех Map и из истории просмотров.
    Так как удаляются абсолютно все задачи, то эффективнее будет сразу же удалить всю историю из HistoryManager,
    чем отправлять на удаление поочередно id задач. Специально для этой цели я создал в HistoryManager
    метод clearHistory(), который очищает историю и обнуляет все ссылки на объекты – без входных данных.
     */
    @Override
    public void clearAllTasks() { //
        historyManager.clearHistory();

        /* Здесь методы clearTasks(), clearEpics() и clearSubtasks() вызывать не стал,
        потому что они будут итерировать по всем существующим задачам, что замедлит
        работу программы.
         */
        tasks.clear();
        epics.clear();
        subTasks.clear();

        System.out.println("Все списки задач очищены, и у вас больше нет ни одной задачи. " +
                "Наконец-то настал тот день, когда Вы сможете совсем ничего не делать :)");
    }

    @Override
    public void clearTasks() { // Удаляет простые задачи.
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);  // Передаёт id задач в HistoryManager для удаления.
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() { // Удаляет Епик-задачи и подзадачи, так как подзадачи
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);  // Передаёт id эпик задач в HistoryManager для удаления.
            for (Integer subtaskId : epics.get(epicId).getSubtaskIdForEpic()) {
                historyManager.remove(subtaskId);  // Передаёт id подзадач в HistoryManager для удаления.
            }
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void clearSubtasks() { // Удаляет подзадачи и связь подзадач с Эпиками – Id подзадач в объектах Эпик.
        for (Integer subtaskId : subTasks.keySet()) {
            historyManager.remove(subtaskId);  // Передаёт id подзадач в HistoryManager для удаления.
        }
        subTasks.clear();

        for (int keyEpic : epics.keySet()) {
            epics.get(keyEpic).clearStIdForEpic(); // Удаление id подзадач из всех эпиков.
        }
    }

    @Override
    public Task getTaskById(int idTask) { // Возвращает простую задачу по Id и отправляет эту задачу в метод, который
        if (tasks.get(idTask) == null) {  // добавляет её в исторический список задач.
            return null;
        }
        add(tasks.get(idTask)); // Добавление простой задачи в исторический список.
        return tasks.get(idTask);
    }

    @Override
    public Epic getEpicById(int idEpic) { // Возвращает Епик-задачу по Id и отправляет эту задачу в метод, который
        if (epics.get(idEpic) == null) {  // добавляет её в исторический список задач.
            return null;
        }
        add(epics.get(idEpic)); // Добавление эпик задачи в исторический список.
        return epics.get(idEpic);
    }

    @Override
    public Subtask getSubtaskById(int idSubtask) { // Возвращает подзадачу по Id и отправляет эту задачу в метод, который
        if (subTasks.get(idSubtask) == null) {     // добавляет её в исторический список задач.
            return null;
        }
        add(subTasks.get(idSubtask)); // Добавление подзадачи в исторический список.
        return subTasks.get(idSubtask);
    }

    @Override
    public void deleteTaskById(int removeTask) { // Удаление задачи по идентификатору.
        if (tasks.get(removeTask) == null) {
            System.out.println("Простой задачи с идентификатором " + removeTask + " нет :(");
        } else {
            historyManager.remove(removeTask); // Удаление простой задачи из истории просмотров.
            tasks.remove(removeTask);
            System.out.println("Простая задача с идентификатором " + removeTask + " удалена.");
        }
    }

    @Override
    public void deleteEpicById(int removeEpic) { // Удаление Епик-задачи по идентификатору. Теперь void.
        if (epics.get(removeEpic) == null) {
            System.out.println("Эпик-задачи с идентификатором " + removeEpic + " нет :(");
        } else {
            // Перед удалением эпика из epics и из истории просмотров, удаляю подзадачи этого эпика
            // из subtask и истории просмотров. Для этого достаю из эпика id подзадач
            // и отправляю их по очереди в deleteSubtaskById(int removeSubtask)
            List<Integer> subtaskIdForEpic = new ArrayList<>(epics.get(removeEpic).getSubtaskIdForEpic());
            for (Integer id : subtaskIdForEpic) {
                deleteSubtaskById(id);
            }

            historyManager.remove(removeEpic); // Удаление эпик из истории просмотров.
            epics.remove(removeEpic);
            System.out.println("Эпик-задача с идентификатором " + removeEpic + " удалена.");
        }
    }

    @Override
    public void deleteSubtaskById(int removeSubtask) { // Удаление подзадачи по идентификатору. Теперь void.
        if (subTasks.get(removeSubtask) == null) {
            System.out.println("Задачи с идентификатором " + removeSubtask + " нет :(");
        } else {
         /* Перед удалением подзадачи удаляю связь подзадачи с эпиком.
            Так как связь лежит в ArrayList объекта эпик в виде id подзадачи,
            удаляю её по заведомо известному значению ↓ */

            int idEpic = subTasks.get(removeSubtask).getEpicIdForSubtask();
            epics.get(idEpic).removeStIdForEpic(removeSubtask);

            historyManager.remove(removeSubtask); // Удаление подзадачи из истории просмотров.
            subTasks.remove(removeSubtask);

            System.out.println("Подзадача с идентификатором " + removeSubtask + " удалена.");

            updateEpicWithSubtask(idEpic); // После удаления подзадачи происходит обновление эпика.
        }
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
       подзадачи. */
    private void updateEpicWithSubtask(int idNumber) {
        int epicId = 0;

        if (epics.containsKey(idNumber)) { // Проверка на то, что передан Id епик-задачи
            epicId = idNumber;
        }

        if (subTasks.containsKey(idNumber)) { // Проверка на то, что передан Id подзадачи.
            epicId = subTasks.get(idNumber).getEpicIdForSubtask();
        }

        List<Integer> subtaskIdForEpic = epics.get(epicId).getSubtaskIdForEpic();

        int check = 0;
        for (int subTaskId : subtaskIdForEpic) {
            if (subTasks.get(subTaskId).getStatus().equals(TaskStatus.NEW)) {
                check++;
            }
        }

        if (check == subtaskIdForEpic.size()) {
            epics.get(epicId).setStatus(TaskStatus.NEW);
        } else if (check == 0) {
            epics.get(epicId).setStatus(TaskStatus.DONE);
        } else {
            epics.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void add(Task task) { // Метод, который добавляет просмотренную задачу в лист истории класса
        historyManager.add(task); // InMemoryHistoryManager(). Private, чтобы случайно не надобавлять сюда задач из Main.
    }

    private boolean checkTaskForNull(Task task) {
        return (task != null);
    }
}





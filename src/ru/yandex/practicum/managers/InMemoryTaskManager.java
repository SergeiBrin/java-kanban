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

    // Все проинициализировал сразу. Конструктор убрал.
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
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

    /* В этом методе я не могу убрать параметр типа Epic, потому что здесь – помимо создания subtask –
    происходит привязка subtask к epic. Если все-таки отсюда убрать epic, то d данной реализации программы
    придётся создавать отдельный метод, который потом будет связывать epic c subtask.
    А если subtask уже было создано 20 штук, то придется 20 раз вызывать этот метод, либо перегружать его на
    количество входных параметров.

    Метод createSubtask(Epic epic, Subtask subtask) работает так: на вход приходят 2 объекта – новый объект subtask,
    у которого еще нет собственного Id, и уже проидентифицированный менеджером объект epic,
    с которым требуется связать этот subtask.
    Далее subtask-у присваиваивается id, который передается в epic, а id epic-а передается в этот subtask.
    Далее subtask ложится в Map. В целом я пытался воспроизвести историю, как в реальной жизни. В которой подзадачи
    не придумываются раньше основных задач.

    Также в этом методе я предусмотрел ситуацию, когда в него передали epic, который так же не был идентифицирован.
    Если это так, то сначала на идентификацию отправляется epic, а потом происходит всё остальное.
     */
    @Override
    public Integer createSubtask(Epic epic, Subtask subtask) { // Присваивает подзадаче Id, добавляет её в Map
        if (epic == null || subtask == null) {                 // и возвращает Id подзадачи.
            return null;
        }

        if (epic.getId() == 0) { // Если у объекта Tasks.Epic id = 0, значит, что он ещё не был идентифицирован менеджером.
            createEpic(epic);    // Поэтому отправляем объект для идентификации и добавления в Map.
        }

        subtask.setId(++taskId);

        epic.setSubtasksIdForEpic(taskId);
        subtask.setEpicIdForSubtask(epic.getId());

        subTasks.put(taskId, subtask);
        return taskId;
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

    @Override
    public void clearAllTasks() { // Удаляет задачи из всех Map.
        clearTasks();
        clearEpics();
        System.out.println("Все списки задач очищены, и у вас больше нет ни одной задачи. " +
                "Наконец-то настал тот день, когда Вы сможете полежать :)");
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
        if (tasks.get(idTask) == null) {  // добавляет её в исторический список задач.
            return null;
        }
        add(tasks.get(idTask));
        return tasks.get(idTask);
    }

    @Override
    public Epic getEpicById(int idEpic) { // Возвращает Епик-задачу по Id и отправляет эту задачу в метод, который
        if (epics.get(idEpic) == null) {  // добавляет её в исторический список задач.
            return null;
        }
        add(epics.get(idEpic));
        return epics.get(idEpic);
    }

    @Override
    public Subtask getSubtaskById(int idSubtask) { // Возвращает подзадачу по Id и отправляет эту задачу в метод, который
        if (subTasks.get(idSubtask) == null) {     // добавляет её в исторический список задач.
            return null;
        }
        add(subTasks.get(idSubtask));
        return subTasks.get(idSubtask);
    }

    @Override
    public void deleteTaskById(int removeTask) { // Удаление задачи по идентификатору. Теперь void.
        if (tasks.get(removeTask) == null) {
            System.out.println("Задачи с идентификатором " + removeTask + " нет :(");
        } else {
            tasks.remove(removeTask);
            System.out.println("Задача с идентификатором " + removeTask + " удалена.");
        }
    }

    @Override
    public void deleteEpicById(int removeEpic) { // Удаление Епик-задачи по идентификатору. Теперь void.
        if (epics.get(removeEpic) == null) {
            System.out.println("Задачи с идентификатором " + removeEpic + " нет :(");
        } else {
            epics.remove(removeEpic);
            System.out.println("Задача с идентификатором " + removeEpic + " удалена.");
        }
    }

    @Override
    public void deleteSubtaskById(int removeSubtask) { // Удаление подзадачи по идентификатору. Теперь void.
        if (subTasks.get(removeSubtask) == null) {
            System.out.println("Задачи с идентификатором " + removeSubtask + " нет :(");
        } else {
         /* Перед удалением подзадачи удаляю связь подзадачи с эпиком.
            Так как связь лежит в ArrayList объекта эпик в виде id подзадачи,
            удаляю её по заведомо известному значению ↓

            Подумал и добавил на суд упрощенную реализацию удаления. Но пока её закомментировал
            Просто хочу понять, так лучше, или лучше так не делать, что избежать Null Pointer Exception */

//          int idEpic = subTasks.get(removeSubtask).getEpicIdForSubtask();
//          epics.get(idEpic).removeStIdForEpic(removeSubtask);

            for (int keyEpic : epics.keySet()) {
                boolean isTrue = epics.get(keyEpic).getSubtaskIdForEpic().contains(removeSubtask);
                if (isTrue) {
                    epics.get(keyEpic).removeStIdForEpic(removeSubtask);
                }
            }

            subTasks.remove(removeSubtask);
            System.out.println("Подзадача с идентификатором " + removeSubtask + " удалена.");
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
    }

    private boolean checkTaskForNull(Task task) {
        return (task != null);
    }
}





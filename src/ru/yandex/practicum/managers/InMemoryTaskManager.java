package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.enums.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subTasks = new HashMap<>();

    // Сделал HashSet, чтобы не сортировать список каждый раз. Далее, в методе getPrioritizedTasks() буду перекладывать
    // HashSet через addAll() в TreeSet, где список сразу же буден отсортирован.
    private final Set<Task> prioritizedTask = new HashSet<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory(); // Возвращает новый объект типа HistoryManager.
    private int taskId;

    // Присваивает простой задаче Id и добавляет её в Map
    // и возвращает Id простой задачи.
    @Override
    public Integer createTask(Task task) {
        if (task == null) {
            return null;
        }

        task.setId(++taskId);
        tasks.put(taskId, task);
        prioritizedTask.add(task); // New! Добавление задачи HashSet
        return taskId;
    }

    @Override
    public Integer createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(++taskId);
        epics.put(taskId, epic);
        return taskId;
    }

    @Override
    public Integer createSubtask(Epic epic, Subtask subtask) {
        if (epic == null || subtask == null) {
            return null;
        }

        // Если у объекта Epic – id = 0, значит, что он ещё не был идентифицирован менеджером.
        // Поэтому отправляем объект для идентификации и добавления в Map.
        if (epic.getId() == 0) {
            createEpic(epic);
        }

        subtask.setId(++taskId);

        epic.setSubtasksIdForEpic(taskId);
        subtask.setEpicIdForSubtask(epic.getId());

        subTasks.put(taskId, subtask);
        prioritizedTask.add(subtask); // New! Добавление задачи HashSet

        calculateEpicTime(epic, subtask); // Расчет времени Эпика, относительно его подзадачи

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
    public int getTaskId() {
        return taskId;
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

    public void setPrioritizedTask(Task task) {
        prioritizedTask.add(task);
    }

    @Override
    public List<Task> getTasksList() { // Возвращает список Tasks.Task-объектов из Map.
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubTasksList() { // Возвращает список подзадач-объектов из Map.
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Subtask> getSubtaskForEpic(Epic epic) {
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

    // Метод удаляет задачи из всех Map и из истории просмотров.
    // Так как удаляются абсолютно все задачи, то эффективнее будет сразу же удалить всю историю из HistoryManager,
    // чем отправлять на удаление поочередно id задач. Специально для этой цели я создал в HistoryManager
    // метод clearHistory(), который очищает историю и обнуляет все ссылки на объекты – без входных данных.
    @Override
    public void clearAllTasks() {
        prioritizedTask.clear(); // new!!! Удаление задач из HashSet
        historyManager.clearHistory();

        tasks.clear();
        epics.clear();
        subTasks.clear();

        System.out.println("Все списки задач очищены.");
    }

    @Override
    public void clearTasks() { // Удаляет простые задачи.
        for (Integer taskId : tasks.keySet()) {
            prioritizedTask.removeIf(task -> task.getId() == taskId); // New!!! Удаление из HashSet по id.

            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    // Удаляет Епик задачи и их подзадачи
    @Override
    public void clearEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
            for (Integer subtaskId : epics.get(epicId).getSubtaskIdForEpic()) {
                // New! При удалении всех эпиков удаляем все подзадачи
                // из prioritizedTaskByTime.
                prioritizedTask.removeIf(task -> task.getId() == subtaskId);

                historyManager.remove(subtaskId);
            }
        }
        epics.clear();
        subTasks.clear();
    }

    // Удаляет подзадачи и связь подзадач с Эпиками.
    @Override
    public void clearSubtasks() {
        for (Integer subtaskId : subTasks.keySet()) {
            prioritizedTask.removeIf(subTask -> subTask.getId() == subtaskId); //  New! Удаление из HashSet по id

            historyManager.remove(subtaskId);
        }
        subTasks.clear();

        for (int keyEpic : epics.keySet()) {
            epics.get(keyEpic).clearStIdForEpic(); // Удаление id подзадач из всех эпиков.
        }
    }

    // Возвращает простую задачу по Id и отправляет эту задачу в метод,
    // который добавляет её в исторический список задач.
    @Override
    public Task getTaskById(int idTask) {
        if (tasks.get(idTask) == null) {
            return null;
        }
        add(tasks.get(idTask)); // Добавление простой задачи в исторический список.
        return tasks.get(idTask);
    }

    // Возвращает Епик задачу по Id и отправляет эту задачу в метод,
    // который добавляет её в исторический список задач.
    @Override
    public Epic getEpicById(int idEpic) {
        if (epics.get(idEpic) == null) {
            return null;
        }
        add(epics.get(idEpic)); // Добавление эпик задачи в исторический список.
        return epics.get(idEpic);
    }

    // Возвращает подзадачу по Id и отправляет эту задачу в метод,
    // который добавляет её в исторический список задач.
    @Override
    public Subtask getSubtaskById(int idSubtask) {
        if (subTasks.get(idSubtask) == null) {
            return null;
        }
        add(subTasks.get(idSubtask)); // Добавление подзадачи в исторический список.
        return subTasks.get(idSubtask);
    }

    // Удаление задачи по идентификатору.
    @Override
    public void deleteTaskById(int removeTask) {
        if (tasks.get(removeTask) == null) {
            System.out.println("Простой задачи id " + removeTask + " нет :(");
        } else {
            prioritizedTask.removeIf(task -> task.getId() == removeTask); // Удаление задачи из HashSet по id.

            historyManager.remove(removeTask);
            tasks.remove(removeTask);

            System.out.println("Простая задача id " + removeTask + " удалена.");
        }
    }

    @Override
    public void deleteEpicById(int removeEpic) { // Удаление Епик-задачи по идентификатору. Теперь void.
        if (epics.get(removeEpic) == null) {
            System.out.println("Эпик-задачи id " + removeEpic + " нет :(");
        } else {
            // Перед удалением эпика из epics и из истории просмотров, удаляю подзадачи этого эпика
            // из subtask и истории просмотров.
            List<Integer> subtaskIdForEpic = new ArrayList<>(epics.get(removeEpic).getSubtaskIdForEpic());
            for (Integer subtaskId : subtaskIdForEpic) {
                // New! При удалении Эпика удаляем его подзадачи
                // из prioritizedTaskByTime ↓
                prioritizedTask.removeIf(task -> task.getId() == subtaskId);

                deleteSubtaskById(subtaskId);
            }

            historyManager.remove(removeEpic);
            epics.remove(removeEpic);
            System.out.println("Эпик-задача id " + removeEpic + " удалена.");
        }
    }

    // Удаление подзадачи по идентификатору.
    @Override
    public void deleteSubtaskById(int removeSubtask) {
        if (subTasks.get(removeSubtask) == null) {
            System.out.println("Задачи id " + removeSubtask + " нет :(");
        } else {
            // Перед удалением подзадачи удаляю связь подзадачи с эпиком.
            // Так как связь лежит в ArrayList объекта эпик в виде id подзадачи,
            // удаляю её по заведомо известному значению ↓
            int epicId = subTasks.get(removeSubtask).getEpicIdForSubtask();
            epics.get(epicId).removeStIdForEpic(removeSubtask);
            subTasks.get(removeSubtask).setEpicIdForSubtask(0);

            prioritizedTask.removeIf(subTask -> subTask.getId() == removeSubtask); // New! Удаление Subtask из HashSet по id.

            historyManager.remove(removeSubtask);
            subTasks.remove(removeSubtask);

            System.out.println("Подзадача id " + removeSubtask + " удалена.");

            updateEpicWithSubtask(epicId); // После удаления подзадачи происходит обновление эпика.
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

    // Обновление простых задач и их статусов.
    @Override
    public void updateTask(Task newTask) {
        boolean isNotNull = checkTaskForNull(newTask);

        if (isNotNull) {
            boolean isTaskIdTrue = tasks.containsKey(newTask.getId());
            if (isTaskIdTrue) {
                tasks.put(newTask.getId(), newTask);
            }
        } else {
            System.out.println("Не получилось сделать апдейт простой задачи. Для того, чтобы сделать апдейт – " +
                    "передайте простую задачу с правильным Id");
        }
    }

    // Обновление Эпик-задач и их статусов.
    @Override
    public void updateEpic(Epic newEpic) {
        boolean isNotNull = checkTaskForNull(newEpic);

        if (isNotNull) {
            boolean isEpicIdTrue = epics.containsKey(newEpic.getId());
            if (isEpicIdTrue) {
                epics.put(newEpic.getId(), newEpic);
                updateEpicWithSubtask(newEpic.getId());

                // Проверка Эпиков на наличие подзадач. Если их нет, то Эпик – NEW.
                for (int keyEpic : epics.keySet()) {
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
        boolean isNotNull = checkTaskForNull(newSubtask);

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
    public List<Task> getHistory() {
        InMemoryHistoryManager manager = (InMemoryHistoryManager) historyManager;
        return manager.getHistory();
    }

    // Метод ищет пересечения, а потом возвращает отсортированный по времени список.
    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> prioritizedTasksByTime = new TreeSet<>(prioritizedTask);
        List<Task> taskIntersection = new ArrayList<>(prioritizedTasksByTime);

        for (int i = 0; i < taskIntersection.size(); i++) {
            Task task1 = taskIntersection.get(i);
            if (task1.getStartTime() == null) {
                break;
            }
            for (int j = i + 1; j < taskIntersection.size(); j++) {
                Task task2 = taskIntersection.get(j);
                if (task2.getStartTime() == null) {
                    break;
                }

                boolean isIntersection = task1.getEndTime().isAfter(task2.getStartTime());
                if ((isIntersection)) {
                    System.out.println("Задачи: " + task1.getTaskName() + " и " + task2.getTaskName() + " пересекаются.");
                }
            }
        }
        return prioritizedTasksByTime;
    }

    // Здесь обновляются статусы Эпика. Также отсюда задачи отправляются в calculateEpicTime()
    // для расчета времени Эпика относительно его подзадач.
    private void updateEpicWithSubtask(int idNumber) {
        int epicId = 0;
        final List<Integer> subtaskIdForEpic;

        if (epics.containsKey(idNumber)) { // Проверка на то, что передан Id Епик задачи
            epicId = idNumber;
        }

        if (subTasks.containsKey(idNumber)) { // Проверка на то, что передан Id подзадачи.
            epicId = subTasks.get(idNumber).getEpicIdForSubtask();
        }

        Epic epic = epics.get(epicId);

        // Перед расчетом времени эпика – относительно его подзадач обнуляю его время.
        // Если этого не сделать, то время Эпика может рассчитаться неправильно.
        epic.setStartTime(null);
        epic.setEndTime(null);

        subtaskIdForEpic = epics.get(epicId).getSubtaskIdForEpic();

        int checkNew = 0;
        int checkDone = 0;
        for (int subTaskId : subtaskIdForEpic) {
            if (subTasks.get(subTaskId).getStatus().equals(TaskStatus.NEW)) {
                checkNew++;
            } else if (subTasks.get(subTaskId).getStatus().equals(TaskStatus.DONE)) {
                checkDone++;
            }
            calculateEpicTime(epic, subTasks.get(subTaskId)); // New! Расчёт времени Эпика – после апдейта Epic или Subtask.
        }

        if (checkNew == subtaskIdForEpic.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (checkDone == subtaskIdForEpic.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    // Mew!!! При создании Subtask, рассчитать startTime и endTime для Эпика.
    private void calculateEpicTime(Epic epic, Subtask subtask) {
        List<Integer> subtaskIdForEpic = epic.getSubtaskIdForEpic();
        boolean isSizeZero = subtaskIdForEpic.isEmpty();
        // todo
        boolean isSizeOne = subtaskIdForEpic.size() == 1; // New. Проверка на то, что у Эпика только один Subtask.

        if (isSizeZero) {
            return;
        }

        // Если Эпик Null, а Subtask не Null, то присваиваем Эпику время Subtask
        if (epic.getStartTime() == null) {
            if (subtask.getStartTime() != null) {
                epic.setStartTime(subtask.getStartTime()); //
                epic.setEndTime(subtask.getEndTime());
            }
            // Если Эпик не Null и Subtask не Null, то сравниваем startTime и endTime
            // Эпика и Subtask. При необходимости переназначаем Время Эпика.
        } else if (subtask.getStartTime() != null) {
            // todo
            if (isSizeOne) {                               // New. Если у Эпика это первый Subtask, то его время должно
                epic.setStartTime(subtask.getStartTime()); // быть сразу же назначено Эпику. Иначе время самого Эпика может
                epic.setEndTime(subtask.getEndTime());     // оказаться раньше и/или позже времени Subtask - и не переназначится.
            } else {
                boolean startTime = subtask.getStartTime().isBefore(epic.getStartTime());
                boolean endTime = subtask.getEndTime().isAfter(epic.getEndTime());

                if (startTime) {
                    epic.setStartTime(subtask.getStartTime());
                }

                if (endTime) {
                    epic.setEndTime(subtask.getEndTime());

                }
            }
        }
    }


    private void add(Task task) { // Метод, который добавляет просмотренную задачу в лист истории.
        historyManager.add(task);
    }

    private boolean checkTaskForNull(Task task) {
        return (task != null);
    }
}





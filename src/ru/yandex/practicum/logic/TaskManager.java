package ru.yandex.practicum.logic;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskId;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subTasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public void createTask(Task task) { // Присваивает простой задаче Id и добавляет её в Map.
        if (task == null) {
            return;
        }

        task.setId(++taskId);
        tasks.put(taskId, task);
    }

    public void createEpic(Epic epic) { // Присваивает Эпик-задаче Id и добавляет её в Map.
        if (epic == null) {
            return;
        }

        epic.setId(++taskId);
        epics.put(taskId, epic);
    }

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

    public ArrayList<Task> getTasksList() { // Возвращает список Tasks.Task-объектов из Map.
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicsList() { // Возвращает список Tasks.Epic-объектов из Map
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubTasksList() { // Возвращает список подзадач-объектов из Map.
        return new ArrayList<>(subTasks.values());
    }

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

    public String clearAllTasks() { // Удаляет задачи из всех Map.
        clearTasks();
        clearEpics();
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

    public Task getTaskById(int idTask) { // Возвращает простую задачу по Id. Если такой задачи нет, возвращает Null.
        if (tasks.get(idTask) != null) {
            return tasks.get(idTask);
        }
        return null;
    }

    public Epic getEpicById(int idEpic) { // Возвращает Епик-задачу по Id. Если такой задачи нет, возвращает Null.
        if (epics.get(idEpic) != null) {
            return epics.get(idEpic);
        }
        return null;
    }

    public Subtask getSubtaskById(int idSubtask) { // Возвращает подзадачу по Id. Если такой задачи нет, возвращает Null.
        if (tasks.get(idSubtask) != null) {
            return subTasks.get(idSubtask);
        }
        return null;
    }

    public String deleteTaskById(int removeTask) { // Удаление задачи по идентификатору.
        if (tasks.get(removeTask) != null) {
            tasks.remove(removeTask);
            return "Задача с идентификатором " + removeTask + " удалена.";
        }
        return "Задачи с идентификатором " + removeTask + " нет :(";
    }

    public String deleteEpicById(int removeEpic) { // Удаление Епик-задачи по идентификатору.
        if (epics.get(removeEpic) != null) {
            epics.remove(removeEpic);
            return "Задача с идентификатором " + removeEpic + " удалена.";
        }
        return "Задачи с идентификатором " + removeEpic + " нет :(";
    }

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

    public void printTaskList() { // Выводит список простых задач в консоль.
        for (Integer key : tasks.keySet()) {
            Task task = tasks.get(key);
            System.out.println(task);
        }
    }

    public void printEpicList() { // Выводит список эпик-задач в консоль.
        for (Integer key : epics.keySet()) {
            Epic epic = epics.get(key);
            System.out.println(epic);
        }
    }

    public void printSubtaskList() { // Выводит список подзадач в консоль.
        for (Integer key : subTasks.keySet()) {
            Subtask subtask = subTasks.get(key);
            System.out.println(subtask);
        }
    }

    public void updateTask(Task newTask) { // Обновление простых задач и их статусов.
        boolean isTrue = checkTaskForNullOrEmpty(newTask);
        if (isTrue) {
            for (int keyTask : tasks.keySet()) {
                if (newTask.getId() == keyTask) {
                    tasks.put(keyTask, newTask);
                }
            }
        }
    }

    /* Тут кроме апдейта Епик в Map происходит проверка эпиков на наличие у них подзадач.
    Если окажется, что у какого-то из эпиков их нет, например, потому что их удалили через метод
    Tasks.Task getTaskById(int idTask), то статус Эпика должен измениться на NEW,
    так как по условию задачи Эпик без подзадач - это NEW */
    public void updateEpic(Epic newEpic) { // Обновление Эпик-задач и их статусов.
        boolean isTrue = checkTaskForNullOrEmpty(newEpic);

        if (isTrue) {
            for (int keyEpic : epics.keySet()) {
                if (newEpic.getId() == keyEpic) {
                    epics.put(keyEpic, newEpic);
                }
                boolean isEmpty = epics.get(keyEpic).getSubtaskIdForEpic().isEmpty();
                if (isEmpty) {
                    epics.get(keyEpic).setStatus("NEW");
                }
            }
            updateEpicWithSubtask();
        }
    }

    public void updateSubtask(Subtask newSubtask) { // Обновление подзадач и их статусов.
        boolean isTrue = checkTaskForNullOrEmpty(newSubtask);

        if (isTrue) {
            for (int keySt : subTasks.keySet()) {
                if (newSubtask.getId() == keySt) {
                    subTasks.put(keySt, newSubtask);
                }
            }
            updateEpicWithSubtask();
        }
    }

    /* Это метод, в котором проверяются статусы подзадач – для решения о статусе Эпика,
       связанного с этими подзадачами. Вызывается из updateEpic и из updateSubtask. Сделал так
       для того, чтобы обновление Епика к подзадачам менялось как при обновлении Эпика, так и при обновлении
       подзадачи. */
    private void updateEpicWithSubtask() {
        for (int keySt : subTasks.keySet()) {
            int epicId = subTasks.get(keySt).getEpicIdForSubtask();

            ArrayList<Integer> subtaskIdForEpic = epics.get(epicId).getSubtaskIdForEpic();
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

    private boolean checkTaskForNullOrEmpty(Task task) {
        return (task != null) && (task.getId() != 0);
    }
}





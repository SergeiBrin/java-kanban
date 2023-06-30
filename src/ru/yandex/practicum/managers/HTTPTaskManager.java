package ru.yandex.practicum.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.http.client.KVTaskClient;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.utils.GsonBuilders;

import java.lang.reflect.Type;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private static final Gson gson = GsonBuilders.getGson();
    private static KVTaskClient kvTaskClient;

    public HTTPTaskManager(String url) {
        super(null);
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save() {
        List<Task> saveTasks = getTasksList();
        List<Epic> saveEpics = getEpicsList();
        List<Subtask> saveSubtasks = getSubTasksList();
        List<Task> saveHistory = getHistory();

        String jsonTask = gson.toJson(saveTasks);
        kvTaskClient.put("task", jsonTask);

        String jsonEpic = gson.toJson(saveEpics);
        kvTaskClient.put("epic", jsonEpic);

        String jsonSubtask = gson.toJson(saveSubtasks);
        kvTaskClient.put("subtask", jsonSubtask);

        String jsonHistory = gson.toJson(saveHistory);
        kvTaskClient.put("history", jsonHistory);
    }

    // Этот метод создаст новый менеджер и загрузит данные с сервера.
    public static HTTPTaskManager load() {
        HTTPTaskManager httpTaskManager = new HTTPTaskManager("http://localhost:8078");

        String loadTasks = kvTaskClient.load("task");
        String loadEpics = kvTaskClient.load("epic");
        String loadSubtasks = kvTaskClient.load("subtask");
        String loadHistory = kvTaskClient.load("history");

        Type typeTask = new TypeToken<List<Task>>() {
        }.getType();
        Type typeEpic = new TypeToken<List<Epic>>() {
        }.getType();
        Type typeSubtask = new TypeToken<List<Subtask>>() {
        }.getType();

        List<Task> tasks = gson.fromJson(loadTasks, typeTask);
        List<Epic> epics = gson.fromJson(loadEpics, typeEpic);
        List<Subtask> subtasks = gson.fromJson(loadSubtasks, typeSubtask);
        List<Task> history = gson.fromJson(loadHistory, typeTask);

        if (tasks != null) {
            putTasksInManager("tasks", tasks, httpTaskManager);
        }

        if (epics != null) {
            putTasksInManager("epics", epics, httpTaskManager);
        }

        if (subtasks != null) {
            putTasksInManager("subtasks", subtasks, httpTaskManager);
        }

        if (history != null) {
            putTasksInManager("history", history, httpTaskManager);
        }

        return httpTaskManager;
    }

    private static void putTasksInManager(String listType,
                                          List<? extends Task> tasks,
                                          HTTPTaskManager httpTaskManager) {

        int managerId = httpTaskManager.getTaskId();

        for (Task task : tasks) {
            int taskId = task.getId();

            if (managerId < taskId) {
               httpTaskManager.setTaskId(taskId);
            }

            switch (listType) {
                case "tasks":
                    httpTaskManager.setTasksMap(task);
                    httpTaskManager.setPrioritizedTask(task);
                    break;
                case "epics":
                    httpTaskManager.setEpicsMap(task);
                    break;
                case "subtasks":
                    httpTaskManager.setSubtasksMap(task);
                    httpTaskManager.setPrioritizedTask(task);
                    break;
                case "history":
                    Task checkTask = httpTaskManager.getTaskById(taskId);
                    Epic checkEpic = httpTaskManager.getEpicById(taskId);
                    Subtask checkSubtask = httpTaskManager.getSubtaskById(taskId);

                    if (checkTask != null) {
                        httpTaskManager.getTaskById(taskId);
                    } else if (checkEpic != null) {
                        httpTaskManager.getEpicById(taskId);
                    } else if (checkSubtask != null) {
                        httpTaskManager.getSubtaskById(taskId);
                    }
                    break;
            }
        }
    }
}

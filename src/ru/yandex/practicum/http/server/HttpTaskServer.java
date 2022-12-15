package ru.yandex.practicum.http.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.managers.HTTPTaskManager;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.utils.GsonBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

public class HttpTaskServer {
    private final static int PORT = 8080;
    private final HttpServer httpServer;
    private URI uri;
    private TaskManager httpTaskManager; // Тут пока непонятно.
    private final Gson gson = GsonBuilders.getGson();

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    private class TasksHandler implements HttpHandler {
        // Здесь будет блок try catch

        @Override
        public void handle(HttpExchange ex) throws IOException {
            httpTaskManager = HTTPTaskManager.load(); // При каждом обращении к серверу апдейтим менеджер с KVServer.

            uri = ex.getRequestURI();
            String query = uri.getQuery();
            String url = uri.getPath();

            String method = ex.getRequestMethod();

            switch (method) {
                case "GET" -> getTasks(ex, query, url);
                case "POST" -> postTasks(ex, query, url);
                case "DELETE" -> deleteTasks(ex, query, url);
                default -> {
                    System.out.println("API трекера задач не поддерживает такой метод – " + method);
                    ex.sendResponseHeaders(400, 0);
                    ex.close();
                }
            }
        }
    }

    private void getTasks(HttpExchange ex, String query, String url) throws IOException {

        switch (url) {
            case "/tasks":
                List<Task> tasks = httpTaskManager.getAllTaskList();
                sendMessage(tasks, ex);
                break;
            case "/tasks/task":
                getProcess(ex, query, "Task");
                break;
            case "/tasks/epic":
                getProcess(ex, query, "Epic");
                break;
            case "/tasks/subtask":
                getProcess(ex, query, "Subtask");
                break;
            case "/tasks/subtask/epic":
                getProcess(ex, query, "Subtask/Epic");
                break;
            case "/tasks/history":
                List<Task> history = httpTaskManager.getHistory();
                sendMessage(history, ex);
            default:
                System.out.println("API cервера не умеет обрабатывать такой запрос " + url + query);
                ex.sendResponseHeaders(400, 0);
                ex.close();
        }
    }

    private void getProcess(HttpExchange ex, String query, String className) throws IOException {
        List<Task> tasks;
        List<Epic> epics;
        List<Subtask> subtasks;
        Task task = null;
        int id;

        if (query != null) {
            String newQuery = query.replace("id=", "");
            id = Integer.parseInt(newQuery);

            switch (className) {
                case "Task" -> task = httpTaskManager.getTaskById(id);
                case "Epic" -> task = httpTaskManager.getEpicById(id);
                case "Subtask" -> task = httpTaskManager.getSubtaskById(id);
                case "Subtask/Epic" -> {
                    Epic epic = httpTaskManager.getEpicById(id);
                    if (epic != null) {
                        subtasks = httpTaskManager.getSubtaskForEpic(epic);
                        sendMessage(subtasks, ex);
                        ex.close();
                        return;
                    } else {
                        System.out.println("Сервер не нашёл запрашиваемый Epic по id=" + id);
                        ex.sendResponseHeaders(404, 0);
                    }
                }
            }

            if (task != null) {
                sendMessage(task, ex);
            } else {
                System.out.println("Сервер не нашёл запрашиваемый Task по id=" + id);
                ex.sendResponseHeaders(404, 0);
            }

        } else {
            switch (className) {
                case "Task" -> {
                    tasks = httpTaskManager.getTasksList();
                    sendMessage(tasks, ex);
                }
                case "Epic" -> {
                    epics = httpTaskManager.getEpicsList();
                    sendMessage(epics, ex);
                }
                case "Subtask" -> {
                    subtasks = httpTaskManager.getSubTasksList();
                    sendMessage(subtasks, ex);
                }
                case "Subtask/Epic" -> {
                    System.out.println("Отсутствует id Эпика");
                    ex.sendResponseHeaders(400, 0);
                }
            }
        }
        ex.close();
    }

    private void postTasks(HttpExchange ex, String query, String url) throws IOException {
        Task task;
        Epic epic;
        Subtask subtask;
        int id;

        InputStream inputStream = ex.getRequestBody();
        String body = new String(inputStream.readAllBytes(), Charset.defaultCharset());

        if (query != null) {
            switch (url) {
                case "/tasks/task" -> {
                    task = gson.fromJson(body, Task.class);

                    String newQuery = query.replace("id=", "");
                    id = Integer.parseInt(newQuery);

                    if (httpTaskManager.getTaskById(id) != null) {
                        task.setId(id);
                        httpTaskManager.updateTask(task);

                        System.out.println(task + " обновлен");
                        sendMessage(id, ex);
                    } else {
                        System.out.println(task + " не обновлен. Передан неправильный id");
                        ex.sendResponseHeaders(400, 0);
                    }
                }
                case "/tasks/epic" -> {
                    epic = gson.fromJson(body, Epic.class);

                    String newQuery = query.replace("id=", "");
                    id = Integer.parseInt(newQuery);

                    if (httpTaskManager.getEpicById(id) != null) {
                        Epic oldEpic = httpTaskManager.getEpicById(id);
                        List<Integer> subtaskIdForOldEpic = oldEpic.getSubtaskIdForEpic();
                        List<Integer> subtaskIdForEpic = epic.getSubtaskIdForEpic();

                        if (subtaskIdForEpic == null) {
                            System.out.println(epic + " передан неправильно. Не хватает поля – \"subtasksIdForEpic\": []");
                            ex.sendResponseHeaders(400, 0);
                            return;
                        }

                        if (!subtaskIdForOldEpic.isEmpty()) {
                            epic.clearStIdForEpic();
                            for (Integer oldId : subtaskIdForOldEpic) {
                                epic.setSubtasksIdForEpic(oldId);
                            }
                        }

                        epic.setId(id);
                        httpTaskManager.updateEpic(epic);

                        System.out.println(epic + " обновлен");
                        sendMessage(id, ex);
                    } else {
                        System.out.println(epic + " не обновлен. Передан неправильный id");
                        ex.sendResponseHeaders(400, 0);
                    }
                }
                case "/tasks/subtask" -> {
                    subtask = gson.fromJson(body, Subtask.class);

                    String newQuery = query.replace("id=", "");
                    id = Integer.parseInt(newQuery);

                    if (httpTaskManager.getSubtaskById(id) != null) {
                        subtask.setId(id);
                        try {
                            httpTaskManager.updateSubtask(subtask);
                        } catch (NullPointerException e) {
                            System.out.println("Передан неправильный epicIdForSubtask.");
                            ex.sendResponseHeaders(400, 0);
                        }

                        System.out.println(subtask + " обновлен");
                        sendMessage(id, ex);
                    } else {
                        System.out.println(subtask + " не обновлен. Передан неправильный id");
                        ex.sendResponseHeaders(400, 0);
                    }
                }
                case "/tasks/subtask/epic" -> {
                    subtask = gson.fromJson(body, Subtask.class);

                    String newQuery = query.replace("id=", "");
                    id = Integer.parseInt(newQuery);

                    epic = httpTaskManager.getEpicById(id);
                    if (epic != null) {
                        int subtaskId = httpTaskManager.createSubtask(epic, subtask);

                        if (subtaskId == subtask.getId()) {
                            System.out.println(subtask + " успешно добавлен");
                            sendMessage(subtaskId, ex);
                        }

                    } else {
                        System.out.println(subtask + " не получилось добавить, так как Epic c таким id=" + id + " нет");
                        ex.sendResponseHeaders(400, 0);
                    }
                }
                default -> {
                    System.out.println("API cервера не умеет обрабатывать такой запрос " + url + query);
                    ex.sendResponseHeaders(400, 0);
                }
            }
        } else {
            switch (url) {
                case "/tasks/task" -> {
                    task = gson.fromJson(body, Task.class);
                    id = httpTaskManager.createTask(task);
                    if (id == task.getId()) {
                        System.out.println(task + " успешно добавлен");
                        sendMessage(id, ex);
                    } else {
                        System.out.println(task + " не получилось добавить");
                        ex.sendResponseHeaders(500, 0);
                    }
                }
                case "/tasks/epic" -> {
                    epic = gson.fromJson(body, Epic.class);
                    id = httpTaskManager.createEpic(epic);
                    if (id == epic.getId()) {
                        System.out.println(epic + " успешно добавлен");
                        sendMessage(id, ex);
                    } else {
                        System.out.println(epic + " не получилось добавить");
                        ex.sendResponseHeaders(500, 0);
                    }
                }
                default -> {
                    System.out.println("API cервера не умеет обрабатывать такой запрос " + url);
                    ex.sendResponseHeaders(400, 0);
                }
            }
        }
        ex.close();
    }

    private void deleteTasks(HttpExchange ex, String query, String url) throws IOException {
        if (query != null) {
            String newQuery = query.replace("id=", "");
            int id = Integer.parseInt(newQuery);

            switch (url) {
                case "/tasks/task" -> httpTaskManager.deleteTaskById(id);
                case "/tasks/epic" -> httpTaskManager.deleteEpicById(id);
                case "/tasks/subtask" -> httpTaskManager.deleteSubtaskById(id);
                default -> {
                    System.out.println("API cервера не умеет обрабатывать такой запрос " + url + query);
                    ex.sendResponseHeaders(400, 0);
                    ex.close();
                    return;
                }
            }

            // FileBackedTasksManager manager = (FileBackedTasksManager) httpTaskManager;
            if (!httpTaskManager.getIsDeleteTask()) {
                ex.sendResponseHeaders(404, 0);
            }

        } else {
            switch (url) {
                case "/tasks" -> httpTaskManager.clearAllTasks();
                case "/tasks/task" -> httpTaskManager.clearTasks();
                case "/tasks/epic" -> httpTaskManager.clearEpics();
                case "/tasks/subtask" -> httpTaskManager.clearSubtasks();
                default -> {
                    System.out.println("API cервера не умеет обрабатывать такой запрос " + url);
                    ex.sendResponseHeaders(400, 0);
                }
            }
        }
        ex.sendResponseHeaders(200, 0);
        ex.close();
    }

    private void sendMessage(Object object, HttpExchange ex) {
        String message = gson.toJson(object);

        try (OutputStream os = ex.getResponseBody()) {
            ex.sendResponseHeaders(200, message.getBytes().length);
            os.write(message.getBytes());
        } catch (IOException e1) {
            try {
                ex.sendResponseHeaders(500, 0);// Ошибка на стороне сервера.
                System.out.println("Сервер не смог отправить клиенту " + message);
                System.out.println(e1.getMessage());
            } catch (IOException e2) {
                System.out.println(e2.getMessage());
            }
        }
    }
}

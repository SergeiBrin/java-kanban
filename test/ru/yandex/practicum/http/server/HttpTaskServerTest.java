package ru.yandex.practicum.http.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managers.HTTPTaskManager;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.enums.TaskStatus;
import ru.yandex.practicum.utils.GsonBuilders;
import ru.yandex.practicum.utils.Managers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String url = "http://localhost:8080/tasks";
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final Gson gson = GsonBuilders.getGson();

    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private TaskManager httpTaskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;
    private String jsonTask;
    private String jsonEpic;
    private String jsonSubtask1;
    private String jsonSubtask2;

    @BeforeEach
    public void beforeEach() throws IOException {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        kvServer = new KVServer();
        kvServer.start();

        httpTaskManager = Managers.getDefault();

        task = new Task("Test Task name",
                "Test Task Description",
                TaskStatus.NEW,
                LocalDateTime.now(),
                Duration.ZERO);
        jsonTask = gson.toJson(task);

        epic = new Epic("Test Epic name",
                "Test Epic Description",
                TaskStatus.NEW,
                null,
                null);
        jsonEpic = gson.toJson(epic);

        subtask1 = new Subtask("Test Subtask1 name",
                "Test Subtask Description",
                TaskStatus.NEW,
                LocalDateTime.now(),
                Duration.ZERO);
        jsonSubtask1 = gson.toJson(subtask1);

        subtask2 = new Subtask("Test Subtask1 name",
                "Test Subtask Description",
                TaskStatus.NEW,
                LocalDateTime.now(),
                Duration.ofDays(10));
        jsonSubtask2 = gson.toJson(subtask1);
    }

    // Написал методы, чтобы в тестах не писать
    // постоянно эти большие конструкции.
    private HttpRequest getRequest(URI path) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(path)
                .header("Content-Type", "application/json")
                .build();
    }

    private HttpRequest postRequest(String jsonTask, URI path) {
        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(path)
                .header("Content-Type", "application/json")
                .build();
    }

    private HttpRequest deleteRequest(URI path) {
        return HttpRequest.newBuilder()
                .DELETE()
                .uri(path)
                .build();
    }

    @Test
    public void ifRequestIsIncorrectCode400() throws IOException, InterruptedException {
        // Отправляем Task
        URI postTaskPath = URI.create(url + "/taskss");
        HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        HttpResponse<String> postResponse = httpClient.send(postRequest, handler);
        int postTaskCode = postResponse.statusCode();

        // Отправляем Epic
        final URI postEpicPath = URI.create(url + "/epic?imi10");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        final HttpResponse<String> postEpicResponse = httpClient.send(postEpicRequest, handler);
        final int postEpicCode = postEpicResponse.statusCode();

        // Отправляем Subtask
        final URI postSubtaskPath = URI.create(url + "/subtaskepic?id=1");
        final HttpRequest postSubtaskRequest = postRequest(jsonSubtask1, postSubtaskPath);
        final HttpResponse<String> postSubtaskResponse = httpClient.send(postSubtaskRequest, handler);
        final int postSubtaskCode = postSubtaskResponse.statusCode();

        assertEquals(400, postTaskCode);
        assertEquals(400, postEpicCode);
        assertEquals(400, postSubtaskCode);
    }

    @Test
    public void checkThatNothingIsReturnedFromAnEmptyServer() throws IOException, InterruptedException {
        // Возвращаем Tasks
        final URI getTaskPath = URI.create(url);
        final HttpRequest getRequest = getRequest(getTaskPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);

        // Переводим в Task
        final String jsonGetTask = getResponse.body();
        final int code = getResponse.statusCode();
        final Type type = new TypeToken<List<Task>>() {
        }.getType();
        final List<Subtask> getTasks = gson.fromJson(jsonGetTask, type);

        // Проверяем
        assertTrue(getTasks.isEmpty());
        assertEquals(200, code);
    }

    @Test
    public void ifTheTaskIsAddedCode200() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        final HttpResponse<String> postResponse = httpClient.send(postRequest, handler);
        final int postCode = postResponse.statusCode();

        assertEquals(200, postCode);
    }

    @Test
    public void ifTheEpicIsAddedCode200() throws IOException, InterruptedException {
        // Отправляем Epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        final HttpResponse<String> postResponse = httpClient.send(postRequest, handler);
        final int postCode = postResponse.statusCode();

        assertEquals(200, postCode);
    }

    @Test
    public void ifTheSubtaskIsAddedCode200() throws IOException, InterruptedException {
        // Отправляем Subtask
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postRequest, handler);

        final URI postSubtask1Path = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtask1Request = postRequest(jsonSubtask1, postSubtask1Path);
        final HttpResponse<String> postSubtask1Response = httpClient.send(postSubtask1Request, handler);
        int postCode = postSubtask1Response.statusCode();

        assertEquals(200, postCode);
    }

    @Test
    public void ifTheTaskIsNotAddedCode400() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task?id=10");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        final HttpResponse<String> postResponse = httpClient.send(postRequest, handler);
        int postCode = postResponse.statusCode();

        assertEquals(400, postCode);
    }

    @Test
    public void ifTheEpicIsNotAddedCode400() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postEpicPath = URI.create(url + "/epic?id=10");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        final HttpResponse<String> postResponse = httpClient.send(postRequest, handler);
        final int postCode = postResponse.statusCode();

        assertEquals(400, postCode);
    }

    @Test
    public void ifTheSubtaskIsNotAddedCode400() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postRequest, handler);

        final URI postSubtask1Path = URI.create(url + "/subtask/epic?id=2");
        final HttpRequest postSubtask1Request = postRequest(jsonSubtask1, postSubtask1Path);
        final HttpResponse<String> postSubtask1Response = httpClient.send(postSubtask1Request, handler);
        final int postCode = postSubtask1Response.statusCode();

        assertEquals(400, postCode);
    }

    @Test
    public void taskMustBeReturnedByCorrectId() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        httpClient.send(postRequest, handler);

        // Возвращаем Task
        final URI getTaskPath = URI.create(url + "/task?id=1");
        final HttpRequest getRequest = getRequest(getTaskPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getCode = getResponse.statusCode();

        assertEquals(200, getCode);
    }

    @Test
    public void epicMustBeReturnedByCorrectId() throws IOException, InterruptedException {
        // Отправляем epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postRequest, handler);

        // Возвращаем Epic
        final URI getEpicPath = URI.create(url + "/epic?id=1");
        final HttpRequest getRequest = getRequest(getEpicPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getCode = getResponse.statusCode();

        assertEquals(200, getCode);
    }

    @Test
    public void subtaskMustBeReturnedByCorrectId() throws IOException, InterruptedException {
        // Отправляем Эпик
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postEpicRequest, handler);

        // Отправляем Subtask1
        final URI postSubtask1Path = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtask1Request = postRequest(jsonSubtask1, postSubtask1Path);
        httpClient.send(postSubtask1Request, handler);

        // Возвращаем Subtask1
        final URI getSubtask1Path = URI.create(url + "/subtask?id=2");
        final HttpRequest getSubtask1Request = getRequest(getSubtask1Path);
        final HttpResponse<String> getSubtask1Response = httpClient.send(getSubtask1Request, handler);
        final int getCode = getSubtask1Response.statusCode();

        assertEquals(200, getCode);
    }

    @Test
    public void taskShouldNotBeBackOnWrongId() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        httpClient.send(postRequest, handler);

        // Возвращаем Task
        final URI getTaskPath = URI.create(url + "/task?id=2");
        final HttpRequest getRequest = getRequest(getTaskPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getCode = getResponse.statusCode();

        assertEquals(404, getCode);
    }

    @Test
    public void epicShouldNotBeBackOnWrongId() throws IOException, InterruptedException {
        // Отправляем epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postRequest, handler);

        // Возвращаем Epic
        final URI getEpicPath = URI.create(url + "/epic?id=3");
        final HttpRequest getRequest = getRequest(getEpicPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getCode = getResponse.statusCode();

        assertEquals(404, getCode);
    }

    @Test
    public void subtaskShouldNotBeBackOnWrongId() throws IOException, InterruptedException {
        // Отправляем Эпик
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postEpicRequest, handler);

        // Отправляем Subtask1
        final URI postSubtask1Path = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtask1Request = postRequest(jsonSubtask1, postSubtask1Path);
        httpClient.send(postSubtask1Request, handler);

        // Возвращаем Subtask1
        final URI getSubtask1Path = URI.create(url + "/subtask?id=5");
        final HttpRequest getSubtask1Request = getRequest(getSubtask1Path);
        final HttpResponse<String> getSubtask1Response = httpClient.send(getSubtask1Request, handler);
        final int getCode = getSubtask1Response.statusCode();

        assertEquals(404, getCode);
    }

    @Test
    public void ifTaskIsDeletedThenItCannotBeRequested() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        httpClient.send(postRequest, handler);

        // Удаляем Task
        final URI deleteTaskPath = URI.create(url + "/task?id=1");
        final HttpRequest deleteRequest = deleteRequest(deleteTaskPath);
        final HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, handler);
        final int deleteCode = deleteResponse.statusCode();

        // Возвращаем Task
        final URI getTaskPath = URI.create(url + "/task?id=1");
        final HttpRequest getRequest = getRequest(getTaskPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getCode = getResponse.statusCode();

        assertEquals(200, deleteCode);
        assertEquals(404, getCode);
    }

    @Test
    public void ifEpicIsDeletedThenItCannotBeRequested() throws IOException, InterruptedException {
        // Отправляем Epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postRequest, handler);

        // Удаляем Epic
        final URI deleteEpicPath = URI.create(url + "/epic?id=1");
        final HttpRequest deleteRequest = deleteRequest(deleteEpicPath);
        final HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, handler);
        final int deleteCode = deleteResponse.statusCode();

        // Возвращаем Epic
        final URI getEpicPath = URI.create(url + "/epic?id=1");
        final HttpRequest getRequest = getRequest(getEpicPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getCode = getResponse.statusCode();

        assertEquals(200, deleteCode);
        assertEquals(404, getCode);
    }

    @Test
    public void ifSubtaskIsDeletedThenItCannotBeRequested() throws IOException, InterruptedException {
        // Отправляем Эпик
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postEpicRequest, handler);

        // Отправляем Subtask
        final URI postSubtaskPath = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtaskRequest = postRequest(jsonSubtask1, postSubtaskPath);
        httpClient.send(postSubtaskRequest, handler);

        // Удаляем Subtask
        final URI deleteSubtaskPath = URI.create(url + "/subtask?id=2");
        final HttpRequest deleteRequest = deleteRequest(deleteSubtaskPath);
        final HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, handler);
        final int deleteCode = deleteResponse.statusCode();

        // Возвращаем Subtask1
        final URI getSubtask1Path = URI.create(url + "/subtask?id=2");
        final HttpRequest getSubtask1Request = getRequest(getSubtask1Path);
        final HttpResponse<String> getSubtask1Response = httpClient.send(getSubtask1Request, handler);
        final int getCode = getSubtask1Response.statusCode();

        assertEquals(200, deleteCode);
        assertEquals(404, getCode);
    }

    @Test
    public void ifDeleteAllTasksThenNothingShouldReturn() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        httpClient.send(postRequest, handler);

        // Отправляем Эпик
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postEpicRequest, handler);

        // Отправляем Subtask
        final URI postSubtaskPath = URI.create(url + "/subtask/epic?id=2");
        final HttpRequest postSubtaskRequest = postRequest(jsonSubtask1, postSubtaskPath);
        httpClient.send(postSubtaskRequest, handler);

        // Удаляем все задачи
        final URI deleteAllTasksPath = URI.create(url);
        final HttpRequest deleteRequest = deleteRequest(deleteAllTasksPath);
        final HttpResponse<String> deleteResponse = httpClient.send(deleteRequest, handler);
        final int deleteCode = deleteResponse.statusCode();

        // Возвращаем список задач. Он теперь пустой.
        final URI getAllTasksPath = URI.create(url);
        final HttpRequest getSubtaskRequest = getRequest(getAllTasksPath);
        final HttpResponse<String> getResponse = httpClient.send(getSubtaskRequest, handler);
        final int getCode = getResponse.statusCode();

        // Конвертируем в Json
        final String allTasks = getResponse.body();
        final Type typeTask = new TypeToken<List<Task>>() {
        }.getType();
        final List<Task> tasks = gson.fromJson(allTasks, typeTask);

        // Проверяем
        assertTrue(tasks.isEmpty());
        assertEquals(200, deleteCode);
        assertEquals(200, getCode);
    }

    @Test
    public void checkThatExistingTaskSheetIsReturned() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        httpClient.send(postRequest, handler);

        // Отправляем Эпик
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postEpicRequest, handler);

        // Отправляем Subtask
        final URI postSubtaskPath = URI.create(url + "/subtask/epic?id=2");
        final HttpRequest postSubtaskRequest = postRequest(jsonSubtask1, postSubtaskPath);
        httpClient.send(postSubtaskRequest, handler);

        // Возвращаем список задач.
        final URI getAllTasksPath = URI.create(url);
        final HttpRequest getSubtaskRequest = getRequest(getAllTasksPath);
        final HttpResponse<String> getResponse = httpClient.send(getSubtaskRequest, handler);
        final int getCode = getResponse.statusCode();

        // Конвертируем в Json
        final String allTasks = getResponse.body();
        final Type typeTask = new TypeToken<List<Task>>() {
        }.getType();
        final List<Task> tasks = gson.fromJson(allTasks, typeTask);

        // Проверяем
        assertFalse(tasks.isEmpty());
        assertEquals(3, tasks.size());
        assertEquals(200, getCode);
    }

    @Test
    public void taskShouldNotBeCreatedIfYouPassIdWithIt() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        httpClient.send(postRequest, handler);

        // Отправляем Task
        final URI postAgainTaskPath = URI.create(url + "/task?id=1");
        final HttpRequest postAgainRequest = postRequest(jsonTask, postAgainTaskPath);
        httpClient.send(postAgainRequest, handler);

        // Возвращаем список задач.
        final URI getAllTasksPath = URI.create(url);
        final HttpRequest getAllTaskRequest = getRequest(getAllTasksPath);
        final HttpResponse<String> getResponse = httpClient.send(getAllTaskRequest, handler);
        final int getCode = getResponse.statusCode();

        // Конвертируем в Json
        final String allTasks = getResponse.body();
        final Type typeTask = new TypeToken<List<Task>>() {
        }.getType();
        final List<Task> tasks = gson.fromJson(allTasks, typeTask);

        assertEquals(1, tasks.size());
        assertEquals(200, getCode);
    }

    @Test
    public void epicShouldNotBeCreatedIfYouPassIdWithIt() throws IOException, InterruptedException {
        // Отправляем Epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postRequest, handler);

        // Отправляем Epic
        final URI postAgainEpicPath = URI.create(url + "/epic?id=1");
        final HttpRequest postAgainRequest = postRequest(jsonEpic, postAgainEpicPath);
        httpClient.send(postAgainRequest, handler);

        // Возвращаем список задач.
        final URI getAllEpicsPath = URI.create(url);
        final HttpRequest getAllTasksRequest = getRequest(getAllEpicsPath);
        final HttpResponse<String> getResponse = httpClient.send(getAllTasksRequest, handler);
        final int getCode = getResponse.statusCode();

        // Конвертируем в Json
        final String allTasks = getResponse.body();
        final Type typeTask = new TypeToken<List<Task>>() {
        }.getType();
        final List<Task> tasks = gson.fromJson(allTasks, typeTask);

        assertEquals(1, tasks.size());
        assertEquals(200, getCode);
    }

    @Test
    public void subtaskShouldNotBeCreatedIfYouPassIdWithIt() throws IOException, InterruptedException {
        // Отправляем Epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postRequest, handler);

        // Отправляем Subtask
        final URI postSubtaskPath = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtaskRequest = postRequest(jsonSubtask1, postSubtaskPath);
        httpClient.send(postSubtaskRequest, handler);

        // Отправляем Subtask ещё раз
        final URI postAgainSubtaskPath = URI.create(url + "/subtask?id=1");
        final HttpRequest postAgainSubtaskRequest = postRequest(jsonSubtask1, postAgainSubtaskPath);
        httpClient.send(postAgainSubtaskRequest, handler);

        // Возвращаем список задач.
        final URI getAllEpicsPath = URI.create(url);
        final HttpRequest getAllTasksRequest = getRequest(getAllEpicsPath);
        final HttpResponse<String> getResponse = httpClient.send(getAllTasksRequest, handler);
        final int getCode = getResponse.statusCode();

        // Конвертируем в Json
        final String allTasks = getResponse.body();
        final Type typeTask = new TypeToken<List<Task>>() {
        }.getType();
        final List<Task> tasks = gson.fromJson(allTasks, typeTask);

        assertEquals(2, tasks.size());
        assertEquals(200, getCode);
    }

    @Test
    public void sendNewTaskAndCheckThatItHasBeenSaved() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        final HttpResponse<String> postResponse = httpClient.send(postRequest, handler);
        final int postCode = postResponse.statusCode();

        // Возвращаем Task
        final URI getTaskPath = URI.create(url + "/task?id=1");
        HttpRequest getRequest = getRequest(getTaskPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getCode = getResponse.statusCode();

        // Конвертируем json в Task
        final String jsonGetTask = getResponse.body();
        final Task getTask = gson.fromJson(jsonGetTask, Task.class);

        // Обновляем менеджер с KV сервера.
        httpTaskManager = HTTPTaskManager.load();

        // Возвращаем из менеджера результат
        final Task postTask = httpTaskManager.getTaskById(1);

        // Сверяем полученное с KV сервера с отправленным на Http сервер.
        assertEquals(200, postCode);
        assertEquals(200, getCode);
        assertEquals(postTask, getTask);
    }

    @Test
    public void sendNewEpicAndCheckThatItHasBeenSaved() throws IOException, InterruptedException {
        // Отправляем epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        final HttpResponse<String> postResponse = httpClient.send(postRequest, handler);
        final int postCode = postResponse.statusCode();

        // Возвращаем Epic
        final URI getEpicPath = URI.create(url + "/epic?id=1");
        final HttpRequest getRequest = getRequest(getEpicPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getCode = getResponse.statusCode();

        // Конвертируем json в Epic
        final String jsonGetEpic = getResponse.body();
        final Task getEpic = gson.fromJson(jsonGetEpic, Epic.class);

        // Обновляем менеджер с KV сервера.
        httpTaskManager = HTTPTaskManager.load();

        // Возвращаем из менеджера результат
        final Epic postEpic = httpTaskManager.getEpicById(1);

        // Сверяем полученное с KV сервера с отправленным на Http сервер.
        assertEquals(200, postCode);
        assertEquals(200, getCode);
        assertEquals(postEpic, getEpic);
    }

    @Test
    public void sendNewSubtaskAndCheckThatItHasBeenSaved() throws IOException, InterruptedException {
        // Отправляем Эпик
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postEpicRequest, handler);

        // Отправляем Subtask1
        final URI postSubtask1Path = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtask1Request = postRequest(jsonSubtask1, postSubtask1Path);
        final HttpResponse<String> postSubtask1Response = httpClient.send(postSubtask1Request, handler);
        final int postSubtask1Code = postSubtask1Response.statusCode();

        // Отправляем Subtask2
        final URI postSubtask2Path = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtask2Request = postRequest(jsonSubtask2, postSubtask2Path);
        httpClient.send(postSubtask2Request, handler);
        final int postSubtask2Code = postSubtask1Response.statusCode();

        // Возвращаем Subtask1
        final URI getSubtask1Path = URI.create(url + "/subtask?id=2");
        final HttpRequest getSubtask1Request = getRequest(getSubtask1Path);
        final HttpResponse<String> getSubtask1Response = httpClient.send(getSubtask1Request, handler);
        final int getSubtask1Code = getSubtask1Response.statusCode();

        // Возвращаем Subtask2
        final URI getSubtask2Path = URI.create(url + "/subtask?id=3");
        final HttpRequest getSubtask2Request = getRequest(getSubtask2Path);
        final HttpResponse<String> getSubtask2Response = httpClient.send(getSubtask2Request, handler);
        final int getSubtask2Code = getSubtask2Response.statusCode();

        // Возвращаем лист подзадач Эпика
        final URI getSubtasksForEpicPath = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest getSubtaskForEpicRequest = getRequest(getSubtasksForEpicPath);
        final HttpResponse<String> getSubtasksForEpicResponse = httpClient.send(getSubtaskForEpicRequest, handler);
        final int getStForEpicCode = getSubtasksForEpicResponse.statusCode();

        // Конвертируем json в Subtask1
        final String jsonGetSubtask1 = getSubtask1Response.body();
        final Subtask getSubtask1 = gson.fromJson(jsonGetSubtask1, Subtask.class);

        // Конвертируем json в Subtask2
        final String jsonGetSubtask2 = getSubtask2Response.body();
        final Subtask getSubtask2 = gson.fromJson(jsonGetSubtask2, Subtask.class);

        // Конвертируем json в лист Subtasks
        final String jsonGetSubtasks = getSubtasksForEpicResponse.body();
        final Type type = new TypeToken<List<Subtask>>() {
        }.getType();
        final List<Subtask> getSubtasks = gson.fromJson(jsonGetSubtasks, type);

        // Обновляем менеджер с KV сервера
        httpTaskManager = HTTPTaskManager.load();

        // Возвращаем из менеджера результат
        final Epic postEpic = httpTaskManager.getEpicById(1);
        final Subtask postSubtask1 = httpTaskManager.getSubtaskById(2);
        final Subtask postSubtask2 = httpTaskManager.getSubtaskById(3);
        final List<Subtask> postSubtasksForEpic = httpTaskManager.getSubtaskForEpic(postEpic);

        // Сверяем полученное с KV сервера с отправленным на Http сервер.
        assertEquals(postSubtask1, getSubtask1);
        assertEquals(postSubtask2, getSubtask2);
        assertEquals(postSubtasksForEpic, getSubtasks);
        assertEquals(200, postSubtask1Code);
        assertEquals(200, postSubtask2Code);
        assertEquals(200, getStForEpicCode);
        assertEquals(200, getSubtask1Code);
        assertEquals(200, getSubtask2Code);
    }

    @Test
    public void checkThatAfterPostRequestTaskIsUpdated() throws IOException, InterruptedException {
        // Отправляем Task
        final URI postTaskPath = URI.create(url + "/task");
        final HttpRequest postRequest = postRequest(jsonTask, postTaskPath);
        httpClient.send(postRequest, handler);

        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDuration(Duration.ofDays(1));
        task.setTaskName("Happy new Year");
        task.setId(1); // Добавляю id, так как в менеджере задаче присвоится id=1
        final String newJsonTask = gson.toJson(task);

        // Отправляем Task
        final URI postTaskPathFoId = URI.create(url + "/task?id=1");
        final HttpRequest postRequestForId = postRequest(newJsonTask, postTaskPathFoId);
        final HttpResponse<String> postResponse = httpClient.send(postRequestForId, handler);

        final int postCode = postResponse.statusCode();

        // Возвращаем Tasks
        final URI getTaskPath = URI.create(url + "/task?id=1");
        final HttpRequest getRequest = getRequest(getTaskPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);

        // Конвертируем json в Task
        final String jsonGetTask = getResponse.body();
        final int getCode = getResponse.statusCode();
        final Task getTask = gson.fromJson(jsonGetTask, Task.class);

        assertEquals(task, getTask);
        assertEquals(200, postCode);
        assertEquals(200, getCode);
    }

    @Test
    public void checkThatAfterPostRequestEpicIsUpdated() throws IOException, InterruptedException {
        // Отправляем Epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postRequest, handler);

        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(Duration.ZERO);
        epic.setTaskName("Happy new Year");
        epic.setId(1); // Добавляю id, так как в менеджере задаче присвоится id=1
        final String newJsonEpic = gson.toJson(epic);

        // Отправляем обновленный Эпик
        final URI postEpicPathFoId = URI.create(url + "/epic?id=1");
        final HttpRequest postRequestForId = postRequest(newJsonEpic, postEpicPathFoId);
        final HttpResponse<String> postResponse = httpClient.send(postRequestForId, handler);
        final int postCode = postResponse.statusCode();

        // Возвращаем обновленный эпик
        final URI getEpicPath = URI.create(url + "/epic?id=1");
        final HttpRequest getRequest = getRequest(getEpicPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);

        // Конвертируем json в Епик
        final String jsonGetEpic = getResponse.body();
        final int getCode = getResponse.statusCode();
        final Epic getEpic = gson.fromJson(jsonGetEpic, Epic.class);

        assertEquals(epic, getEpic);
        assertEquals(200, postCode);
        assertEquals(200, getCode);
    }

    @Test
    public void checkThatAfterPostRequestSubtaskIsUpdated() throws IOException, InterruptedException {
        // Отправляем Epic
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postEpicRequest, handler);

        // Отправляем Subtask
        final URI postSubtaskPath = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtaskRequest = postRequest(jsonSubtask1, postSubtaskPath);
        httpClient.send(postSubtaskRequest, handler);

        subtask1.setStartTime(LocalDateTime.now());
        subtask1.setDuration(Duration.ZERO);
        subtask1.setTaskName("Happy new Year");
        subtask1.setId(2); // Добавляю id, так как в менеджере задаче присвоится id=2
        subtask1.setEpicIdForSubtask(1);
        final String newJsonSubtask = gson.toJson(subtask1);

        // Отправляем обновленный Subtask
        final URI postSubtaskPathFoId = URI.create(url + "/subtask?id=2");
        final HttpRequest postRequestForId = postRequest(newJsonSubtask, postSubtaskPathFoId);
        final HttpResponse<String> postResponse = httpClient.send(postRequestForId, handler);
        final int postCode = postResponse.statusCode();

        // Возвращаем обновленный Subtask
        final URI getSubtaskPath = URI.create(url + "/subtask?id=2");
        final HttpRequest getSubtaskRequest = getRequest(getSubtaskPath);
        final HttpResponse<String> getSubtaskResponse = httpClient.send(getSubtaskRequest, handler);
        final int getCode = getSubtaskResponse.statusCode();

        // Конвертируем json в Subtask
        final String jsonGetSubtask = getSubtaskResponse.body();
        final Subtask getSubtask1 = gson.fromJson(jsonGetSubtask, Subtask.class);

        assertEquals(subtask1, getSubtask1);
        assertEquals(200, postCode);
        assertEquals(200, getCode);
    }

    @Test
    public void checkThatEpicIsUpdatedWhenSubtasksAdded() throws IOException, InterruptedException {
        // Отправляем Epic и добавляем ему id
        final URI postEpicPath = URI.create(url + "/epic");
        final HttpRequest postEpicRequest = postRequest(jsonEpic, postEpicPath);
        httpClient.send(postEpicRequest, handler);
        epic.setId(1);

        // Изменяем поля Subtask
        subtask1.setStatus(TaskStatus.DONE);
        subtask1.setStartTime(LocalDateTime.now().minusDays(2));
        subtask1.setDuration(Duration.ofDays(2));
        final String jsonSubtask = gson.toJson(subtask1);

        // Изменяем поля Epic относительно Subtask
        epic.setStatus(TaskStatus.DONE);
        epic.setStartTime(subtask1.getStartTime());
        epic.setEndTime(subtask1.getEndTime());
        epic.setSubtasksIdForEpic(2);

        // Отправляем Subtask
        final URI postSubtaskPath = URI.create(url + "/subtask/epic?id=1");
        final HttpRequest postSubtaskRequest = postRequest(jsonSubtask, postSubtaskPath);
        httpClient.send(postSubtaskRequest, handler);

        // Возвращаем обновленный эпик
        final URI getEpicPath = URI.create(url + "/epic?id=1");
        final HttpRequest getRequest = getRequest(getEpicPath);
        final HttpResponse<String> getResponse = httpClient.send(getRequest, handler);
        final int getEpicCode = getResponse.statusCode();

        // Конвертируем json в Епик
        final String jsonGetEpic = getResponse.body();
        final Epic getEpic = gson.fromJson(jsonGetEpic, Epic.class);

        // Обновляем Епик задачу в менеджере и возвращаем.
        httpTaskManager = HTTPTaskManager.load();
        final Epic updateManagerEpic = httpTaskManager.getEpicById(1);

        assertEquals(epic, getEpic);
        assertEquals(updateManagerEpic, getEpic);
        assertEquals(200, getEpicCode);
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }
}
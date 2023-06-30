package ru.yandex.practicum.http.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String API_TOKEN;
    private final String url;

    public KVTaskClient(String url) {
        this.url = url;
        this.API_TOKEN = registerApiToken();
    }

    public void put(String key, String json) { // метод для сохранения задачи на сервер
        String path = url + "/save/" + key + "?API_TOKEN=" + API_TOKEN;
        URI uriPath = URI.create(path);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uriPath)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse.BodyHandler<String> postHandler = HttpResponse.BodyHandlers.ofString();

        try {
            client.send(postRequest, postHandler);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String load(String key) {
        String path = url + "/load/" + key + "?API_TOKEN=" + API_TOKEN;
        URI uriPath = URI.create(path);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriPath)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse.BodyHandler<String> postHandler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(postRequest, postHandler);
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("KVServer не смог обработать GET запрос по пути " + path);
            throw new RuntimeException(e.getMessage());
        }
    }

    private String registerApiToken() {
        String register = url + "/register";
        URI uriRegister = URI.create(register);

        HttpRequest regRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriRegister)
                .build();

        HttpResponse.BodyHandler<String> regHandler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> regResponse = client.send(regRequest, regHandler);
            return regResponse.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

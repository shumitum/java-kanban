package service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static servers.HttpTaskServer.DEFAULT_CHARSET;

public class KVTaskClient {
    private final String kvServerUrl;
    private String apiToken;

    public KVTaskClient(String kvServerUrl) {
        this.kvServerUrl = kvServerUrl;
        registration();
    }

    private void registration() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(kvServerUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiToken = response.body();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при регистрации" + e.getMessage());
        }
    }

    public void put(String key, String json) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kvServerUrl + "/save/" + key + "?API_TOKEN=" + apiToken))
                .header("Content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, DEFAULT_CHARSET))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка сохранения данных" + e.getMessage());
        }
    }

    public String load(String key) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kvServerUrl + "/load/" + key + "?API_TOKEN=" + apiToken))
                .header("Content-type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка загрузки данных" + e.getMessage());
        }
        return null;
    }
}

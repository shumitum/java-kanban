package service;

import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static servers.KVServer.PORT;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private HttpClient client;
    private final Gson gson = Managers.getGson();

    @Override
    HttpTaskManager getRightTypeOfManager() {
        return Managers.getDefault();
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = getRightTypeOfManager();
        task = new Task();
        subtask = new Subtask();
        epic = new Epic();
        HttpTaskManager.setId(0);
        client = HttpClient.newHttpClient();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    public void afterEach() {
        taskManager = null;
        task = null;
        subtask = null;
        epic = null;
        client = null;
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void shouldReturnHttpTaskManagerWithEpic() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest getEpicByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .GET()
                .build();
        client.send(getEpicByIdRequest, HttpResponse.BodyHandlers.ofString());
        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(1, httpTaskManager.getHistory().size(), "В истории просмотров должен быть один элемент");
        assertEquals(1, httpTaskManager.getListOfEpics().size(), "В списке должен быть одни эпик");
    }

    @Test
    void shouldReturnHttpTaskManagerWithEmptyHistory() throws IOException, InterruptedException {
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);


        assertEquals(1, httpTaskManager.getListOfTasks().size(), "В списке должна быть одна задача");
        assertEquals(1, httpTaskManager.getListOfEpics().size(), "В списке должен быть одни эпик");
        assertEquals(0, httpTaskManager.getHistory().size(), "История просмотров должна быть пустой");
    }
}
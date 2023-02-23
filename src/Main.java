import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import servers.HttpTaskServer;
import servers.KVServer;
import service.HttpTaskManager;
import service.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static servers.KVServer.PORT;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = Managers.getGson();
        Task task = new Task();
        task.setStartTime(LocalDateTime.now());
        task.setDuration(120);
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString()); //Создать задачу
        HttpRequest getTaskByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .GET()
                .build();
        client.send(getTaskByIdRequest, HttpResponse.BodyHandlers.ofString()); //Запросить задачу по ID
        Epic epic = new Epic();
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString()); //Создать эпик
        HttpRequest getEpicByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=2"))
                .GET()
                .build();
        client.send(getEpicByIdRequest, HttpResponse.BodyHandlers.ofString()); //Запросить эпик по ID
        Subtask subtask = new Subtask();
        subtask.setStartTime(LocalDateTime.now().minusHours(5));
        subtask.setDuration(180);
        subtask.setEpicId(2);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString()); //Создать подзадачу
        HttpRequest getSubtaskByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=3"))
                .GET()
                .build();
        client.send(getSubtaskByIdRequest, HttpResponse.BodyHandlers.ofString()); //Запросить подзадачу по ID

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);
        System.out.println("\n\nВОССТАНОВЛЕННЫЙ c сервера менеджер HttpTaskManager");
        System.out.println("ВОССТАНОВЛЕННЫЙ список задач\n" + httpTaskManager.getListOfTasks());
        System.out.println("ВОССТАНОВЛЕННЫЙ список эпиков\n" + httpTaskManager.getListOfEpics());
        System.out.println("ВОССТАНОВЛЕННЫЙ список подзадач\n" + httpTaskManager.getListOfSubtasks());
        System.out.println("ВОССТАНОВЛЕННЫЙ список задач в порядке приоритета\n" + httpTaskManager.getPrioritizedTasks());
        System.out.println("ВОССТАНОВЛЕННАЯ история\n" + httpTaskManager.getHistory());

        httpTaskServer.stop();
        kvServer.stop();
    }
}
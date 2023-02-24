package servers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskManager;
import service.LocalDateTimeFormatter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static servers.KVServer.PORT;

class HttpTaskServerTest {

    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private HttpClient client;
    private final Gson gson = LocalDateTimeFormatter.getGson();
    private Task task;
    Subtask subtask;
    Epic epic;

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
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
        task = null;
        subtask = null;
        epic = null;
        client = null;
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void shouldCreateNewTaskOnServer() throws IOException, InterruptedException {
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(1, httpTaskManager.getListOfTasks().size(), "В списке должен быть одна задача");
        assertEquals(1, httpTaskManager.getTaskById(1).getId(), "Неверный ID задачи");
    }

    @Test
    void shouldUpdateTaskOnServer() throws IOException, InterruptedException {
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        task.setTaskName("new task 1");
        task.setId(1);
        HttpRequest updateTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> newResponse = client.send(updateTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(1, httpTaskManager.getListOfTasks().size(), "В списке должен быть одна задача");
        assertEquals("new task 1", httpTaskManager.getTaskById(1).getTaskName(), "В списке должен быть одна задача");
    }

    @Test
    void shouldReturnTaskByIdFromServer() throws IOException, InterruptedException {
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest getTaskByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(getTaskByIdRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        final Task actualTask = gson.fromJson(newResponse.body(), Task.class);

        assertNotNull(actualTask, "Задача не вернулась");
        assertEquals(1, actualTask.getId(), "ID задачи должен быть 1");
    }

    @Test
    void shouldReturnTaskListFromServer() throws IOException, InterruptedException {
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest getTasksListRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(getTasksListRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        final List<Task> actualTasksList = gson.fromJson(newResponse.body(), taskType);

        assertNotNull(actualTasksList, "Список задач не вернулся");
        assertEquals(1, actualTasksList.size(), "Количество задач не совпадает");
    }

    @Test
    void shouldDeleteTaskByIdFromServer() throws IOException, InterruptedException {
        for (int i = 0; i < 2; i++) {
            HttpRequest createTaskRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/task/"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                    .build();
            HttpResponse<String> response = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        }

        HttpRequest deleteTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .DELETE()
                .build();
        HttpResponse<String> newResponse = client.send(deleteTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertNotNull(httpTaskManager.getListOfTasks(), "Список задач не должен быть пуст");
        assertEquals(1, httpTaskManager.getListOfTasks().size(), "В списке задач должна остаться одна задача");
    }

    @Test
    void shouldClearTaskListOnServer() throws IOException, InterruptedException {
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpRequest deleteTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .DELETE()
                .build();
        HttpResponse<String> newResponse = client.send(deleteTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(0, httpTaskManager.getListOfTasks().size(), "Список задач должен быть пуст");
    }

    @Test
    void shouldCreateNewEpicOnServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(1, httpTaskManager.getListOfEpics().size(), "В списке должен быть один эпик");
        assertEquals(1, httpTaskManager.getEpicById(1).getId(), "Неверный ID эпика");
    }

    @Test
    void shouldUpdateEpicOnServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        epic.setTaskName("new epic 1");
        epic.setId(1);
        HttpRequest updateEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> newResponse = client.send(updateEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(1, httpTaskManager.getListOfEpics().size(), "В списке должен быть один эпик");
        assertEquals("new epic 1", httpTaskManager.getEpicById(1).getTaskName(),
                "Название эпика отличается от заданного");
    }

    @Test
    void shouldReturnEpicByIdFromServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest getEpicByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(getEpicByIdRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        final Epic actualEpic = gson.fromJson(newResponse.body(), Epic.class);

        assertNotNull(actualEpic, "Эпик не вернулся");
        assertEquals(1, actualEpic.getId(), "ID эпика должен быть 1");
    }

    @Test
    void shouldReturnEpicListFromServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest getEpicsListRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(getEpicsListRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>(){}.getType();
        final List<Epic> actualEpicsList = gson.fromJson(newResponse.body(), taskType);

        assertNotNull(actualEpicsList, "Список эпиков пуст");
        assertEquals(1, actualEpicsList.size(), "Количество эпиков не совпадает");
    }

    @Test
    void shouldDeleteEpicByIdFromServer() throws IOException, InterruptedException {
        for (int i = 0; i < 2; i++) {
            HttpRequest createEpicRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/epic/"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                    .build();
            HttpResponse<String> response = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        }

        HttpRequest deleteEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                .DELETE()
                .build();
        HttpResponse<String> newResponse = client.send(deleteEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertNotNull(httpTaskManager.getListOfEpics(), "Список эпиков не должен быть пуст");
        assertEquals(1, httpTaskManager.getListOfEpics().size(), "В списке эпиков должен остаться один эпик");
    }

    @Test
    void shouldClearEpicListOnServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest deleteEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .DELETE()
                .build();
        HttpResponse<String> newResponse = client.send(deleteEpicRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(0, httpTaskManager.getListOfEpics().size(), "Список эпиков должен быть пуст");
    }

    @Test
    void shouldCreateNewSubtaskOnServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        subtask.setEpicId(1);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(1, httpTaskManager.getListOfSubtasks().size(), "В списке должен быть одна подзадача");
        assertEquals(2, httpTaskManager.getSubtaskById(2).getId(), "Неверный ID подзадачи");
    }

    @Test
    void shouldUpdateSubtaskOnServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        subtask.setEpicId(1);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        subtask.setTaskName("new subtask 1");
        subtask.setId(2);
        HttpRequest updateSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=1"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> newResponse = client.send(updateSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(1, httpTaskManager.getListOfSubtasks().size(), "В списке должна быть одна подзадача");
        assertEquals("new subtask 1", httpTaskManager.getSubtaskById(2).getTaskName(),
                "Название подзадачи отличается от заданного");
    }

    @Test
    void shouldReturnSubtaskByIdFromServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        subtask.setEpicId(1);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest getSubtaskByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(getSubtaskByIdRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        final Subtask actualSubtask = gson.fromJson(newResponse.body(), Subtask.class);

        assertNotNull(actualSubtask, "Подзадача не вернулась");
        assertEquals(2, actualSubtask.getId(), "ID подзадачи должен быть 2");
    }

    @Test
    void shouldReturnSubtaskListFromServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        subtask.setEpicId(1);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest getSubtasksListRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(getSubtasksListRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        final List<Subtask> actualSubtasksList = gson.fromJson(newResponse.body(), taskType);

        assertNotNull(actualSubtasksList, "Список подзадач пуст");
        assertEquals(1, actualSubtasksList.size(), "Количество подзадач не совпадает");
        assertEquals(2, actualSubtasksList.get(0).getId(), "ID подзадачи должен быть 2");
    }

    @Test
    void shouldDeleteSubtaskByIdFromServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        subtask.setEpicId(1);
        for (int i = 0; i < 2; i++) {
            HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                    .build();
            HttpResponse<String> response = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        }

        HttpRequest deleteSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                .DELETE()
                .build();
        HttpResponse<String> newResponse = client.send(deleteSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertNotNull(httpTaskManager.getListOfSubtasks(), "Список подзадач не должен быть пуст");
        assertEquals(1, httpTaskManager.getListOfSubtasks().size(), "В списке подзадач должна остаться одна задача");
        assertEquals(3, httpTaskManager.getListOfSubtasks().get(0).getId(), "ID подзадачи должен быть 3");
    }

    @Test
    void shouldClearSubtaskListOnServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        subtask.setEpicId(1);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest deleteSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .DELETE()
                .build();
        HttpResponse<String> newResponse = client.send(deleteSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer("http://localhost:" + PORT);

        assertEquals(0, httpTaskManager.getListOfSubtasks().size(), "Список подзадач должен быть пуст");
    }

    @Test
    void shouldReturnPrioritizedTasksListFromServer() throws IOException, InterruptedException {
        task.setStartTime(LocalDateTime.now());
        task.setDuration(120);
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());
        Task newtask = new Task();
        newtask.setStartTime(LocalDateTime.now().minusHours(4));
        newtask.setDuration(60);
        HttpRequest createNewTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newtask)))
                .build();
        HttpResponse<String> response = client.send(createNewTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest PrioritizedTasksListRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(PrioritizedTasksListRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        final List<Task> actualTasksList = gson.fromJson(newResponse.body(), taskType);

        assertEquals(2, actualTasksList.size(),
                "Количество задач в списке по приоритету не совпадает");
        assertEquals(2, actualTasksList.get(0).getId(),
                "ID задачи не совпадает");
        assertEquals(1, actualTasksList.get(1).getId(),
                "ID задачи не совпадает");
    }

    @Test
    void shouldReturnEpicsSubtasksListFromServer() throws IOException, InterruptedException {
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        subtask.setEpicId(1);
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();
        HttpResponse<String> response = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest getEpicsSubtasksRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/epic/?id=1"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(getEpicsSubtasksRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, newResponse.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        final List<Subtask> actualTasksList = gson.fromJson(newResponse.body(), taskType);

        assertEquals(1, actualTasksList.size(),
                "Количество подзадач в вписке не совпадает");
        assertEquals(2, actualTasksList.get(0).getId(),
                "ID подзадачи не совпадает");
    }

    @Test
    void shouldReturnHistoryFromServer() throws IOException, InterruptedException {
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpRequest getHistoryRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history/"))
                .GET()
                .build();
        HttpResponse<String> newResponse = client.send(getHistoryRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, newResponse.statusCode(), "История просмотра должна быть пуста");

        HttpRequest getTaskByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .GET()
                .build();
        HttpResponse<String> thirdResponse = client.send(getTaskByIdRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, thirdResponse.statusCode());

        HttpRequest newGetHistoryRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history/"))
                .GET()
                .build();
        HttpResponse<String> forthResponse = client.send(newGetHistoryRequest, HttpResponse.BodyHandlers.ofString());
        Task[] tasks = gson.fromJson(forthResponse.body(), Task[].class);

        assertEquals(200, forthResponse.statusCode());
        assertEquals(1, tasks[0].getId(), "ID задачи из истории просмотров не совпадает");
    }
}
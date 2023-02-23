package servers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;
import service.HttpTaskManager;
import service.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HttpTaskServer {
    public static final int PORT = 8080;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final HttpServer httpTaskServer;
    HttpTaskManager httpTaskManager;

    public HttpTaskServer() throws IOException {
        httpTaskManager = Managers.getDefault();
        httpTaskServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpTaskServer.createContext("/tasks", new taskHandler());
        httpTaskServer.createContext("/tasks/task", new taskHandler());
        httpTaskServer.createContext("/tasks/subtask", new subtaskHandler());
        httpTaskServer.createContext("/tasks/epic", new epicHandler());
        httpTaskServer.createContext("/tasks/subtask/epic", new EpicsSubtaskHandler());
        httpTaskServer.createContext("/tasks/history", new historyHandler());
        gson = Managers.getGson();
    }

    public void start() {
        httpTaskServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpTaskServer.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private class taskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String requestMethod = exchange.getRequestMethod();
            try {
                switch (requestMethod) {
                    case "GET": {
                        if (Pattern.matches("^/tasks/$", path)) {
                            if (!httpTaskManager.getPrioritizedTasks().isEmpty()) {
                                String jsonPrioritizedTasks = gson.toJson(httpTaskManager.getPrioritizedTasks());
                                writeResponse(exchange, jsonPrioritizedTasks, 200);
                            } else {
                                exchange.sendResponseHeaders(204, -1);
                            }
                        } else if (Pattern.matches("^/tasks/task/$", path)) {
                            if (query == null) {
                                String jsonTasks = gson.toJson(httpTaskManager.getListOfTasks());
                                if (!jsonTasks.isBlank()) {
                                    writeResponse(exchange, jsonTasks, 200);
                                }
                            } else {
                                String pathId = query.replaceFirst("id=", "");
                                int taskId = parseId(pathId);
                                if (taskId != -1) {
                                    if (httpTaskManager.getTaskById(taskId) != null) {
                                        String jsonTask = gson.toJson(httpTaskManager.getTaskById(taskId));
                                        writeResponse(exchange, jsonTask, 200);
                                    } else {
                                        writeResponse(exchange, "Получен несуществующий ID задачи", 404);
                                    }
                                } else {
                                    writeResponse(exchange, "Получен неверный ID задачи", 404);
                                }
                            }
                        }
                        break;
                    }
                    case "POST": {
                        if (Pattern.matches("^/tasks/task/$", path)) {
                            InputStream inputStream = exchange.getRequestBody();
                            String jsonBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                            if (!jsonBody.isBlank()) {
                                try {
                                    Task task = gson.fromJson(jsonBody, Task.class);
                                    if (task.getId() == 0) {
                                        httpTaskManager.createTask(task);
                                        writeResponse(exchange, "Задача создана", 200);
                                    } else {
                                        httpTaskManager.updateTask(task);
                                        writeResponse(exchange, "Задача обновлена", 200);
                                    }
                                } catch (JsonSyntaxException e) {
                                    writeResponse(exchange, "Получен некорректный JSON", 400);
                                    return;
                                }
                            } else {
                                writeResponse(exchange, "Задача не передана", 404);
                            }
                        }
                        break;
                    }
                    case "DELETE": {
                        if (Pattern.matches("^/tasks/task/$", path)) {
                            if (query == null) {
                                httpTaskManager.clearTasksList();
                                writeResponse(exchange, "Список задач очищен", 200);
                            } else {
                                String pathId = query.replaceFirst("id=", "");
                                int taskId = parseId(pathId);
                                if (taskId != -1) {
                                    if (httpTaskManager.getTaskById(taskId) != null) {
                                        httpTaskManager.deleteTaskById(taskId);
                                        writeResponse(exchange, "Задача с ID " + pathId + " удалена", 200);
                                    } else {
                                        writeResponse(exchange, "Получен несуществующий ID задачи", 404);
                                    }
                                } else {
                                    writeResponse(exchange, "Получен неверный ID задачи " + pathId, 404);
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        writeResponse(exchange, "Получен недопустимый метод", 405);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }
    }

    private class subtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            String query = exchange.getRequestURI().getQuery();
            String requestMethod = exchange.getRequestMethod();
            try {
                switch (requestMethod) {
                    case "GET": {
                        if (query == null) {
                            if (!httpTaskManager.getListOfSubtasks().isEmpty()) {
                                String jsonSubtasks = gson.toJson(httpTaskManager.getListOfSubtasks());
                                writeResponse(exchange, jsonSubtasks, 200);
                            } else {
                                exchange.sendResponseHeaders(204, -1);
                            }
                        } else {
                            String pathId = query.replaceFirst("id=", "");
                            int taskId = parseId(pathId);
                            if (taskId != -1) {
                                if (httpTaskManager.getSubtaskById(taskId) != null) {
                                    String jsonSubtasks = gson.toJson(httpTaskManager.getSubtaskById(taskId));
                                    writeResponse(exchange, jsonSubtasks, 200);
                                } else {
                                    writeResponse(exchange, "Получен несуществующий ID подзадачи'", 404);
                                }
                            } else {
                                writeResponse(exchange, "Получен неверный ID подзадачи", 404);
                            }
                        }
                        break;
                    }
                    case "POST": {
                        InputStream inputStream = exchange.getRequestBody();
                        String jsonBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        if (!jsonBody.isBlank()) {
                            try {
                                Subtask subtask = gson.fromJson(jsonBody, Subtask.class);
                                if (subtask.getId() == 0) {
                                    httpTaskManager.createSubtask(subtask);
                                    writeResponse(exchange, "Подзадача создана", 200);
                                } else {
                                    httpTaskManager.updateSubtask(subtask);
                                    writeResponse(exchange, "Подзадача обновлена", 200);
                                }
                            } catch (JsonSyntaxException e) {
                                writeResponse(exchange, "Получен некорректный JSON", 400);
                                return;
                            }
                        } else {
                            writeResponse(exchange, "Подзадача не передана", 404);
                        }
                        break;
                    }
                    case "DELETE": {
                        if (query == null) {
                            httpTaskManager.clearSubtaskList();
                            writeResponse(exchange, "Список подзадач очищен", 200);
                        } else {
                            String pathId = query.replaceFirst("id=", "");
                            int taskId = parseId(pathId);
                            if (taskId != -1) {
                                if (httpTaskManager.getSubtaskById(taskId) != null) {
                                    httpTaskManager.deleteSubtaskById(taskId);
                                    writeResponse(exchange, "Подзадача с ID " + pathId + " удалена", 200);
                                    return;
                                } else {
                                    writeResponse(exchange, "Получен несуществующий ID подзадачи", 404);
                                }
                            } else {
                                writeResponse(exchange, "Получен неверный ID подзадачи " + pathId, 404);
                            }
                        }
                        break;
                    }
                    default: {
                        writeResponse(exchange, "Получен недопустимый метод", 405);
                    }
                }
            } catch (
                    IOException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }
    }

    private class epicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            String query = exchange.getRequestURI().getQuery();
            String requestMethod = exchange.getRequestMethod();
            try {
                switch (requestMethod) {
                    case "GET": {
                        if (query == null) {
                            if (!httpTaskManager.getListOfEpics().isEmpty()) {
                                String jsonEpics = gson.toJson(httpTaskManager.getListOfEpics());
                                writeResponse(exchange, jsonEpics, 200);
                            } else {
                                exchange.sendResponseHeaders(204, -1);
                            }
                        } else {
                            String pathId = query.replaceFirst("id=", "");
                            int taskId = parseId(pathId);
                            if (taskId != -1) {
                                if (httpTaskManager.getEpicById(taskId) != null) {
                                    String jsonEpic = gson.toJson(httpTaskManager.getEpicById(taskId));
                                    writeResponse(exchange, jsonEpic, 200);
                                } else {
                                    writeResponse(exchange, "Получен несуществующий ID эпика'", 404);
                                }
                            } else {
                                writeResponse(exchange, "Получен неверный ID эпика", 404);
                            }
                        }
                        break;
                    }
                    case "POST": {
                        InputStream inputStream = exchange.getRequestBody();
                        String jsonBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                        if (!jsonBody.isBlank()) {
                            try {
                                Epic epic = gson.fromJson(jsonBody, Epic.class);
                                if (epic.getId() == 0) {
                                    httpTaskManager.createEpic(epic);
                                    writeResponse(exchange, "Эпик создан", 200);
                                } else {
                                    httpTaskManager.updateEpic(epic);
                                    writeResponse(exchange, "Эпик обновлен", 200);
                                }
                            } catch (JsonSyntaxException e) {
                                writeResponse(exchange, "Получен некорректный JSON", 400);
                                return;
                            }
                        } else {
                            writeResponse(exchange, "Эпик не передан", 404);
                        }
                        break;
                    }
                    case "DELETE": {
                        if (query == null) {
                            httpTaskManager.clearEpicList();
                            writeResponse(exchange, "Список эпиков очищен", 200);
                        } else {
                            String pathId = query.replaceFirst("id=", "");
                            int taskId = parseId(pathId);
                            if (taskId != -1) {
                                if (httpTaskManager.getEpicById(taskId) != null) {
                                    httpTaskManager.deleteEpicById(taskId);
                                    writeResponse(exchange, "Эпик с ID " + pathId + " удалён", 200);
                                } else {
                                    writeResponse(exchange, "Получен несуществующий ID эпика", 404);
                                }
                            } else {
                                writeResponse(exchange, "Получен неверный ID эпика " + pathId, 404);
                            }
                        }
                        break;
                    }
                    default: {
                        writeResponse(exchange, "Получен недопустимый метод", 405);
                    }
                }
            } catch (
                    IOException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }
    }

    private class historyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (exchange.getRequestMethod().equals("GET")) {
                    if (!httpTaskManager.getHistory().isEmpty()) {
                        String jsonHistory = gson.toJson(httpTaskManager.getHistory());
                        writeResponse(exchange, jsonHistory, 200);
                    } else {
                        exchange.sendResponseHeaders(204, -1);
                    }
                } else {
                    writeResponse(exchange, "Получен недопустимый метод", 405);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }
    }

    private class EpicsSubtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            String query = exchange.getRequestURI().getQuery();
            try {
                if (exchange.getRequestMethod().equals("GET")) {
                    String pathId = query.replaceFirst("id=", "");
                    int epicId = parseId(pathId);
                    if (epicId != -1) {
                        if (httpTaskManager.getEpicsSubtaskList(epicId) != null) {
                            String jsonEpicsSubtasks = gson.toJson(httpTaskManager.getPrioritizedTasks());
                            writeResponse(exchange, jsonEpicsSubtasks, 200);
                        } else {
                            exchange.sendResponseHeaders(204, -1);
                        }
                    } else {
                        writeResponse(exchange, "Получен неверный ID эпика " + pathId, 404);
                    }
                } else {
                    writeResponse(exchange, "Получен недопустимый метод", 405);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }
    }

    private int parseId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

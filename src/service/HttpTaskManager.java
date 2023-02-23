package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String kvServerUrl) {
        kvTaskClient = new KVTaskClient(kvServerUrl);
        gson = Managers.getGson();
    }

    @Override
    public void save() {
        try {
            kvTaskClient.put("tasks", gson.toJson(getListOfTasks()));
            kvTaskClient.put("subtasks", gson.toJson(getListOfSubtasks()));
            kvTaskClient.put("epics", gson.toJson(getListOfEpics()));
            kvTaskClient.put("history", historyToString(getHistoryManager()));
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException("Не удалось сохранить данные", e);
        }
    }

    public static HttpTaskManager loadFromServer(String kvServerUrl) {
        Map<Integer, Task> allTasks = new HashMap<>();
        Gson gson = Managers.getGson();
        HttpTaskManager httpTaskManager = new HttpTaskManager(kvServerUrl);
        String jSonTasks = httpTaskManager.kvTaskClient.load("tasks");
        Type tasksTypeList = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(jSonTasks, tasksTypeList);
        if (tasks != null) {
            for (Task task : tasks) {
                httpTaskManager.createTask(task.getId(), task);
                allTasks.put(task.getId(), task);
            }
        }

        String jSonEpics = httpTaskManager.kvTaskClient.load("epics");
        Type epicsTypeList = new TypeToken<ArrayList<Epic>>(){}.getType();
        List<Epic> epics = gson.fromJson(jSonEpics, epicsTypeList);
        if (epics != null) {
            for (Epic epic : epics) {
                httpTaskManager.createEpic(epic.getId(), epic);
                allTasks.put(epic.getId(), epic);
            }
        }

        String jSonSubtasks = httpTaskManager.kvTaskClient.load("subtasks");
        Type subtasksTypeList = new TypeToken<ArrayList<Subtask>>(){}.getType();
        List<Subtask> subtasks = gson.fromJson(jSonSubtasks, subtasksTypeList);
        if (subtasks != null) {
            for (Subtask subtask : subtasks) {
                httpTaskManager.createSubtask(subtask.getEpicId(), subtask);
                allTasks.put(subtask.getId(), subtask);
            }
        }

        String historyIDs = httpTaskManager.kvTaskClient.load("history");
        if (historyIDs != null) {
            for (Integer id : historyFromString(historyIDs)) {
                if (allTasks.containsKey(id)) {
                    httpTaskManager.getHistoryManager().add(allTasks.get(id));
                }
            }
        }
        return httpTaskManager;
    }


}

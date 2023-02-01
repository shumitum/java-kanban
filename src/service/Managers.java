package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        String tasksFromFile;
        int currentId = 0;
        try {
            tasksFromFile = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] tasksAsStrings = tasksFromFile.split("\r?\n");
        String historyIds = tasksAsStrings[tasksAsStrings.length - 1];
        Map<Integer, Task> allTasks = new HashMap<>();
        for (int i = 1; !tasksAsStrings[i].equals(""); i++) {
            Task task = fromString(tasksAsStrings[i]);
            if (task instanceof Epic) {
                fileBackedTasksManager.createEpic(task.getId(), (Epic) task);
            } else if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                fileBackedTasksManager.createSubtask(subtask.getEpicId(), subtask);
            } else {
                fileBackedTasksManager.createTask(task.getId(), task);
            }
            allTasks.put(task.getId(), task);
            if (task.getId() > currentId) {
                currentId = task.getId();
            }
        }
        for (Integer id : historyFromString(historyIds)) {
            if (allTasks.containsKey(id)) {
                fileBackedTasksManager.getHistoryManager().add(allTasks.get(id));
            }
        }
        InMemoryTaskManager.setId(currentId);
        return fileBackedTasksManager;
    }

    private static Task fromString(String value) {
        String[] taskFields = value.split(",");

        Task task = null;
        switch (taskFields[1]) {
            case "TASK":
                task = new Task(Integer.parseInt(taskFields[0]), taskFields[2], Status.valueOf(taskFields[3]), taskFields[4]);
                break;
            case "EPIC":
                task = new Epic(Integer.parseInt(taskFields[0]), taskFields[2], Status.valueOf(taskFields[3]), taskFields[4]);
                break;
            case "SUBTASK":
                task = new Subtask(Integer.parseInt(taskFields[0]), taskFields[2], Status.valueOf(taskFields[3]),
                        taskFields[4], Integer.parseInt(taskFields[5]));
                break;
        }
        return task;
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        for (String id : value.split(",")) {
            historyIds.add(Integer.parseInt(id));
        }
        return historyIds;
    }

}
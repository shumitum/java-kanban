package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

import static model.Task.TIME_FORMATTER;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static final String PATH = "." + File.separator + "resources" + File.separator + "data.csv";
    public static final String HEADER = "id,type,name,status,description,start_time,duration,end_time,epic\n";

    public FileBackedTasksManager() {
    }

    public void save() {
        List<Task> allTasksList = new ArrayList<>(getListOfTasks());
        allTasksList.addAll(getListOfEpics());
        allTasksList.addAll(getListOfSubtasks());
        try (Writer taskWriter = new FileWriter(PATH, StandardCharsets.UTF_8)) {
            taskWriter.write(HEADER);
            for (Task task : allTasksList) {
                taskWriter.write(task.toString() + "\n");
            }
            taskWriter.write("\n");
            taskWriter.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить файл", e);
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<String> id = new ArrayList<>();
        if (!manager.getHistory().isEmpty()) {
            for (Task task : manager.getHistory()) {
                id.add(String.valueOf(task.getId()));
            }
        }
        return String.join(",", id);
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        String tasksFromFile;
        int currentId = 0;
        try {
            tasksFromFile = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось прочитать файл", e);
        }
        String[] tasksAndHistory = tasksFromFile.split("\r?\n\n");
        String[] tasksAsStrings = tasksAndHistory[0].split("\r?\n");
        Map<Integer, Task> allTasks = new HashMap<>();
        if (tasksAsStrings.length > 1) {
            for (int i = 1; i < tasksAsStrings.length; i++) {
                Task task = fileBackedTasksManager.fromString(tasksAsStrings[i]);
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
        }
        if (tasksAndHistory.length > 1) {
            String historyIds = tasksAndHistory[1];
            for (Integer id : historyFromString(historyIds)) {
                if (allTasks.containsKey(id)) {
                    fileBackedTasksManager.getHistoryManager().add(allTasks.get(id));
                }
            }
        }
        InMemoryTaskManager.setId(currentId);
        return fileBackedTasksManager;
    }

    private Task fromString(String value) {
        String[] taskFields = value.split(",");

        Task task = null;
        switch (taskFields[1]) {
            case "TASK":
                task = new Task(
                        Integer.parseInt(taskFields[0]),
                        taskFields[2],
                        Status.valueOf(taskFields[3]),
                        taskFields[4],
                        parseStringToTime(taskFields[5]),
                        Integer.parseInt(taskFields[6]));
                break;
            case "EPIC":
                task = new Epic(
                        Integer.parseInt(taskFields[0]),
                        taskFields[2],
                        Status.valueOf(taskFields[3]),
                        taskFields[4],
                        parseStringToTime(taskFields[5]),
                        Integer.parseInt(taskFields[6]));
                break;
            case "SUBTASK":
                task = new Subtask(
                        Integer.parseInt(taskFields[0]),
                        taskFields[2],
                        Status.valueOf(taskFields[3]),
                        taskFields[4],
                        parseStringToTime(taskFields[5]),
                        Integer.parseInt(taskFields[6]),
                        Integer.parseInt(taskFields[8]));
                break;
        }
        return task;
    }

    private LocalDateTime parseStringToTime(String startTime) {
        if (Objects.equals(startTime, "null")) {
            return null;
        }
        return LocalDateTime.parse(startTime, TIME_FORMATTER);
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        for (String id : value.split(",")) {
            historyIds.add(Integer.parseInt(id));
        }
        return historyIds;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        if (task != null) {
            save();
        }
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void clearTasksList() {
        super.clearTasksList();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        if (epic != null) {
            save();
        }
        return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void clearEpicList() {
        super.clearEpicList();
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        if (subtask != null) {
            save();
        }
        return subtask;
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void clearSubtaskList() {
        super.clearSubtaskList();
        save();
    }
}
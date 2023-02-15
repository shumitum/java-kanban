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
    public static void main(String[] args) {
        TaskManager fileBackedTasksManager = Managers.getDefault();

        Epic epicTask1 = new Epic();
        epicTask1.setTaskName("epic 1");
        epicTask1.setDescription("NEW DESCRIPTION...");
        Epic epicTask2 = new Epic();
        epicTask2.setTaskName("epic 2");
        Task newTask1 = new Task();
        newTask1.setTaskName("task 1");
        Task newTask2 = new Task();
        newTask2.setTaskName("task 2");
        newTask2.setStartTime(LocalDateTime.now().plusHours(1));
        newTask2.setDuration(60);
        Subtask subtask1 = new Subtask();
        subtask1.setTaskName("sub 1");
        Subtask subtask2 = new Subtask();
        subtask2.setTaskName("sub 2");
        subtask2.setStartTime(LocalDateTime.now().minusHours(1));
        subtask2.setDuration(320);
        Subtask subtask3 = new Subtask();
        subtask3.setTaskName("sub 3");
        subtask3.setStartTime(LocalDateTime.now().minusHours(5));
        subtask3.setDuration(240);
        Subtask subtask4 = new Subtask();
        subtask4.setTaskName("sub 4");
        subtask4.setStartTime(LocalDateTime.now().plusHours(8));
        subtask4.setDuration(120);
        Subtask subtask5 = new Subtask();
        subtask5.setTaskName("sub 5");
        subtask5.setStartTime(LocalDateTime.now().minusHours(2));
        subtask5.setDuration(120);
        Subtask subtask6 = new Subtask();
        subtask6.setTaskName("sub 6");
        Subtask subtask7 = new Subtask();
        subtask7.setTaskName("sub 7");

        System.out.println("СОЗДАНИЕ ЗАДАЧ");
        fileBackedTasksManager.createEpic(epicTask1);
        System.out.println(epicTask1);
        fileBackedTasksManager.createEpic(epicTask2);
        System.out.println(epicTask2);
        fileBackedTasksManager.createTask(newTask1);
        System.out.println(newTask1);
        fileBackedTasksManager.createSubtask(epicTask1, subtask6);
        System.out.println(subtask6);
        fileBackedTasksManager.createSubtask(epicTask1, subtask1);
        System.out.println(subtask1);
        fileBackedTasksManager.createSubtask(epicTask1, subtask4);
        System.out.println(subtask4);
        fileBackedTasksManager.createSubtask(epicTask1, subtask7);
        System.out.println(subtask7);
        fileBackedTasksManager.createSubtask(epicTask2, subtask2);
        System.out.println(subtask2);
        fileBackedTasksManager.createSubtask(epicTask2, subtask3);
        System.out.println(subtask3);
        fileBackedTasksManager.createSubtask(epicTask2, subtask5); //Пересечение по времени
        System.out.println(subtask5);
        fileBackedTasksManager.createTask(newTask2); //Пересечение по времени
        System.out.println(newTask2);


        for (int i = 0; i < 2; i++) {
            fileBackedTasksManager.getEpicById(epicTask2.getId());
        }
        System.out.println("ЗАПРОС epic 2 \n" + fileBackedTasksManager.getHistory());
        for (int i = 0; i < 2; i++) {
            fileBackedTasksManager.getSubtaskById(subtask1.getId());
        }
        System.out.println("ЗАПРОС subtask 1 \n" + fileBackedTasksManager.getHistory());
        for (int i = 0; i < 2; i++) {
            fileBackedTasksManager.getTaskById(newTask1.getId());
        }
        System.out.println("ЗАПРОС task 1 \n" + fileBackedTasksManager.getHistory());
        System.out.println("СПИСОК задач\n" + fileBackedTasksManager.getListOfTasks());
        System.out.println("СПИСОК подзадач\n" + fileBackedTasksManager.getListOfSubtasks());
        System.out.println("СПИСОК эпиков\n" + fileBackedTasksManager.getListOfEpics());
        System.out.println("СПИСОК задач в порядке приоритета\n" + fileBackedTasksManager.getPrioritizedTasks());

        TaskManager newFileBackedTasksManager = loadFromFile(new File(PATH));
        System.out.println("ВОССТАНОВЛЕННЫЙ из файла менеджер FileBackedTasksManager \nИСТОРИЯ просмотров \n"
                + newFileBackedTasksManager.getHistory());
        System.out.println("ВОССТАНОВЛЕННЫЙ список задач\n" + newFileBackedTasksManager.getListOfTasks());
        System.out.println("ВОССТАНОВЛЕННЫЙ список подзадач\n" + newFileBackedTasksManager.getListOfSubtasks());
        System.out.println("ВОССТАНОВЛЕННЫЙ список эпиков\n" + newFileBackedTasksManager.getListOfEpics());
        System.out.println("СПИСОК задач в порядке приоритета\n" + fileBackedTasksManager.getPrioritizedTasks());

        Epic epicTask3 = new Epic();
        epicTask3.setTaskName("epic 3");
        newFileBackedTasksManager.createEpic(epicTask3);
        System.out.println("СОЗДАТЬ новый эпик\n" + newFileBackedTasksManager.getListOfEpics());
        newFileBackedTasksManager.getEpicById(epicTask3.getId());
        System.out.println("ЗАПРОС epic 3 \n" + newFileBackedTasksManager.getHistory());
    }

    public static final String PATH = "." + File.separator + "resources" + File.separator + "data.csv";
    public static final String HEADER = "id,type,name,status,description,start_time,duration,end_time,epic\n";
    private final File programData;

    public FileBackedTasksManager(File programData) {
        this.programData = programData;
    }

    private void save() {
        List<Task> allTasksList = new ArrayList<>(getListOfTasks());
        allTasksList.addAll(getListOfEpics());
        allTasksList.addAll(getListOfSubtasks());
        try (Writer taskWriter = new FileWriter(programData, StandardCharsets.UTF_8)) {
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

    private static String historyToString(HistoryManager manager) {
        List<String> id = new ArrayList<>();
        if (!manager.getHistory().isEmpty()) {
            for (Task task : manager.getHistory()) {
                id.add(String.valueOf(task.getId()));
            }
        }
        return String.join(",", id);
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
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

    private static List<Integer> historyFromString(String value) {
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
    public void createSubtask(Epic epic, Subtask subtask) {
        super.createSubtask(epic, subtask);
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
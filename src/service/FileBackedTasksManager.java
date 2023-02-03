package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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
        Subtask subtask1 = new Subtask();
        subtask1.setTaskName("sub 1");
        Subtask subtask2 = new Subtask();
        subtask2.setTaskName("sub 2");

        fileBackedTasksManager.createEpic(epicTask1);
        fileBackedTasksManager.createEpic(epicTask2);
        fileBackedTasksManager.createTask(newTask1);
        fileBackedTasksManager.createSubtask(epicTask1, subtask1);
        fileBackedTasksManager.createSubtask(epicTask2, subtask2);
        fileBackedTasksManager.createTask(newTask2);

        System.out.println("СОЗДАНИЕ ЗАДАЧ");
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(epicTask1);
        System.out.println(epicTask2);
        System.out.println(newTask1);
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

        FileBackedTasksManager newFileBackedTasksManager = loadFromFile(new File(PATH));
        System.out.println("ВОССТАНОВЛЕННЫЙ из файла менеджер FileBackedTasksManager \nИСТОРИЯ просмотров \n"
                + newFileBackedTasksManager.getHistory());
        System.out.println("ВОССТАНОВЛЕННЫЙ список задач\n" + newFileBackedTasksManager.getListOfTasks());
        System.out.println("ВОССТАНОВЛЕННЫЙ список подзадач\n" + newFileBackedTasksManager.getListOfSubtasks());
        System.out.println("ВОССТАНОВЛЕННЫЙ список эпиков\n" + newFileBackedTasksManager.getListOfEpics());

        Epic epicTask3 = new Epic();
        epicTask3.setTaskName("epic 3");
        newFileBackedTasksManager.createEpic(epicTask3);
        System.out.println("СОЗДАТЬ новый эпик\n" + newFileBackedTasksManager.getListOfEpics());
        newFileBackedTasksManager.getEpicById(epicTask3.getId());
        System.out.println("ЗАПРОС epic 3 \n" + newFileBackedTasksManager.getHistory());
    }

    public static final String PATH = "." + File.separator + "resources" + File.separator + "data.csv";
    public static final String HEADER = "id,type,name,status,description,epic\n";
    private final File programData;

    public FileBackedTasksManager(File programData) {
        this.programData = programData;
    }

    private void save() throws ManagerSaveException {
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

    private static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        String tasksFromFile;
        int currentId = 0;
        try {
            tasksFromFile = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось прочитать файл", e);
        }
        String[] tasksAsStrings = tasksFromFile.split("\r?\n");
        String historyIds = tasksAsStrings[tasksAsStrings.length - 1];
        Map<Integer, Task> allTasks = new HashMap<>();
        for (int i = 1; !tasksAsStrings[i].equals(""); i++) {
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
        for (Integer id : historyFromString(historyIds)) {
            if (allTasks.containsKey(id)) {
                fileBackedTasksManager.getHistoryManager().add(allTasks.get(id));
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
        save();
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
        save();
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
        save();
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
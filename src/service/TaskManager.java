package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    private HashMap<Integer, Task> tasksList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();
    private HashMap<Integer, ArrayList<Subtask>> subtaskList = new HashMap<>();
    private static int id = 0;

    public HashMap<Integer, Task> getTasksList() {
        return tasksList;
    }

    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    public HashMap<Integer, ArrayList<Subtask>> getSubtaskList() {
        return subtaskList;
    }

    public void createTask(Task task) {
        task.setId(++id);
        tasksList.put(id, task);
    }

    public void updateTask(Task task) {
        if (tasksList.containsKey(task.getId())) {
            tasksList.put(task.getId(), task);
        }
    }

    public ArrayList<String> getListOfTasks() {
        ArrayList<String> tasksList = new ArrayList<>();

        if (!this.tasksList.isEmpty()) {
            for (Map.Entry<Integer, Task> name : this.tasksList.entrySet()) {
                tasksList.add(name.getValue().getTaskName());
            }
        }
        return tasksList;
    }

    public Task getTaskById(int id) {
        Task task = new Task();

        if (!tasksList.isEmpty()) {
            if (tasksList.containsKey(id)) {
                task = tasksList.get(id);
            }
        }
        return task;
    }

    public void deleteTaskById(int id) {

        tasksList.remove(id);
    }

    public void clearTasksList() {

        tasksList.clear();
    }

    public void createEpic(Epic epic) {
        epic.setId(++id);
        epicList.put(id, epic);
    }

    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            epicList.put(epic.getId(), epic);
        }
    }

    public ArrayList<String> getListOfEpics() {
        ArrayList<String> tasksList = new ArrayList<>();

        if (!this.epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> epic : epicList.entrySet()) {
                tasksList.add(epic.getValue().getTaskName());
            }
        }
        return tasksList;
    }

    public Epic getEpicById(int id) {
        Epic epic = null;

        if (!epicList.isEmpty()) {
            if (epicList.containsKey(id)) {
                epic = epicList.get(id);
            }
        }
        return epic;
    }

    public ArrayList<Subtask> getEpicsSubtaskList(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (subtaskList.containsKey(epicId)) {
            subtasks = subtaskList.get(epicId);
        }
        return subtasks;
    }

    public void deleteEpicById(int id) {

        epicList.remove(id);
        subtaskList.remove(id);
    }

    public void clearEpicList() {

        epicList.clear();
        subtaskList.clear();
    }

    public void createSubtask(Epic epic, Subtask subtask) {
        subtask.setId(++id);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (subtaskList.get(epic.getId()) != null) {
            subtasks = subtaskList.get(epic.getId());
        }
        subtasks.add(subtask);
        subtaskList.put(epic.getId(), subtasks);
    }

    public void updateSubtask(Subtask subtask) {
        int epicId = 0;
        for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
            ArrayList<Subtask> tasks = subtasks.getValue();
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == subtask.getId()) {
                    tasks.set(i, subtask);
                    epicId = subtasks.getKey();
                    break;
                }
            }
        }
        System.out.println(epicId);
        updateEpicStatus(epicId);

    }

    public void updateEpicStatus(int epicId) {

        Epic epicForUpdate = epicList.get(epicId);
    }

    public ArrayList<String> getListOfSubtasks() {
        ArrayList<String> tasksList = new ArrayList<>();

        if (!this.subtaskList.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
                ArrayList<Subtask> tasks = subtasks.getValue();
                for (Subtask task : tasks) {
                    tasksList.add(task.getTaskName());
                }
            }
        }
        return tasksList;
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = null;

        if (!subtaskList.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
                ArrayList<Subtask> tasks = subtasks.getValue();
                for (Subtask task : tasks) {
                    if (task.getId() == id) {
                        subtask = task;
                    }
                }
            }
        }
        return subtask;
    }

    public void deleteSubtaskById(int id) {

        if (!subtaskList.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
                ArrayList<Subtask> tasks = subtasks.getValue();
                for (Subtask task : tasks) {
                    if (task.getId() == id) {
                        tasks.remove(task);
                        break;
                    }
                }
            }
        }
    }

    public void clearSubtaskList() {

        subtaskList.clear();
    }
}

package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager{
    private final HashMap<Integer, Task> tasksList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, ArrayList<Subtask>> subtaskList = new HashMap<>();
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

    @Override
    public void createTask(Task task) {
        task.setId(++id);
        tasksList.put(id, task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasksList.containsKey(task.getId())) {
            tasksList.put(task.getId(), task);
        }
    }

    @Override
    public ArrayList<String> getListOfTasks() {
        ArrayList<String> tasksList = new ArrayList<>();

        if (!this.tasksList.isEmpty()) {
            for (Map.Entry<Integer, Task> name : this.tasksList.entrySet()) {
                tasksList.add(name.getValue().getTaskName());
            }
        }
        return tasksList;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = new Task();

        if (!tasksList.isEmpty()) {
            if (tasksList.containsKey(id)) {
                task = tasksList.get(id);
            }
        }
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        tasksList.remove(id);
    }

    @Override
    public void clearTasksList() {
        tasksList.clear();
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++id);
        epicList.put(id, epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            epicList.put(epic.getId(), epic);
        }
    }

    @Override
    public ArrayList<String> getListOfEpics() {
        ArrayList<String> tasksList = new ArrayList<>();

        if (!this.epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> epic : epicList.entrySet()) {
                tasksList.add(epic.getValue().getTaskName());
            }
        }
        return tasksList;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = null;

        if (!epicList.isEmpty()) {
            if (epicList.containsKey(id)) {
                epic = epicList.get(id);
            }
        }
        return epic;
    }

    @Override
    public ArrayList<Subtask> getEpicsSubtaskList(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (subtaskList.containsKey(epicId)) {
            subtasks = subtaskList.get(epicId);
        }
        return subtasks;
    }

    @Override
    public void deleteEpicById(int id) {
        epicList.remove(id);
        subtaskList.remove(id);
    }

    @Override
    public void clearEpicList() {
        epicList.clear();
        subtaskList.clear();
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        subtask.setId(++id);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (subtaskList.get(epic.getId()) != null) {
            subtasks = subtaskList.get(epic.getId());
        }
        subtasks.add(subtask);
        subtaskList.put(epic.getId(), subtasks);
        updateEpicStatus(epic.getId());
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
            ArrayList<Subtask> tasks = subtasks.getValue();
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == subtask.getId()) {
                    tasks.set(i, subtask);
                    int epicId = subtasks.getKey();
                    updateEpicStatus(epicId);
                    break;
                }
            }
        }
    }

    @Override
    public void updateEpicStatus(int epicId) {
        ArrayList<String> statuses = new ArrayList<>();

        if (subtaskList.get(epicId).isEmpty()) {
            epicList.get(epicId).setStatus(Status.NEW);
        } else {
            for (Subtask task : subtaskList.get(epicId)) {
                if (task.getStatus().equals(Status.IN_PROGRESS)) {
                    statuses.add("in_progress");
                    break;
                } else if (task.getStatus().equals(Status.NEW)) {
                    statuses.add("new");
                } else if (task.getStatus().equals(Status.DONE)) {
                    statuses.add("done");
                }
            }
            if (statuses.contains("in_progress")) {
                epicList.get(epicId).setStatus(Status.IN_PROGRESS);
            } else if (statuses.contains("new") && !statuses.contains("done")) {
                epicList.get(epicId).setStatus(Status.NEW);
            } else if (!statuses.contains("new") && statuses.contains("done")) {
                epicList.get(epicId).setStatus(Status.DONE);
            } else {
                epicList.get(epicId).setStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
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

    @Override
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

    @Override
    public void deleteSubtaskById(int id) {
        if (!subtaskList.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
                ArrayList<Subtask> tasks = subtasks.getValue();
                for (Subtask task : tasks) {
                    if (task.getId() == id) {
                        tasks.remove(task);
                        int epicId = subtasks.getKey();
                        updateEpicStatus(epicId);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void clearSubtaskList() {
        subtaskList.clear();
        if (!epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> epic : epicList.entrySet()) {
                epic.getValue().setStatus(Status.NEW);
            }
        }
    }
}

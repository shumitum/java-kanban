package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, ArrayList<Subtask>> subtaskList = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private static int id = 0;

    private void updateEpicStatus(int epicId) {
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

    private void setEpicStartTime(int epicId) {
        if (!subtaskList.get(epicId).isEmpty()) {
            subtaskList.get(epicId).stream()
                    .filter(subtask -> subtask.getStartTime() != null)
                    .min(Comparator.comparing(Task::getStartTime))
                    .ifPresent(subtask -> epicList.get(epicId).setStartTime(subtask.getStartTime()));
        }
    }

    private void updateEpicDuration(int epicId) {
        if (!subtaskList.get(epicId).isEmpty()) {
            int epicDuration = 0;
            for (Subtask task : subtaskList.get(epicId)) {
                epicDuration += task.getDuration();
            }
            epicList.get(epicId).setDuration(epicDuration);
        }
    }

    private void updateEpicEndTime(int epicId) {
        if (!subtaskList.get(epicId).isEmpty()) {
            subtaskList.get(epicId).stream()
                    .filter(subtask -> subtask.getEndTime() != null)
                    .max(Comparator.comparing(Task::getEndTime))
                    .ifPresent(subtask -> epicList.get(epicId).setEndTime(subtask.getEndTime()));
        }
    }

    private void taskTimeIntersectionCheck(Task task) throws IllegalArgumentException {
        for (Task currentTask : getPrioritizedTasks()) {
            if (task.getStartTime() != null
                    && currentTask.getStartTime() != null
                    && task.getEndTime() != null
                    && currentTask.getEndTime() != null) {
                if ((task.getStartTime().isAfter(currentTask.getStartTime())
                        && task.getStartTime().isBefore(currentTask.getEndTime()))
                        || (task.getEndTime().isAfter(currentTask.getStartTime())
                        && task.getEndTime().isBefore(currentTask.getEndTime()))
                        || (task.getStartTime().isBefore(currentTask.getStartTime())
                        & task.getEndTime().isAfter(currentTask.getEndTime()))) {
                    throw new IllegalArgumentException("Задачи не должны пересекаться по времени выполнения");
                }
            }
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> PrioritizedTasksList = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime() == null) {
                if (task2.getStartTime() == null) {
                    return task1.getId() - task2.getId();
                } else {
                    return 1;
                }
            } else if (task2.getStartTime() == null) {
                return -1;
            }
            return task1.getStartTime().compareTo(task2.getStartTime());

        });
        PrioritizedTasksList.addAll(getListOfTasks());
        PrioritizedTasksList.addAll(getListOfSubtasks());
        return PrioritizedTasksList;
    }

    public static void setId(int id) {
        InMemoryTaskManager.id = id;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createTask(Task task) {
        taskTimeIntersectionCheck(task);
        task.setId(++id);
        tasksList.put(id, task);
    }

    public void createTask(int taskId, Task task) {
        taskTimeIntersectionCheck(task);
        tasksList.put(taskId, task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasksList.containsKey(task.getId())) {
            taskTimeIntersectionCheck(task);
            tasksList.put(task.getId(), task);
            historyManager.add(task);
        }
    }

    @Override
    public ArrayList<Task> getListOfTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();

        if (!this.tasksList.isEmpty()) {
            for (Map.Entry<Integer, Task> task : this.tasksList.entrySet()) {
                tasksList.add(task.getValue());
            }
        }
        return tasksList;
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasksList.isEmpty()) {
            if (tasksList.containsKey(id)) {
                historyManager.add(tasksList.get(id));
                return tasksList.get(id);
            }
        }
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        tasksList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void clearTasksList() {
        if (!tasksList.isEmpty()) {
            for (Integer key : tasksList.keySet()) {
                historyManager.remove(key);
            }
            tasksList.clear();
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++id);
        epicList.put(id, epic);
    }

    public void createEpic(int epicId, Epic epic) {
        epicList.put(epicId, epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            epicList.put(epic.getId(), epic);
            historyManager.add(epic);
        }
    }

    @Override
    public ArrayList<Epic> getListOfEpics() {
        ArrayList<Epic> tasksList = new ArrayList<>();

        if (!this.epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> epic : epicList.entrySet()) {
                tasksList.add(epic.getValue());
            }
        }
        return tasksList;
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epicList.isEmpty()) {
            if (epicList.containsKey(id)) {
                historyManager.add(epicList.get(id));
                return epicList.get(id);
            }
        }
        return null;
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
        if (subtaskList.containsKey(id)) {
            historyManager.remove(id);
            for (Subtask subtask : subtaskList.get(id)) {
                historyManager.remove(subtask.getId());
            }
        }
        epicList.remove(id);
        subtaskList.remove(id);
    }

    @Override
    public void clearEpicList() {
        if (!subtaskList.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
                for (Subtask task : subtasks.getValue()) {
                    historyManager.remove(task.getId());
                }
            }
        }
        if (!epicList.isEmpty()) {
            for (Integer epicID : epicList.keySet()) {
                historyManager.remove(epicID);
            }
        }
        subtaskList.clear();
        epicList.clear();
    }

    @Override
    public void createSubtask(Epic epic, Subtask subtask) {
        taskTimeIntersectionCheck(subtask);
        subtask.setId(++id);
        subtask.setEpicId(epic.getId());
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (subtaskList.get(epic.getId()) != null) {
            subtasks = subtaskList.get(epic.getId());
        }
        subtasks.add(subtask);
        subtaskList.put(epic.getId(), subtasks);
        updateEpicStatus(epic.getId());
        setEpicStartTime(epic.getId());
        updateEpicDuration(epic.getId());
        updateEpicEndTime(epic.getId());
    }

    public void createSubtask(int epicId, Subtask subtask) {
        taskTimeIntersectionCheck(subtask);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (subtaskList.get(epicId) != null) {
            subtasks = subtaskList.get(epicId);
        }
        subtasks.add(subtask);
        subtaskList.put(epicId, subtasks);
        updateEpicStatus(epicId);
        setEpicStartTime(epicId);
        updateEpicDuration(epicId);
        updateEpicEndTime(epicId);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        taskTimeIntersectionCheck(subtask);
        for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
            ArrayList<Subtask> tasks = subtasks.getValue();
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == subtask.getId()) {
                    tasks.set(i, subtask);
                    int epicId = subtasks.getKey();
                    updateEpicStatus(epicId);
                    setEpicStartTime(epicId);
                    updateEpicDuration(epicId);
                    updateEpicEndTime(epicId);
                    historyManager.add(subtask);
                    break;
                }
            }
        }
    }

    @Override
    public ArrayList<Subtask> getListOfSubtasks() {
        ArrayList<Subtask> tasksList = new ArrayList<>();

        if (!this.subtaskList.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
                ArrayList<Subtask> tasks = subtasks.getValue();
                tasksList.addAll(tasks);
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
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!subtaskList.isEmpty()) {
            historyManager.remove(id);
            for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
                ArrayList<Subtask> tasks = subtasks.getValue();
                for (Subtask task : tasks) {
                    if (task.getId() == id) {
                        tasks.remove(task);
                        int epicId = subtasks.getKey();
                        updateEpicStatus(epicId);
                        setEpicStartTime(epicId);
                        updateEpicDuration(epicId);
                        updateEpicEndTime(epicId);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void clearSubtaskList() {
        if (!subtaskList.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<Subtask>> subtasks : subtaskList.entrySet()) {
                for (Subtask task : subtasks.getValue()) {
                    historyManager.remove(task.getId());
                }
            }
            subtaskList.clear();
            if (!epicList.isEmpty()) {
                for (Map.Entry<Integer, Epic> epic : epicList.entrySet()) {
                    epic.getValue().setStatus(Status.NEW);
                    epic.getValue().setStartTime(null);
                    epic.getValue().setDuration(0);
                    epic.getValue().setEndTime(null);
                }
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
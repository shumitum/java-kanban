package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public interface TaskManager {

    Set<Task> getPrioritizedTasks();

    void createTask(Task task);

    void updateTask(Task task);

    ArrayList<Task> getListOfTasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void clearTasksList();

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    ArrayList<Epic> getListOfEpics();

    Epic getEpicById(int id);

    ArrayList<Subtask> getEpicsSubtaskList(int epicId);

    void deleteEpicById(int id);

    void clearEpicList();

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    ArrayList<Subtask> getListOfSubtasks();

    Subtask getSubtaskById(int id);

    void deleteSubtaskById(int id);

    void clearSubtaskList();

    List<Task> getHistory();
}
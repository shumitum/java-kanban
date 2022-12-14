package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;


public interface TaskManager {

    void createTask(Task task);

    void updateTask(Task task);

    ArrayList<String> getListOfTasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void clearTasksList();

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    ArrayList<String> getListOfEpics();

    Epic getEpicById(int id);

    ArrayList<Subtask> getEpicsSubtaskList(int epicId);

    void deleteEpicById(int id);

    void clearEpicList();

    void createSubtask(Epic epic, Subtask subtask);

    void updateSubtask(Subtask subtask);

    void updateEpicStatus(int epicId);

    ArrayList<String> getListOfSubtasks();

    Subtask getSubtaskById(int id);

    void deleteSubtaskById(int id);

    void clearSubtaskList();
}
package service;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    Task task;
    Subtask subtask;
    Epic epic;

    abstract T getRightTypeOfManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = getRightTypeOfManager();
        task = new Task();
        subtask = new Subtask();
        epic = new Epic();
        InMemoryTaskManager.setId(0);
    }

    @AfterEach
    public void afterEach() {
        taskManager = null;
        task = null;
        subtask = null;
        epic = null;
    }

    @Test
    void getPrioritizedTasks() {
        taskManager.createEpic(epic);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(60);
        taskManager.createTask(task);
        subtask.setStartTime(LocalDateTime.now().minusHours(2));
        subtask.setDuration(60);
        taskManager.createSubtask(epic, subtask);
        Task newTask = new Task();
        newTask.setStartTime(LocalDateTime.now().minusHours(4));
        newTask.setDuration(60);
        taskManager.createTask(newTask);

        final List<Task> prioritizedTasks = new ArrayList<>(taskManager.getPrioritizedTasks());
        assertNotNull(prioritizedTasks, "Список задач не должен быть пустым");
        assertEquals(4, prioritizedTasks.get(0).getId());
        assertEquals(TaskType.TASK, prioritizedTasks.get(0).getTaskType());
        assertEquals(3, prioritizedTasks.get(1).getId());
        assertEquals(TaskType.SUBTASK, prioritizedTasks.get(1).getTaskType());
        assertEquals(2, prioritizedTasks.get(2).getId());
        assertEquals(TaskType.TASK, prioritizedTasks.get(2).getTaskType());
    }

    @Test
    void createTask() {
        taskManager.createTask(task);
        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }


    @Test
    void updateTask() {
        taskManager.createTask(task);
        final String taskName = task.getTaskName();
        task.setTaskName("task 1");
        taskManager.updateTask(task);
        final String updatedTaskName = task.getTaskName();
        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");

        final List<Task> tasks = taskManager.getListOfTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotEquals(taskName, updatedTaskName, "Имя обновленной задачи должно отличаться");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getListOfTasks() {
        final List<Task> emptyTasksList = taskManager.getListOfTasks();
        assertEquals(emptyTasksList.size(), 0, "Список задач должен быть пустым.");

        taskManager.createTask(task);
        final List<Task> tasksList = taskManager.getListOfTasks();

        assertNotNull(tasksList, "Список задач пуст");
        assertEquals(1, tasksList.size(), "Список задач не должен быть пустым.");
        assertEquals(TaskType.TASK, tasksList.get(0).getTaskType(), "Тип задачи не совпадает");
    }

    @Test
    void getTaskById() {
        taskManager.createTask(task);
        Task nullTask = taskManager.getTaskById(5);

        assertEquals(task, taskManager.getTaskById(task.getId()), "Задачи не совпадают");
        assertNull(nullTask, "Задача должна быть null");
        assertNotNull(taskManager.getTaskById(1), "Задачи с ID = 1 нет в списке задач");
    }

    @Test
    void deleteTaskById() {
        taskManager.createTask(task);
        taskManager.deleteTaskById(6);

        assertEquals(1, taskManager.getListOfTasks().size(), "В списке должна быть 1 задача");

        taskManager.createTask(new Task());
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1), "Задачи с идентификатором 1 не должно быть в списке");
        assertEquals(1, taskManager.getListOfTasks().size(), "В списке должна остаться одна задача");
    }

    @Test
    void clearTasksList() {
        taskManager.createTask(task);
        taskManager.clearTasksList();

        assertEquals(0, taskManager.getListOfTasks().size(), "В списке не должно остаться задач");
    }

    @Test
    void createEpic() {
        taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getListOfEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void updateEpic() {
        taskManager.createEpic(epic);
        final String epicName = epic.getTaskName();
        epic.setTaskName("epic 1");
        taskManager.updateEpic(epic);
        final String updatedEpicName = epic.getTaskName();
        final Task savedTask = taskManager.getEpicById(epic.getId());

        assertNotNull(savedTask, "Эпик не найден.");

        final List<Epic> epics = taskManager.getListOfEpics();

        assertNotNull(savedTask, "Эпик не найден.");
        assertNotEquals(epicName, updatedEpicName, "Имя обновленного эпика должно отличаться");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void getListOfEpics() {
        final List<Epic> emptyEpicsList = taskManager.getListOfEpics();
        assertEquals(emptyEpicsList.size(), 0, "Список эпиков должен быть пустым.");

        taskManager.createEpic(epic);
        final List<Epic> epics = taskManager.getListOfEpics();

        assertNotNull(epics, "Список эпиков пуст");
        assertEquals(1, epics.size(), "Список эпиков не должен быть пустым.");
        assertEquals(TaskType.EPIC, epics.get(0).getTaskType(), "Тип задачи не совпадает");
    }

    @Test
    void getEpicById() {
        taskManager.createEpic(epic);
        Epic nullEpic = taskManager.getEpicById(5);

        assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпики не совпадают");
        assertNull(nullEpic, "Эпик должен быть null");
        assertNotNull(taskManager.getEpicById(1), "Эпика с ID = 1 нет в списке эпиков");
    }

    @Test
    void getEpicsSubtaskList() {
        final ArrayList<Subtask> emptySubtasksList = taskManager.getEpicsSubtaskList(1);
        assertEquals(0, emptySubtasksList.size(), "Список подзадач должен быть пуст");

        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);

        final ArrayList<Subtask> subtasks = taskManager.getEpicsSubtaskList(1);
        assertEquals(1, subtasks.size(), "В списке подзадач должна быть одна задача");
        assertEquals(2, subtasks.get(0).getId(), "ID подзадачи должен быть 2");

        final ArrayList<Subtask> subtasks2 = taskManager.getEpicsSubtaskList(6);
        assertEquals(0, subtasks2.size(), "Список подзадач должен быть пуст");
    }

    @Test
    void deleteEpicById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);

        taskManager.deleteEpicById(5);
        assertEquals(1, taskManager.getEpicsSubtaskList(1).size(), "В списке подзадач должна быть одна подзадача");
        assertEquals(1, taskManager.getListOfEpics().size(), "В списке эпиков должна быть один эпик");

        taskManager.deleteEpicById(1);
        assertEquals(0, taskManager.getEpicsSubtaskList(1).size(), "Список подзадач должен быть пуст");
        assertEquals(0, taskManager.getListOfEpics().size(), "Список Эпиков должен быть пуст");
    }

    @Test
    void clearEpicList() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);
        taskManager.clearEpicList();

        assertEquals(0, taskManager.getEpicsSubtaskList(1).size(), "Список подзадач должен быть пуст");
        assertEquals(0, taskManager.getListOfEpics().size(), "Список Эпиков должен быть пуст");
    }

    @Test
    void createSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(2);
        final Epic savedEpic = taskManager.getEpicById(1);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertNotNull(savedEpic, "Эпик не найден.");

        final List<Subtask> subtasks = taskManager.getListOfSubtasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void updateSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);
        final String subtaskName = epic.getTaskName();
        subtask.setTaskName("epic 1");
        taskManager.updateSubtask(subtask);
        final String updatedSubtaskName = subtask.getTaskName();
        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        final Epic savedEpic = taskManager.getEpicById(1);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertNotNull(savedEpic, "Эпик не найден.");
        assertNotEquals(subtaskName, updatedSubtaskName, "Имя обновленной подзадачи должно отличаться");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void getListOfSubtasks() {
        final List<Subtask> emptySubtaskList = taskManager.getListOfSubtasks();
        assertEquals(emptySubtaskList.size(), 0, "Список подзадач должен быть пустым.");

        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);
        final List<Subtask> subtaskList = taskManager.getListOfSubtasks();

        assertNotNull(subtaskList, "Список подзадач пуст");
        assertEquals(1, subtaskList.size(), "Список подзадач не должен быть пустым.");
        assertEquals(TaskType.SUBTASK, subtaskList.get(0).getTaskType(), "Тип задачи не совпадает");
        assertNotNull(taskManager.getEpicById(epic.getId()), "Эпик не найден.");
    }

    @Test
    void getSubtaskById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);
        Subtask nullSubtask = taskManager.getSubtaskById(7);

        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадачи не совпадают");
        assertNotNull(taskManager.getSubtaskById(2), "Подзадачи нет в списке");
        assertNull(nullSubtask, "Подзадачи не должно в списке");
        assertNotNull(taskManager.getEpicById(epic.getId()), "Эпик не найден.");
    }

    @Test
    void deleteSubtaskById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);
        taskManager.deleteSubtaskById(5);

        assertNotNull(taskManager.getSubtaskById(subtask.getId()), "Подзадачи нет в списке эпиков");
        assertEquals(1, taskManager.getListOfSubtasks().size(), "В списке подзадач должна быть одна подзадача");
        assertEquals(1, taskManager.getListOfEpics().size(), "В списке эпиков должна быть один эпик");

        taskManager.deleteSubtaskById(subtask.getId());
        assertEquals(0, taskManager.getListOfSubtasks().size(), "Список подзадач должен быть пуст");
        assertEquals(1, taskManager.getListOfEpics().size(), "В списке эпиков должна быть один эпик");
    }

    @Test
    void clearSubtaskList() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);
        subtask.setStartTime(LocalDateTime.now());
        subtask.setDuration(120);
        taskManager.updateSubtask(subtask);
        taskManager.clearSubtaskList();

        assertEquals(0, taskManager.getListOfSubtasks().size(), "Список подзадач должен быть пуст");
        assertEquals(1, taskManager.getListOfEpics().size(), "В списке должен остаться один Эпик");
        assertEquals(0, epic.getDuration(), "Продолжительность эпика должна быть равна нулю");
        assertNull(epic.getEndTime(), "Продолжительность эпика должна быть равна нулю");
        assertNull(epic.getStartTime(), "Продолжительность эпика должна быть равна нулю");
    }

    @Test
    void getHistory() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);
        final List<Task> emptyHistory = taskManager.getHistory();

        assertEquals(0, emptyHistory.size(), "История просмотров должна быть пустой");

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        final List<Task> history = taskManager.getHistory();

        assertEquals(3, history.size(), "В истории просмотров должно быть 3 задачи");
    }

    @Test
    void epicStatusShouldBeNewWithEmptySubtaskList() {
        taskManager.createEpic(epic);

        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть NEW");
    }

    @Test
    void epicStatusShouldBeNewAllSubtasksStatusesNew() {
        Subtask subtask1 = new Subtask();
        taskManager.createEpic(epic);
        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask1);

        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть NEW");
    }

    @Test
    void epicStatusShouldBeDoneAllSubtasksStatusesDone() {
        Subtask subtask1 = new Subtask();
        taskManager.createEpic(epic);
        subtask1.setStatus(Status.DONE);
        subtask.setStatus(Status.DONE);
        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask1);

        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть DONE");
    }

    @Test
    void epicStatusShouldBeDone() {
        Subtask subtask1 = new Subtask();
        taskManager.createEpic(epic);
        subtask1.setStatus(Status.DONE);
        subtask.setStatus(Status.NEW);
        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask1);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void epicStatusShouldBeInProgress() {
        Subtask subtask1 = new Subtask();
        taskManager.createEpic(epic);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.createSubtask(epic, subtask);
        taskManager.createSubtask(epic, subtask1);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика должен быть IN_PROGRESS");
    }
}
package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    TaskManager taskManager;
    Task task;
    Subtask subtask;
    Epic epic;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager();
        task = new Task();
        subtask = new Subtask();
        epic = new Epic();
    }

    @AfterEach
    public void afterEach() {
        taskManager = null;
        task = null;
        subtask = null;
        epic = null;
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void getHistory() {
        final List<Task> emptyHistory = historyManager.getHistory();
        assertEquals(0, emptyHistory.size(), "История просмотров должна быть пустой");

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask.setEpicId(epic.getId());
        taskManager.createSubtask(subtask);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size(), "В истории просмотров должно быть 3 задачи");
    }

    @Test
    void remove() {
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        subtask.setEpicId(epic.getId());
        taskManager.createSubtask(subtask);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size(), "В истории просмотров должно быть 3 задачи");
        historyManager.remove(task.getId());
        assertEquals(2, historyManager.getHistory().size(), "В истории просмотров должно быть 2 задачи");
        historyManager.remove(subtask.getId());
        assertEquals(1, historyManager.getHistory().size(), "В истории просмотров должна быть 1 задача");
        historyManager.remove(epic.getId());
        assertEquals(0, historyManager.getHistory().size(), "В истории просмотров не должно остаться задач");
    }
}
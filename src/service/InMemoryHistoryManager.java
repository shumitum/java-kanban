package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (!(task == null)) {
            history.add(task);
        }
        if (history.size() == 11) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
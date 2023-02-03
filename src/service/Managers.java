package service;

import java.io.File;

import static service.FileBackedTasksManager.PATH;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File(PATH));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}